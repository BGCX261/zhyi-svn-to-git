/*
 * Copyright (C) 2011 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zv.ui.dialog;

import zhyi.zv.common.Options;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;
import zhyi.zse.io.FileHelper;
import zhyi.zse.io.FileType;
import zhyi.zse.swing.ClosableTabbedPane;
import zhyi.zse.zip.ZipItem;
import zhyi.zse.swing.ExceptionDialog;
import zhyi.zse.swing.SwingHelper;
import zhyi.zse.zip.ZipSystem;
import zhyi.zv.ui.viewer.ViewerContainer;
import zhyi.zv.ui.viewer.ViewerType;
import zhyi.zv.ui.zip.ZipItemNode;
import zhyi.zv.ui.zip.ZipTree;

/**
 * The main frame.
 * @author Zhao Yi
 */
public class MainFrame extends JFrame {
    private Options options;
    private Map<ZipItemNode, ViewerContainer> tabIndexMap;

    private ZipTree zipTree;
    private ClosableTabbedPane viewerTabbedPane;
    private JFileChooser zipFileChooser;
    private OptionsDialog optionsDialog;
    private ZipItemPropertiesDialog zipItemPropertiesDialog;

    public MainFrame() {
        super("Zip Viewer");
        options = Options.getInstance();
        tabIndexMap = new IdentityHashMap<>();

        initComponents();
        initDialogs();

        FileType zipFileType = FileType.getType("zip");
        setIconImages(Arrays.asList(zipFileType.getSmallIcon().getImage(),
                zipFileType.getLargeIcon().getImage()));
        setSize(960, 540);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    options.persist();
                } catch (Exception ex) {
                    ExceptionDialog.showException(ex, MainFrame.this);
                }
            }
        });
    }

    private void initComponents() {
        initMenuBar();
        initZipTree();
        viewerTabbedPane = new ClosableTabbedPane() {
            @Override
            public void removeTabAt(int index) {
                for (Entry<ZipItemNode, ViewerContainer> entry : tabIndexMap.entrySet()) {
                    if (indexOfComponent(entry.getValue()) == index) {
                        tabIndexMap.remove(entry.getKey());
                        break;
                    }
                }
                super.removeTabAt(index);
            }
        };

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(zipTree), viewerTabbedPane);
        splitPane.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);
        final int padding = 10;
        splitPane.setBorder(BorderFactory.createEmptyBorder(
                padding, padding, padding, padding));
        add(splitPane, BorderLayout.CENTER);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = menuBar.add(SwingHelper.createMenu("File", KeyEvent.VK_F));
        fileMenu.add(SwingHelper.createMenuItem("Open...", KeyEvent.VK_O,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK),
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openZipFile();
                } catch (IOException ex) {
                    ExceptionDialog.showException(ex, MainFrame.this);
                }
            }
        }));
        fileMenu.add(SwingHelper.createMenuItem("Close All", KeyEvent.VK_C,
                null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewerTabbedPane.removeAll();
                zipTree.closeAll();
            }
        }));
        fileMenu.addSeparator();
        fileMenu.add(SwingHelper.createMenuItem("Options...", KeyEvent.VK_P,
                null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingHelper.showWindow(optionsDialog);
            }
        }));
        fileMenu.addSeparator();
        fileMenu.add(SwingHelper.createMenuItem("Exit", KeyEvent.VK_X,
                null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }));

        JMenu helpMenu = menuBar.add(SwingHelper.createMenu("Help", KeyEvent.VK_H));
        helpMenu.add(SwingHelper.createMenuItem("About...", KeyEvent.VK_A,
                null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "<html><h3>Zip Viewer 1.0</h3>Copyright &copy; 2011 Zhao Yi<br>"
                        + "Licensed under GNU GPL Version 3.</html>",
                        "About Zip Viewer", JOptionPane.INFORMATION_MESSAGE,
                        FileType.getType("zip").getLargeIcon());
            }
        }));
    }

    private void initZipTree() {
        zipTree = new ZipTree();

        final JMenuItem viewMenuItem = SwingHelper.createMenuItem(
                "View", KeyEvent.VK_V, null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewSelectedNodeAs(null);
            }
        });

        final JMenuItem closeMenuItem = SwingHelper.createMenuItem(
                "Close", KeyEvent.VK_R, null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object o = zipTree.getSelectionPath().getLastPathComponent();
                if (o instanceof ZipItemNode) {
                    ZipItemNode node = (ZipItemNode) o;
                    List<ViewerContainer> vcs = new ArrayList<>();
                    // Two loops are necessary to avoid concurrent modification
                    // to `tabIndexMap'.
                    for (Entry<ZipItemNode, ViewerContainer> entry : tabIndexMap.entrySet()) {
                        if (entry.getKey().isNodeAncestor(node)) {
                            vcs.add(entry.getValue());
                        }
                    }
                    for (ViewerContainer vc : vcs) {
                        viewerTabbedPane.remove(vc);
                    }
                    zipTree.close(node);
                }
            }
        });

        final JMenu viewAsMenu = SwingHelper.createMenu("View As", KeyEvent.VK_A);
        for (final ViewerType vt : ViewerType.values()) {
            viewAsMenu.add(SwingHelper.createMenuItem(
                    vt.toString(), 0, null, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewSelectedNodeAs(vt);
                }
            }));
        }

        final JMenuItem propertiesMenuItem = SwingHelper.createMenuItem(
                "Properties...", KeyEvent.VK_R, null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    zipItemPropertiesDialog.showZipItem(((ZipItemNode)
                            zipTree.getSelectionPath().getLastPathComponent()).getZipItem());
                } catch (IOException ex) {
                    ExceptionDialog.showException(ex, MainFrame.this);
                }
            }
        });

        zipTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!e.isPopupTrigger() && e.getClickCount() > 1) {
                    TreePath tp = zipTree.getSelectionPath();
                    if (tp != null) {
                        Object o = tp.getLastPathComponent();
                        if (o instanceof ZipItemNode) {
                            ZipItemNode node = (ZipItemNode) o;
                            // Double-click a zip system node should expand
                            // instead of view it.
                            if (!(node.getZipItem() instanceof ZipSystem)) {
                                viewSelectedNodeAs(null);
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = zipTree.getRowForLocation(e.getX(), e.getY());
                    if (row != -1) {
                        zipTree.setSelectionRow(row);
                        Object node = zipTree.getSelectionPath().getLastPathComponent();
                        // Avoid the loading node.
                        if (node instanceof ZipItemNode) {
                            JPopupMenu zipTreePopupMenu = new JPopupMenu();
                            ZipItem zi = ((ZipItemNode) node).getZipItem();
                            if (!zi.isDirectory()) {
                                zipTreePopupMenu.add(viewMenuItem);
                                zipTreePopupMenu.add(viewAsMenu);
                                zipTreePopupMenu.addSeparator();
                            }
                            if (zi.getOwner() == null) {
                                zipTreePopupMenu.add(closeMenuItem);
                                zipTreePopupMenu.addSeparator();
                            }
                            zipTreePopupMenu.add(propertiesMenuItem);
                            zipTreePopupMenu.show(zipTree, e.getX(), e.getY());
                        }
                    }
                }
            }
        });
    }

    private void initDialogs() {
        zipFileChooser = new JFileChooser();
        zipFileChooser.setAcceptAllFileFilterUsed(true);
        zipFileChooser.setFileFilter(new FileNameExtensionFilter(
                "Zip Archives", "zip", "jar", "war", "ear", "docx", "xlsx", "pptx"));
        optionsDialog = new OptionsDialog(this);
        zipItemPropertiesDialog = new ZipItemPropertiesDialog(this);
    }

    private void openZipFile() throws IOException {
        if (zipFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            zipTree.open(new ZipSystem(
                    zipFileChooser.getSelectedFile(), options.getCharset()));
        }
    }

    private void viewSelectedNodeAs(ViewerType vt) {
        Object o = zipTree.getSelectionPath().getLastPathComponent();
        if (!(o instanceof ZipItemNode)) {
            return;
        }

        ZipItemNode node = (ZipItemNode) o;
        ZipItem zi = node.getZipItem();
        if (!zi.isDirectory()) {
            try {
                if (zi.getZipEntry().getSize() > 128 * 1024) {
                    int option = JOptionPane.showConfirmDialog(this,
                            "The selected zip item is too large (>128KB).\n"
                            + "Do you still want to view it?",
                            "Zip Item Too Large", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (option == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            } catch (IOException ex) {
                ExceptionDialog.showException(ex, this);
                return;
            }

            if (vt == null) {
                String extension = FileHelper.getExtension(
                    node.getZipItem().getRelativePath());
                    vt = options.getDefaultViewerType();
                    for (Entry<ViewerType, List<String>> e
                            : options.getVtftMap().entrySet()) {
                        if (e.getValue().contains(extension)) {
                            vt = e.getKey();
                            break;
                        }
                    }
            }
            view(node, vt);
        }
    }

    private void view(ZipItemNode node, ViewerType vt) {
        if (!tabIndexMap.containsKey(node)) {
            ZipItem zi = node.getZipItem();
            try {
                ViewerContainer vc = new ViewerContainer(zi, vt, options.getCharset());
                viewerTabbedPane.addTab(zi.getName(),
                        FileType.getType(zi.getRelativePath()).getSmallIcon(),
                        vc, zi.getFullPath());
                tabIndexMap.put(node, vc);
            } catch (IOException ex) {
                ExceptionDialog.showException(ex, viewerTabbedPane);
            }
        } else {
            ViewerContainer vc = tabIndexMap.get(node);
            viewerTabbedPane.setSelectedComponent(vc);
            if (vt != vc.getCurrentViewerType()) {
                vc.viewAs(vt, options.getCharset());
            }
        }
    }
}
