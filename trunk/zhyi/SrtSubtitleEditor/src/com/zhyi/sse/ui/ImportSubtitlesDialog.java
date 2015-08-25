/*
 * ImportSubtitlesDialog.java
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
package com.zhyi.sse.ui;

import com.zhyi.sse.common.SrtToolkit;
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The "Import Sbutitles" dialog. It's used to import subtitles from an SRT file.
 */
public class ImportSubtitlesDialog extends JDialog implements ActionListener {

    public static final String IMPORT_SUBTITLES_PROPERTY = "importSubtitlesProperty";

    private JTextField indexTextField;
    private JTextField sourceFileTextField;
    private JButton sourceFileButton;
    private JButton okButton;
    private JButton cancelButton;
    private JFileChooser srtFileChooser;

    public ImportSubtitlesDialog(Frame owner) {
        super(owner, "Import Subtitles", true);
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
    }

    private void initComponents() {
        sourceFileTextField = new JTextField();
        SwingToolkit.addPopupMenuForTextComponent(sourceFileTextField, true);
        sourceFileButton = SwingToolkit.createButton("SRT File:", null, this);
        srtFileChooser = new JFileChooser();
        srtFileChooser.setFileFilter(new FileNameExtensionFilter(
                "SRT Subtitle File (*.srt)", "srt"));

        JLabel indexLabel = new JLabel("Imported subtitles will be added after index:");
        indexTextField = new JTextField(5);
        SwingToolkit.addPopupMenuForTextComponent(indexTextField, true);

        JSeparator separator = new JSeparator();
        okButton = SwingToolkit.createButton("OK", null, this);
        cancelButton = SwingToolkit.createButton("Cancel", null, this);

        GroupLayout gl = SwingToolkit.createGroupLayout(getContentPane(), true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(sourceFileButton).addComponent(sourceFileTextField))
            .addGroup(gl.createSequentialGroup()
                .addComponent(indexLabel).addComponent(indexTextField))
            .addComponent(separator)
            .addGroup(gl.createSequentialGroup()
                .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(sourceFileButton).addComponent(sourceFileTextField))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(indexLabel).addComponent(indexTextField))
            .addComponent(separator)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.linkSize(okButton, cancelButton);
    }

    public void setIndex(int index) {
        indexTextField.setText(Integer.toString(index));
    }

    private void importSubtitles() {
        okButton.setEnabled(false);
        int index0 = 0;
        try {
            index0 = Integer.parseInt(indexTextField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Index must be an integer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (index0 < 0) {
            JOptionPane.showMessageDialog(this,
                    "Begin index mustn't be smaller than 0.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final int index = index0;

        firePropertyChange(IMPORT_SUBTITLES_PROPERTY, null,
                new SubtitleImporter(index, new File(sourceFileTextField.getText())));
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src.equals(sourceFileButton)) {
            if (srtFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                sourceFileTextField.setText(
                        SrtToolkit.getSelectedSrtFile(srtFileChooser).getPath());
            }
        } else if (src.equals(okButton)) {
            importSubtitles();
        } else if (src.equals(cancelButton)) {
            dispose();
        }
    }

    /**
     * This class wraps necessary parameters for importing subtitles. When the
     * "OK" button of "Import Subtitles" dialog is clicked, an instance of this
     * class may be sent as the new value of a property change event.
     */
    public static class SubtitleImporter {

        private int index;
        private File sourceFile;

        public SubtitleImporter(int index, File sourceFile) {
            this.index = index;
            this.sourceFile = sourceFile;
        }

        public int getIndex() {
            return index;
        }

        public File getSourceFile() {
            return sourceFile;
        }

    }

}
