/*
 * WordDialog.java
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
package com.zhyi.wordz.ui;

import com.zhyi.wordz.common.Context;
import com.zhyi.wordz.db.entity.Example;
import com.zhyi.wordz.db.entity.Word;
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 * Used for adding and editing a word.
 */
public class WordDialog extends JDialog
        implements ActionListener, ListSelectionListener {

    public static final String WORD_PROPERTY = "WordDialog.word";

    private JTextField spellingTextField;
    private JTextField pronunciationTextField;
    private JTextField explanationTextField;
    private JTable exampleTable;
    private JButton addButton;
    private JButton editButton;
    private JButton removeButton;
    private JButton upButton;
    private JButton downButton;
    private JButton saveButton;
    private JButton cancelButton;

    private ExampleTableModel exampleTableModel;
    private Word word;

    public WordDialog(Frame owner) {
        super(owner, true);
        initComponents();
        setIconImage(Context.WORDZ_ICON);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        spellingTextField = new JTextField();
        SwingToolkit.addPopupMenuForTextComponent(spellingTextField, true);
        pronunciationTextField = new JTextField();
        SwingToolkit.addPopupMenuForTextComponent(pronunciationTextField, true);
        explanationTextField = new JTextField();
        SwingToolkit.addPopupMenuForTextComponent(explanationTextField, true);

        exampleTableModel = new ExampleTableModel(new LinkedList<Example>());
        exampleTable = new JTable(exampleTableModel);
        exampleTable.setToolTipText("Double-click an example to edit it.");
        exampleTable.setTableHeader(null);
        exampleTable.getSelectionModel().addListSelectionListener(this);
        // Double-clicking a row triggers an edit event.
        exampleTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    editExample();
                }
            }

        });
        JScrollPane exampleScrollPane = new JScrollPane(exampleTable);

        addButton = SwingToolkit.createButton(
                "+", "Add a new example.", this);
        editButton = SwingToolkit.createButton(
                "E", "Edit the first selected example.", this);
        removeButton = SwingToolkit.createButton(
                "-", "Remove selected examples.", this);
        upButton = SwingToolkit.createButton(
                "↑", "Move the first selected example up.", this);
        downButton = SwingToolkit.createButton(
                "↓", "Move the first selected example down.", this);

        JSeparator separator = new JSeparator();
        saveButton = SwingToolkit.createButton("Save", null, this);
        cancelButton = SwingToolkit.createButton("Cancel", null, this);

        JLabel spellingLabel = new JLabel("Spelling:");
        JLabel pronunciationLabel = new JLabel("Pronunciation:");
        JLabel explanationLabel = new JLabel("Explanation:");
        JLabel exampLabel = new JLabel("Examples:");

        GroupLayout gl = SwingToolkit.createGroupLayout(
                getContentPane(), true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(spellingLabel).addComponent(explanationLabel)
                    .addComponent(exampLabel))
                .addGroup(gl.createParallelGroup()
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(spellingTextField)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(pronunciationLabel).addComponent(pronunciationTextField))
                    .addComponent(explanationTextField)
                    .addGroup(gl.createParallelGroup()
                        .addComponent(exampleScrollPane)
                        .addGroup(gl.createSequentialGroup()
                            .addComponent(addButton).addComponent(editButton).addComponent(removeButton)
                            .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addComponent(upButton).addComponent(downButton)))))
            .addComponent(separator)
            .addGroup(gl.createSequentialGroup()
                .addPreferredGap(ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                .addComponent(saveButton).addComponent(cancelButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(spellingLabel).addComponent(spellingTextField)
                .addComponent(pronunciationLabel).addComponent(pronunciationTextField))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(explanationLabel).addComponent(explanationTextField))
            .addGroup(gl.createParallelGroup(Alignment.LEADING)
                .addComponent(exampLabel)
                .addGroup(gl.createSequentialGroup()
                    .addComponent(exampleScrollPane)
                    .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(addButton).addComponent(editButton)
                        .addComponent(removeButton).addComponent(upButton)
                        .addComponent(downButton))))
            .addComponent(separator)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(saveButton).addComponent(cancelButton)));
        gl.linkSize(addButton, editButton, removeButton);
        gl.linkSize(upButton, downButton);
        gl.linkSize(saveButton, cancelButton);
        pack();
    }

    /**
     * Sets the word to display for editing. The id of the word will determines
     * the dislog's title: "New Word" if {@code null}, otherwise "Edit Word -
     * &lt;Spelling_of_the_Word&gt;".
     * @param word The word.
     */
    public void setWord(Word word) {
        this.word = word;
        spellingTextField.setText(word.getSpelling());
        pronunciationTextField.setText(word.getPronunciation());
        explanationTextField.setText(word.getExplanation());
        // LinkedList is nore efficient for inserting and removing elements.
        List<Example> examples = new LinkedList<Example>();
        if (word.getExamples() != null) {
            examples.addAll(word.getExamples());
        }
        exampleTableModel.setExamples(examples);
        valueChanged(null);
        if (word.getId() == null) {
            setTitle("New Word");
        } else {
            setTitle("Edit Word - " + word.getSpelling());
        }
    }

    // If some rows are selected, the new example will be added after them;
    // other wise, the new example will be added to the first row.
    private void addExample() {
        int row = 0;
        int[] rows = exampleTable.getSelectedRows();
        if (rows.length != 0) {
            row = rows[rows.length - 1] + 1;
        }
        String content = JOptionPane.showInputDialog(
                this, "Type in the new example (can be formatted with HTML tags):",
                "Add Example", JOptionPane.PLAIN_MESSAGE);
        if (content != null) {
            if (content.length() > 255) {
                JOptionPane.showMessageDialog(this,
                        "The length of an example should be no longer than 255.",
                        "Save Example", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Example example = new Example();
            example.setContent(content);
            exampleTableModel.addExample(row, example);
            exampleTable.setRowSelectionInterval(row, row);
        }
    }

    private void editExample() {
        int row = exampleTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        String content = (String) JOptionPane.showInputDialog(
                this, "Edit the exsiting example (can be formatted with HTML tags):",
                "Edit Example", JOptionPane.PLAIN_MESSAGE, null, null,
                exampleTableModel.examples.get(row));
        if (content != null) {
            if (content.length() > 255) {
                JOptionPane.showMessageDialog(this,
                        "The length of an example should be no longer than 255.",
                        "Save Example", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Example example = exampleTableModel.examples.get(row);
            example.setContent(content);
            exampleTableModel.setExample(row, example);
            exampleTable.setRowSelectionInterval(row, row);
        }
    }

    private void removeExamples() {
        int row = exampleTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        int option = JOptionPane.showConfirmDialog(
                this, "Do you want to remove the selected example(s)?",
                "Remove Example(s)", JOptionPane.WARNING_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            exampleTableModel.removeExamples(exampleTable.getSelectedRows());
        }

        // Select the originally first selected row to make the UI look smooth.
        int rowCount = exampleTable.getRowCount();
        if (row < rowCount) {
            exampleTable.setRowSelectionInterval(row, row);
        } else if (rowCount != 0)  {
            exampleTable.setRowSelectionInterval(rowCount - 1, rowCount - 1);
        }
    }

    private void moveUpExample() {
        int row = exampleTable.getSelectedRow();
        if (row == -1 || row == 0) {
            return;
        }
        Example previousExample = exampleTableModel.examples.get(row - 1);
        exampleTableModel.setExample(row - 1, exampleTableModel.examples.get(row));
        exampleTableModel.setExample(row, previousExample);
        exampleTable.setRowSelectionInterval(row - 1, row - 1);
    }

    private void moveDownExample() {
        int row = exampleTable.getSelectedRow();
        if (row == -1 || row == exampleTable.getRowCount() - 1) {
            return;
        }
        Example nextExample = exampleTableModel.examples.get(row + 1);
        exampleTableModel.setExample(row + 1, exampleTableModel.examples.get(row));
        exampleTableModel.setExample(row, nextExample);
        exampleTable.setRowSelectionInterval(row + 1, row + 1);
    }

    private void saveWord() {
        if (!validateFields()) {
            return;
        }
        word.setSpelling(spellingTextField.getText());
        word.setPronunciation(pronunciationTextField.getText());
        word.setExplanation(explanationTextField.getText());
        int index = 0;
        for (Example example : exampleTableModel.examples) {
            example.setIndex(index);
            index++;
        }
        word.setExamples(exampleTableModel.examples);
        if (word.getCreatedTime() == null) {
            word.setCreatedTime(new Date());
        }
        firePropertyChange(WORD_PROPERTY, null, word);
        dispose();
    }

    private boolean validateFields() {
        if (spellingTextField.getText().length() > 32) {
            JOptionPane.showMessageDialog(this,
                    "The length of spelling should be no longer than 32.",
                    "Save Word", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (pronunciationTextField.getText().length() > 32) {
            JOptionPane.showMessageDialog(this,
                    "The length of pronunciation should be no longer than 32.",
                    "Save Word", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (explanationTextField.getText().length() > 64) {
            JOptionPane.showMessageDialog(this,
                    "The length of pronunciation should be no longer than 64.",
                    "Save Word", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addButton) {
            addExample();
        } else if (src == editButton) {
            editExample();
        } else if (src == removeButton) {
            removeExamples();
        } else if (src == upButton) {
            moveUpExample();
        } else if (src == downButton) {
            moveDownExample();
        } else if (src == saveButton) {
            saveWord();
        } else if (src == cancelButton) {
            dispose();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        boolean enabled = exampleTable.getSelectedRow() != -1;
        editButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
    }

    private static class ExampleTableModel extends AbstractTableModel {

        List<Example> examples;

        ExampleTableModel(List<Example> examples) {
            this.examples = examples;
        }

        void setExamples(List<Example> examples) {
            this.examples = examples;
            fireTableDataChanged();
        }

        void addExample(int row, Example example) {
            examples.add(row, example);
            fireTableRowsInserted(row, row);
        }

        void removeExamples(int[] rows) {
            for (int i = rows.length - 1; i >= 0; i--) {
                examples.remove(rows[i]);
                fireTableRowsDeleted(rows[i], rows[i]);
            }
        }

        void setExample(int row, Example example) {
            examples.set(row, example);
            fireTableCellUpdated(row, 0);
        }

        @Override
        public int getRowCount() {
            return examples.size();
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            // Enclose the example with html tags so that it can be rendered.
            return "<html>" + examples.get(rowIndex) + "</html>";
        }

    }

}
