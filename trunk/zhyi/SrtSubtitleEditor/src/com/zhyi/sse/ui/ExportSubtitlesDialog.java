/*
 * ExportSubtitlesDialog.java
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
 * The "Export Subtitles" dialog. It's used to export some subtitles to an SRT
 * file.
 */
public class ExportSubtitlesDialog extends JDialog implements ActionListener {

    public static final String EXPORT_SUBTITLES_PROPERTY = "exportSubtitlesProperty";

    private JTextField beginIndexTextField;
    private JTextField endIndexTextField;
    private JTextField targetFileTextField;
    private JButton targetFileButton;
    private JButton okButton;
    private JButton cancelButton;
    private JFileChooser srtFileChooser;

    public ExportSubtitlesDialog(Frame owner) {
        super(owner, "Export Subtitles", true);
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
    }

    private void initComponents() {
        JLabel beginIndexLabel = new JLabel("Begin Index:");
        beginIndexTextField = new JTextField(5);
        SwingToolkit.addPopupMenuForTextComponent(beginIndexTextField, true);

        JLabel endIndexLabel = new JLabel("End Index:");
        endIndexTextField = new JTextField(5);
        SwingToolkit.addPopupMenuForTextComponent(endIndexTextField, true);

        targetFileButton = SwingToolkit.createButton("Target File:", null, this);
        targetFileTextField = new JTextField(5);
        SwingToolkit.addPopupMenuForTextComponent(targetFileTextField, true);
        srtFileChooser = new JFileChooser();
        srtFileChooser.setFileFilter(new FileNameExtensionFilter(
                "SRT Subtitle File (*.srt)", "srt"));

        JSeparator separator = new JSeparator();
        okButton = SwingToolkit.createButton("OK", null, this);
        cancelButton = SwingToolkit.createButton("Cancel", null, this);

        GroupLayout gl = SwingToolkit.createGroupLayout(getContentPane(), true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(beginIndexLabel).addComponent(beginIndexTextField)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(endIndexLabel).addComponent(endIndexTextField))
            .addGroup(gl.createSequentialGroup()
                .addComponent(targetFileButton).addComponent(targetFileTextField))
            .addComponent(separator)
            .addGroup(gl.createSequentialGroup()
                .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(beginIndexLabel).addComponent(beginIndexTextField)
                .addComponent(endIndexLabel).addComponent(endIndexTextField))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(targetFileButton).addComponent(targetFileTextField))
            .addComponent(separator)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.linkSize(beginIndexTextField, endIndexTextField);
        gl.linkSize(okButton, cancelButton);
    }

    public void setIndexes(int beginIndex, int endIndex) {
        beginIndexTextField.setText(Integer.toString(beginIndex));
        endIndexTextField.setText(Integer.toString(endIndex));
    }

    private void exportSubtitles() {
        int beginIndex = 0;
        int endIndex = 0;
        try {
            beginIndex = Integer.parseInt(beginIndexTextField.getText());
            endIndex = Integer.parseInt(endIndexTextField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Begin and end indexex must be integers.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (beginIndex > endIndex) {
            JOptionPane.showMessageDialog(this,
                    "Begin index mustn't be greater than end index",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (beginIndex < 1) {
            JOptionPane.showMessageDialog(this,
                    "Begin index mustn't be smaller than 1.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File targetFile = new File(targetFileTextField.getText());
        if (targetFile.exists()) {
            int option = JOptionPane.showConfirmDialog(this,
                    "File already exists. Do you want to overwrite it?", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            switch (option) {
                case JOptionPane.YES_OPTION:
                    break;
                case JOptionPane.NO_OPTION:
                default:
                    return;
            }
        }

        firePropertyChange(EXPORT_SUBTITLES_PROPERTY, null,
                new SubtitleExporter(beginIndex, endIndex, targetFile));
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == targetFileButton) {
            if (srtFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                targetFileTextField.setText(
                        SrtToolkit.getSelectedSrtFile(srtFileChooser).getPath());
            }
        } else if (src == okButton) {
            exportSubtitles();
        } else if (src == cancelButton) {
            dispose();
        }
    }

    /**
     * This class wraps necessary parameters for exporting subtitles. When the
     * "OK" button of "Import Subtitles" dialog is clicked, an instance of this
     * class may be sent as the new value of a property change event.
     */
    public static class SubtitleExporter {

        private int beginIndex;
        private int endIndex;
        private File targetFile;

        public SubtitleExporter(int beginIndex, int endIndex, File targetFile) {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.targetFile = targetFile;
        }

        public int getBeginIndex() {
            return beginIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public File getTargetFile() {
            return targetFile;
        }

    }

}
