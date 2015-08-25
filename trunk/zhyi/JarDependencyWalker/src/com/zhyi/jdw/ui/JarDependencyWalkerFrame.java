/*
 * JarDependencyWalkerFrame.java
 *
 * Copyright (C) 2009 Zhao Yi
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
package com.zhyi.jdw.ui;

import com.zhyi.jdw.common.JarFileWrapper;
import com.zhyi.zylib.toolkit.ExceptionToolkit;
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarFile;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * The main frame.
 */
public class JarDependencyWalkerFrame extends JFrame {

    private final Vector<String> columnNames = new Vector<String>();
    {
        columnNames.add("Jar File");
        columnNames.add("Depends On");
    };

    private DefaultListModel listModel = new DefaultListModel();

    public JarDependencyWalkerFrame() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jarFileChooser = new JFileChooser();
        jarListScrollPane = new JScrollPane();
        jarList = new JList(listModel);
        addButton = new JButton();
        removeButton = new JButton();
        separator = new JSeparator();
        goButton = new JButton();
        resultTableScrollPane = new JScrollPane();
        resultTable = new JTable();
        summaryLabel = new JLabel();
        summaryTextField = new JTextField();
        copyButton = new JButton();
        jButton1 = new JButton();

        jarFileChooser.setMultiSelectionEnabled(true);
        jarFileChooser.setFileFilter(
            new FileNameExtensionFilter("Jar File (*.jar)", "jar"));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Jar Dependency Walker");
        setResizable(false);

        jarList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        jarList.setVisibleRowCount(-1);
        jarListScrollPane.setViewportView(jarList);

        addButton.setText("Add...");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        goButton.setText("Go");
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        updateResult(new Vector<Vector<String>>(), new HashSet<String>());
        resultTableScrollPane.setViewportView(resultTable);

        summaryLabel.setText("Summary:");

        summaryTextField.setEditable(false);

        copyButton.setText("Copy");
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        jButton1.setText("About...");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(separator, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(summaryLabel)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(summaryTextField, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(copyButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(resultTableScrollPane, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                    .addComponent(addButton)
                                    .addComponent(removeButton))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jarListScrollPane, GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(goButton)))))
                .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {addButton, removeButton});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {copyButton, jButton1});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(goButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jarListScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(removeButton)))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(separator, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(resultTableScrollPane, GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(summaryLabel)
                    .addComponent(summaryTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(copyButton)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        if (jarFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File jar : jarFileChooser.getSelectedFiles()) {
                try {
                    listModel.addElement(new JarFileWrapper(new JarFile(jar)));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        for (int i : jarList.getSelectedIndices()) {
            listModel.remove(i);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void goButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
        goButton.setEnabled(false);
        new SwingWorker<Void, Void>() {

            private Vector<Vector<String>> data = new Vector<Vector<String>>();
            private Set<String> summary = new HashSet<String>();

            @Override
            protected Void doInBackground() throws Exception {
                Enumeration<?> e = listModel.elements();
                while (e.hasMoreElements()) {
                    JarFileWrapper jarWrapper = (JarFileWrapper) e.nextElement();
                    JarFile jar = jarWrapper.getJarFile();
                    Vector<String> row = new Vector<String>();
                    row.add(jarWrapper.toString());
                    String libs = jar.getManifest().getMainAttributes().getValue("Class-Path");
                    row.add(libs);
                    data.add(row);
                    for (String lib : libs.split("\\s")) {
                        summary.add(lib);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    updateResult(data, summary);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            JarDependencyWalkerFrame.this,
                            ExceptionToolkit.getRootCause(ex).getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    goButton.setEnabled(true);
                }
            }

        }.execute();
    }//GEN-LAST:event_goButtonActionPerformed

    private void copyButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        summaryTextField.copy();
    }//GEN-LAST:event_copyButtonActionPerformed

    private void jButton1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JOptionPane.showMessageDialog(this,
                "<html><h2>Jar Dependency Walker</h2>"
                + "A tool for analyzing Jar dependencies from manifests.<br><br>"
                + "Copyright &copy; 2009 Zhao Yi (shinzey@msn.com)<br>"
                + "Licensed under GNU General Public License Version 3</html>",
                "About Jar Dependency Walker", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void updateResult(Vector<Vector<String>> data, Set<String> summary) {
        resultTable.setModel(new DefaultTableModel(data, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        });
        int tableWidth = resultTable.getPreferredScrollableViewportSize().width;
        TableColumnModel columnModel = resultTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150);
        columnModel.getColumn(1).setPreferredWidth(tableWidth - 150);

        StringBuilder sb = new StringBuilder();
        for (String s : summary) {
            sb.append(" " + s);
        }
        summaryTextField.setText(sb.length() > 0 ? sb.substring(1) : "");
    }

    public static void main(String[] args) {
        SwingToolkit.initSystemlookAndFeel();
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JarDependencyWalkerFrame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addButton;
    private JButton copyButton;
    private JButton goButton;
    private JButton jButton1;
    private JFileChooser jarFileChooser;
    private JList jarList;
    private JScrollPane jarListScrollPane;
    private JButton removeButton;
    private JTable resultTable;
    private JScrollPane resultTableScrollPane;
    private JSeparator separator;
    private JLabel summaryLabel;
    private JTextField summaryTextField;
    // End of variables declaration//GEN-END:variables

}