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

import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * A customized dialog used to display the stack trace of an exception.
 * <p>The caller invokes {@link #showException(Exception, Component)} to display
 * an instance of exception dialog.</p>
 * @author Zhao Yi
 */
public class ExceptionDialog extends JDialog {
    private static ConcurrentMap<Window, ExceptionDialog> CACHE
            = new ConcurrentHashMap<>();

    private SelectableLabel messageLabel;
    private JTextArea exceptionViewer;
    private JButton closeButton;

    /**
     * Creates a new instance with the specified owner.
     * @param owner Owner of this exception dialog.
     */
    private ExceptionDialog(Window owner) {
        super(owner, DEFAULT_MODALITY_TYPE);

        initComponents();
        setSize(640, 360);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                closeButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        JLabel errorIconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        messageLabel = new SelectableLabel();
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        exceptionViewer = new JTextArea();
        exceptionViewer.setEditable(false);
        exceptionViewer.setForeground(UIManager.getColor("Label.foreground"));
        exceptionViewer.setBackground(UIManager.getColor("Label.background"));
        SwingHelper.addPopupMenuForTextComponent(exceptionViewer);
        JScrollPane exceptionViewerScrollPane = new JScrollPane(exceptionViewer);
        closeButton = SwingHelper.createButton("Close", KeyEvent.VK_C, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        GroupLayout gl = SwingHelper.createGroupLayout(getContentPane());
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER)
                .addGroup(gl.createSequentialGroup()
                        .addComponent(errorIconLabel)
                        .addGap(15)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(messageLabel)
                                .addComponent(exceptionViewerScrollPane)))
                .addComponent(closeButton));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup()
                                .addComponent(errorIconLabel)
                                .addGroup(gl.createSequentialGroup()
                                        .addComponent(messageLabel)
                                        .addComponent(exceptionViewerScrollPane)))
                .addComponent(closeButton)));
    }

    /**
     * Displays an exception and puts the dialog relative to the window ancestor
     * of the specified component.
     * @param ex The exception to display.
     * @param c The component used for positioning the dialog. It's usually the
     * component that has thrown the exception.
     */
    public static void showException(Exception ex, Component c) {
        if (ex == null) {
            throw new IllegalArgumentException("Exception is null.");
        }

        Window window = null;
        if (c instanceof Window) {
            window = (Window) c;
        } else {
            window = SwingUtilities.getWindowAncestor(c);
        }

        ExceptionDialog exceptionDialog = CACHE.get(window);
        if (exceptionDialog == null) {
            exceptionDialog = new ExceptionDialog(window);
            CACHE.putIfAbsent(window, exceptionDialog);
        }

        exceptionDialog.setTitle(String.format(
                "Exception - %s", ex.getClass().getName()));
        exceptionDialog.messageLabel.setText(ex.getMessage());
        StringWriter out = new StringWriter();
        ex.printStackTrace(new PrintWriter(out));
        exceptionDialog.exceptionViewer.setText(out.toString());
        exceptionDialog.exceptionViewer.setCaretPosition(0);
        SwingHelper.showWindow(exceptionDialog, window);
    }
}
