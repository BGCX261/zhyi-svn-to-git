/*
 * UIHelper.java
 *
 * Copyright (C) 2011 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zse.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

/**
 * Utility methods for Swing.
 * @author Zhao Yi
 */
public final class SwingHelper {
    private SwingHelper() {
    }

    /**
     * Sets the look and feel to system look and feel, and fixes the following
     * issues under Windows look and feel:
     * <ul>
     * <li>In Windows XP and Windows Server 2003, popup menu lacks a padding.</li>
     * <li>In Windows Vista and above, combo box has an extra border, and menu
     * in menu bar lacks a padding.</li>
     * </ul>
     */
    public static void initSystemlookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (UIManager.getLookAndFeel().getName().equals("Windows")) {
                fixWindowsLafProblems();
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
    }

    private static void fixWindowsLafProblems() {
        double xpVersion = 5.1;
        double vistaVersion = 6.0;
        Double version = Double.parseDouble(System.getProperty("os.version"));
        UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
        if (version >= xpVersion && version < vistaVersion) {
            // We can't simply change "PopupMenu.border" property, because
            // a JComboBox can also trigger a popup menu which shouldn't have
            // a padding.
            uiDefaults.put("PopupMenuUI", XpPopupMenuUI.class.getName());
        } else if (version >= vistaVersion) {
            // Remove the redundent border for combo box.
            uiDefaults.put("ComboBox.border", null);
            // Add some paddings for menu.
            uiDefaults.put("Menu.border", BorderFactory.createEmptyBorder(0, 3, 0, 3));
        }
    }

    /**
     * Places a window relative to {@code relativeComponent}, and then displays
     * it.
     * @see Window#setLocationRelativeTo(Component)
     */
    public static void showWindow(Window window, Component relativeComponent) {
        window.setLocationRelativeTo(relativeComponent);
        window.setVisible(true);
    }

    /**
     * Same as {@code showWindow(window, window.getOwner())}.
     * @see #showWindow(Window, Component)
     */
    public static void showWindow(Window window) {
        showWindow(window, window.getOwner());
    }

    /**
     * Creates a menu with the specified text and mnemonic.
     */
    public static JMenu createMenu(String text, int mnemonic) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    /**
     * Creates a menu with the specified text, mnemonic, key accelerator, and
     * action listener.
     */
    public static JMenuItem createMenuItem(String text, int mnemonic,
            KeyStroke accelerator, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.setAccelerator(accelerator);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    /**
     * Creates a button with the specified text and action listener.
     */
    public static JButton createButton(String text, int mnemonic,
            ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setMnemonic(mnemonic);
        button.addActionListener(actionListener);
        return button;
    }

    /**
     * Creates a group layout for the specified container component.
     * @param autoCreateContainerGaps Whether gaps between container edge and
     * components should be created automatically.
     * @param autoCreateGaps Whether gaps between components should be created
     * automatically.
     */
    public static GroupLayout createGroupLayout(Container container,
            boolean autoCreateContainerGaps, boolean autoCreateGaps) {
        GroupLayout gl = new GroupLayout(container);
        gl.setAutoCreateContainerGaps(autoCreateContainerGaps);
        gl.setAutoCreateGaps(autoCreateGaps);
        container.setLayout(gl);
        return gl;
    }

    /**
     * Same as {@code createGroupLayout(container, true, true)}.
     * @see #createGroupLayout(Container, boolean, boolean)
     */
    public static GroupLayout createGroupLayout(Container container) {
        return createGroupLayout(container, true, true);
    }

    /**
     * Adds a popup menu for a text component. It includes common editing items:
     * undo, redo, select all, cut, copy, copy all and paste. If the text
     * component is not editable, undo, redo, cut and paste actions will be
     * unavailable.
     * @param textComponent The text component to add popup menu.
     * @param editable Whether the text component is editable.
     */
    public static void addPopupMenuForTextComponent(
            final JTextComponent textComponent) {
        final JPopupMenu popupMenu = new JPopupMenu();

        // Undo and Redo.
        final JMenuItem undoMenuItem = createMenuItem("Undo", KeyEvent.VK_U,
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), null);
        final JMenuItem redoMenuItem = createMenuItem("Redo", KeyEvent.VK_R,
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), null);
        setUpUndoRedo(textComponent, undoMenuItem, redoMenuItem);

        // Select All and Copy All.
        final JMenuItem selectAllMenuItem = createMenuItem("Select All", KeyEvent.VK_A,
                KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK),
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textComponent.selectAll();
            }
        });
        final JMenuItem copyAllMenuItem = createMenuItem("Copy All", KeyEvent.VK_L,
                KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK),
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textComponent.selectAll();
                textComponent.copy();
            }
        });

        // Cut, Copy and Paste.
        final JMenuItem cutMenuItem = createMenuItem("Cut", KeyEvent.VK_T,
                KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK),
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textComponent.cut();
            }
        });
        final JMenuItem copyMenuItem = createMenuItem("Copy", KeyEvent.VK_Y,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK),
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textComponent.copy();
            }
        });
        final JMenuItem pasteMenuItem = createMenuItem("Paste", KeyEvent.VK_P,
                KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK),
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textComponent.paste();
            }
        });

        // Only display the popup menu when the text component is enabled.
        textComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && textComponent.isEnabled()) {
                    textComponent.requestFocusInWindow();
                    popupMenu.show(textComponent, e.getX(), e.getY());
                }
            }
        });

        // Show popup menu items conditionally.
        String editablePropertyName = "editable";
        textComponent.addPropertyChangeListener(
                new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                popupMenu.removeAll();
                if (Boolean.TRUE.equals(evt.getNewValue())) {
                    popupMenu.add(undoMenuItem);
                    popupMenu.add(redoMenuItem);
                    popupMenu.addSeparator();
                    popupMenu.add(selectAllMenuItem);
                    popupMenu.add(copyAllMenuItem);
                    popupMenu.addSeparator();
                    popupMenu.add(cutMenuItem);
                    popupMenu.add(copyMenuItem);
                    popupMenu.add(pasteMenuItem);
                } else {
                    popupMenu.add(selectAllMenuItem);
                    popupMenu.add(copyAllMenuItem);
                    popupMenu.addSeparator();
                    popupMenu.add(copyMenuItem);
                }
            }
        });
        textComponent.firePropertyChange(editablePropertyName,
                !textComponent.isEditable(), textComponent.isEditable());
    }

    /**
     * Undo and Redo.
     */
    private static void setUpUndoRedo(final JTextComponent textComponent,
            final JMenuItem undoMenuItem, final JMenuItem redoMenuItem) {
        final UndoManager undoManager = new UndoManager();
        updateUndoStatus(undoManager, undoMenuItem, redoMenuItem);

        textComponent.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
                updateUndoStatus(undoManager, undoMenuItem, redoMenuItem);
            }
        });

        ActionListener undoActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source == undoMenuItem) {
                    undoManager.undo();
                    updateUndoStatus(undoManager, undoMenuItem, redoMenuItem);
                } else if (source == redoMenuItem) {
                    undoManager.redo();
                    updateUndoStatus(undoManager, undoMenuItem, redoMenuItem);
                }
            }
        };
        undoMenuItem.addActionListener(undoActionListener);
        redoMenuItem.addActionListener(undoActionListener);

        // Enable Ctrl+C and Ctrl+V for the text component.
        textComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_Z && undoManager.canUndo()) {
                        undoManager.undo();
                        updateUndoStatus(undoManager, undoMenuItem, redoMenuItem);
                    } else if (e.getKeyCode() == KeyEvent.VK_Y && undoManager.canRedo()) {
                        undoManager.redo();
                        updateUndoStatus(undoManager, undoMenuItem, redoMenuItem);
                    }
                }
            }
        });
    }

    private static void updateUndoStatus(UndoManager undoManager,
            JMenuItem undoMenuItem, JMenuItem redoMenuItem) {
        undoMenuItem.setEnabled(undoManager.canUndo());
        undoMenuItem.setText(undoManager.getUndoPresentationName());
        redoMenuItem.setEnabled(undoManager.canRedo());
        redoMenuItem.setText(undoManager.getRedoPresentationName());
    }

    /**
     * Scrolls the view to make a cell visible, and selects the whole row in
     * which the cell resides.
     * @param table The table.
     * @param row The row index of the cell.
     * @param column The column index of the cell.
     */
    public static void viewTableCell(JTable table, int row, int column) {
        table.scrollRectToVisible(table.getCellRect(row, column, true));
        table.setRowSelectionInterval(row, row);
    }

    /**
     * Makes the specified components to have the same preferred width. The
     * maximum preferred width among them is given to each component.
     */
    public static void linkPreferredWidth(Component... components) {
        List<Integer> widths = new ArrayList<>();
        for (Component c : components) {
            widths.add(c.getPreferredSize().width);
        }
        int maxWidth = Collections.max(widths);
        for (Component c : components) {
            c.setPreferredSize(new Dimension(maxWidth, c.getPreferredSize().height));
            c.setSize(c.getPreferredSize());
        }
    }

    /**
     * Makes the specified components to have the same preferred height. The
     * maximum preferred height among them is given to each component.
     */
    public static void linkPreferredHeight(Component... components) {
        List<Integer> heights = new ArrayList<>();
        for (Component c : components) {
            heights.add(c.getPreferredSize().height);
        }
        int maxHeight = Collections.max(heights);
        for (Component c : components) {
            c.setPreferredSize(new Dimension(c.getPreferredSize().width, maxHeight));
        }
    }

    /**
     * Makes the specified components to have the same preferred size. The
     * maximum preferred width and height among them are given to each component.
     */
    public static void linkPreferredSize(Component... components) {
        linkPreferredWidth(components);
        linkPreferredHeight(components);
    }
}
