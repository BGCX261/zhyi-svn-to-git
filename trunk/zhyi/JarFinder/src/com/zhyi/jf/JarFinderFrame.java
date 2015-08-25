/*
 * WordzLauncher.java
 *
 * Copyright (C) 2010 Zhao Yi
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
package com.zhyi.jf;

import com.zhyi.zylib.toolkit.FileToolkit;
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

/**
 * The main frame.
 */
public class JarFinderFrame extends JFrame implements ActionListener {

    private JTextField classNameTextField;
    private JButton directoryButton;
    private JTextField directoryTextField;
    private JButton goButton;
    private JMenuItem copyCellMenuItem;
    private JTable resultTable;
    private JCheckBox recursiveCheckBox;
    private JButton aboutButton;
    private JFileChooser directoryChooser;
    private ResultTableModel resultTableModel;

    public JarFinderFrame() {
        super("Jar Finder");
        initComponents();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void initComponents() {
        directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        JLabel classLabel = new JLabel("Class Name:");
        classNameTextField = new JTextField();
        classNameTextField.setToolTipText(
                "Type the name of class for finding which Jar files contain it."
                + " It can be any substring of the fully qulified class name.");
        SwingToolkit.addPopupMenuForTextComponent(classNameTextField, true);

        directoryButton = SwingToolkit.createButton("Directory:",
                "Specify the directory in which to find the Jar file(s)"
                + " containing the given class.", this);
        directoryTextField = new JTextField();
        directoryTextField.setToolTipText(
                "Specify the directory in which to find the Jar file(s)"
                + " containing the given class.");
        SwingToolkit.addPopupMenuForTextComponent(directoryTextField, true);
        goButton = SwingToolkit.createButton("Go", null, this);

        JPopupMenu popupMenu = new JPopupMenu();
        copyCellMenuItem = SwingToolkit.createMenuItem("Copy Cell", KeyEvent.VK_C,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), this);
        popupMenu.add(copyCellMenuItem);

        resultTableModel = new ResultTableModel(new ArrayList<Result>());
        resultTable = new JTable(resultTableModel);
        resultTable.setFillsViewportHeight(true);
        resultTable.setAutoCreateRowSorter(true);
        resultTable.setCellSelectionEnabled(true);
        resultTable.setComponentPopupMenu(popupMenu);
        JScrollPane resultScrollPane = new JScrollPane(resultTable);

        JSeparator separator = new JSeparator();
        recursiveCheckBox = new JCheckBox("Search the directory recursively.");
        aboutButton = SwingToolkit.createButton("About...", null, this);

        GroupLayout gl = SwingToolkit.createGroupLayout(getContentPane(), true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(classLabel).addComponent(directoryButton))
                .addGroup(gl.createParallelGroup()
                    .addComponent(classNameTextField)
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(directoryTextField).addComponent(goButton))))
            .addComponent(resultScrollPane)
            .addComponent(separator)
            .addGroup(gl.createSequentialGroup()
                .addComponent(recursiveCheckBox)
                .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(aboutButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(classLabel).addComponent(classNameTextField))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(directoryButton).addComponent(directoryTextField).addComponent(goButton))
            .addComponent(resultScrollPane)
            .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(recursiveCheckBox).addComponent(aboutButton)));
    }

    private void findJars() {
        final String key = classNameTextField.getText();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "The class name shouldn't be empty.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final File path = new File(directoryTextField.getText());
        if (!path.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                    "Path \"" + directoryTextField.getText() + "\" isn't a directory.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        goButton.setText("Finding...");
        goButton.setEnabled(false);
        resultTableModel.setResults(new ArrayList<Result>());
        new SwingWorker<Void, Result>() {

            private void searchDirectory(File directory) {
                List<Result> results = new ArrayList<Result>();
                for (File jar : FileToolkit.listFiles(directory, "jar")) {
                    try {
                        for (String className : JarFinderToolkit.findClass(key, new JarFile(jar))) {
                            results.add(new Result(className, jar.getPath()));
                        }
                    } catch (IOException ex) {
                        // Invalid Jar files are ignored here...
                    }
                }
                publish(results.toArray(new Result[results.size()]));
            }

            private void searchDirectoryRecursively(
                    File directory, final ExecutorService threadPool) {
                searchDirectory(directory);
                for (final File dir : FileToolkit.listDirectories(directory)) {
                    threadPool.execute(new Runnable() {

                        @Override
                        public void run() {
                            searchDirectoryRecursively(dir, threadPool);
                        }

                    });
                }
            }

            @Override
            protected Void doInBackground() throws Exception {
                if (recursiveCheckBox.isSelected()) {
                    searchDirectoryRecursively(path, Executors.newCachedThreadPool());
                } else {
                    searchDirectory(path);
                }
                return null;
            }

            @Override
            protected void process(List<Result> chunks) {
                resultTableModel.addResults(chunks);
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    SwingToolkit.showRootCause(ex, JarFinderFrame.this);
                }
                goButton.setText("Go");
                goButton.setEnabled(true);
            }

        }.execute();
    }

    private void copyCell() {
        int row = resultTable.getSelectedRow();
        int column = resultTable.getSelectedColumn();
        if (row == -1 || column == -1) {
            return;
        }
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection((String) resultTableModel.getValueAt(row, column)), null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == directoryButton) {
            if (directoryChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                directoryTextField.setText(directoryChooser.getSelectedFile().getPath());
            }
        } else if (src == goButton) {
            findJars();
        } else if (src == copyCellMenuItem) {
            copyCell();
        } else if (src == aboutButton) {
            JOptionPane.showMessageDialog(this,
                    "<html><h2>Jar Finder 1.0.0</h2>"
                    + "Copyright &copy; 2010 Zhao Yi (shinzey@msn.com)"
                    + "<br>Licensed under GNU General Public License Version 3</html>",
                    "About Jar Finder", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static class Result {

        String className;
        String jarPath;

        Result(String className, String jarPath) {
            this.className = className;
            this.jarPath = jarPath;
        }

        String getClassName() {
            return className;
        }

        void setClassName(String className) {
            this.className = className;
        }

        String getJarPath() {
            return jarPath;
        }

        void setJarPath(String jarPath) {
            this.jarPath = jarPath;
        }

    }

    private static class ResultTableModel extends AbstractTableModel {

        static final String[] columnNames = {"Class Name", "Jar Path"};
        List<Result> results;

        ResultTableModel(List<Result> results) {
            this.results = results;
        }

        void setResults(List<Result> results) {
            this.results = results;
            fireTableDataChanged();
        }

        void addResults(List<Result> moreResults) {
            if (moreResults.isEmpty()) {
                return;
            }
            int beginRow = results.size();
            results.addAll(moreResults);
            fireTableRowsInserted(beginRow, beginRow + moreResults.size() - 1);
        }

        @Override
        public int getRowCount() {
            return results.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Result result = results.get(rowIndex);
            return columnIndex == 0 ? result.getClassName() : result.getJarPath();
        }

    }

    public static void main(String[] args) {
        SwingToolkit.initSystemlookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JarFinderFrame();
                frame.setSize(480, 300);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }

        });
    }

}
