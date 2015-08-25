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
package zhyi.zse.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/**
 * An enhanced {@link JTabbedPane} component with closable tabs.
 * @author Zhao Yi
 */
public class ClosableTabbedPane extends JTabbedPane {
    /**
     * Index of the right-clicked tab where the popup menu is shown.
     */
    private int clickedTabIndex;

    public ClosableTabbedPane() {
        this(TOP, WRAP_TAB_LAYOUT);
    }

    public ClosableTabbedPane(int tabPlacement) {
        this(tabPlacement, WRAP_TAB_LAYOUT);
    }

    public ClosableTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);

        // Establish popup menu for tabs.
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(SwingHelper.createMenuItem(
                "Close This Tab", 0, null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTabAt(clickedTabIndex);
            }
        }));
        popupMenu.addSeparator();
        popupMenu.add(SwingHelper.createMenuItem(
                "Close All Tabs", 0, null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
            }
        }));
        popupMenu.add(SwingHelper.createMenuItem(
                "Close Other Tabs", 0, null, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component c = getComponentAt(clickedTabIndex);
                String title = getTitleAt(clickedTabIndex);
                Icon icon = getIconAt(clickedTabIndex);
                String tip = getToolTipTextAt(clickedTabIndex);
                removeAll();
                addTab(title, icon, c, tip);
            }
        }));

        // We don't want tab to be switched by right clicks.
        for (MouseListener l : getMouseListeners()) {
            removeMouseListener(l);
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int index = indexAtLocation(e.getX(), e.getY());
                if (index != -1) {
                    if (e.isPopupTrigger()) {
                        clickedTabIndex = index;
                        popupMenu.show(ClosableTabbedPane.this, e.getX(), e.getY());
                    } else {
                        setSelectedIndex(index);
                    }
                }
            }
        });
    }

    @Override
    public void addTab(String title, Component component) {
        addTab(title, null, component, null);
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        addTab(title, icon, component, null);
    }

    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        super.addTab(title, icon, component, tip);
        int index = indexOfComponent(component);
        setTabComponentAt(index, new ClosableTab());
        setSelectedIndex(index);
    }

    private class ClosableTab extends JPanel {
        private ClosableTab() {
            // Make sure the label read information from the tabbed pane.
            JLabel label = new JLabel() {
                @Override
                public String getText() {
                    int index = indexOfTabComponent(ClosableTab.this);
                    return index == -1 ? null : getTitleAt(index);
                }

                @Override
                public Icon getIcon() {
                    int index = indexOfTabComponent(ClosableTab.this);
                    return index == -1 ? null : getIconAt(index);
                }

                @Override
                public String getToolTipText() {
                    int index = indexOfTabComponent(ClosableTab.this);
                    return index == -1 ? null : getToolTipTextAt(index);
                }
            };
            label.setOpaque(false);

            // The close button.
            char cross = '×';
            JButton closeButton = SwingHelper.createButton("" + cross,
                    0, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeTabAt(indexOfTabComponent(ClosableTab.this));
                }
            });
            closeButton.setToolTipText("Close");
            closeButton.setOpaque(false);
            closeButton.setFocusable(false);

            // Compact the close button.
            Font font = new Font(Font.SANS_SERIF, Font.BOLD, 10);
            closeButton.setFont(font);
            FontMetrics fm = closeButton.getFontMetrics(font);
            int size = Math.max(fm.charWidth(cross), fm.getHeight()) + 1;
            closeButton.setPreferredSize(new Dimension(size, size));

            // Skin the close button to a toolbar button.
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setOpaque(false);
            toolBar.add(closeButton);

            setLayout(new BorderLayout(3, 0));
            add(label, BorderLayout.CENTER);
            add(toolBar, BorderLayout.EAST);
            setOpaque(false);
        }
    }
}
