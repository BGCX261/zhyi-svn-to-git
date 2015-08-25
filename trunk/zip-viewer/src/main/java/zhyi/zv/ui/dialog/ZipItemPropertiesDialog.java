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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.zip.ZipEntry;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import zhyi.zse.io.FileHelper;
import zhyi.zse.io.FileType;
import zhyi.zse.swing.SelectableLabel;
import zhyi.zse.zip.ZipItem;
import zhyi.zse.swing.SwingHelper;
import zhyi.zse.zip.ZipSystem;

/**
 * The dialog that displays a zip item's properties.
 * @author Zhao Yi
 */
public class ZipItemPropertiesDialog extends JDialog {
    private JLabel fileTypeIconLabel;
    private ValueLabel pathValueLabel;
    private ValueLabel typeValueLabel;
    private JLabel sizeLabel;
    private ValueLabel sizeValueLabel;
    private JLabel modifiedTimeLabel;
    private ValueLabel modifiedTimeValueLabel;
    private JLabel compressedSizeLabel;
    private ValueLabel compressedSizeValueLabel;
    private JLabel methodLabel;
    private ValueLabel methodValueLabel;
    private JLabel crcLabel;
    private ValueLabel crcValueLabel;
    private JLabel commentLabel;
    private ValueLabel commentValueLabel;
    private JLabel itemCountLabel;
    private ValueLabel itemCountValueLabel;
    private JLabel compressionRatioLabel;
    private ValueLabel compressionRatioValueLabel;
    private JPanel compressionInfoPanel;
    private JPanel zipSystemInfoPanel;
    private JButton closeButton;

    public ZipItemPropertiesDialog(Window owner) {
        super(owner, DEFAULT_MODALITY_TYPE);

        initComponents();
        SwingHelper.linkPreferredWidth(sizeLabel, modifiedTimeLabel, compressedSizeLabel,
                methodLabel, crcLabel, itemCountLabel, compressionRatioLabel);
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
        JPanel generalInfoPanel = createGeneralInfoPanel();
        JPanel fileInfoPanel = createFileInfoPanel();
        compressionInfoPanel = createCompressionInfoPanel();
        zipSystemInfoPanel = createZipSystemInfoPanel();
        closeButton = SwingHelper.createButton(
                "Close", KeyEvent.VK_C, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        GroupLayout gl = SwingHelper.createGroupLayout(getContentPane());
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER)
                .addComponent(generalInfoPanel)
                .addComponent(fileInfoPanel)
                .addComponent(compressionInfoPanel)
                .addComponent(zipSystemInfoPanel, 0, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
                .addComponent(closeButton));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(generalInfoPanel)
                .addComponent(fileInfoPanel)
                .addComponent(compressionInfoPanel)
                .addComponent(zipSystemInfoPanel)
                .addComponent(closeButton));
        gl.linkSize(SwingConstants.HORIZONTAL, generalInfoPanel,
                fileInfoPanel, compressionInfoPanel, zipSystemInfoPanel);
    }

    private JPanel createGeneralInfoPanel() {
        JLabel pathLabel = new JLabel("<html><b>Path:</b></html>");
        JLabel typeLabel = new JLabel("<html><b>Type:</b></html>");
        fileTypeIconLabel = new JLabel();
        pathValueLabel = new ValueLabel();
        typeValueLabel = new ValueLabel();

        JPanel panel = new JPanel();
        GroupLayout gl = SwingHelper.createGroupLayout(panel, false, true);
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(fileTypeIconLabel)
                .addGap(10)
                .addGroup(gl.createParallelGroup()
                        .addGroup(gl.createSequentialGroup()
                                .addComponent(pathLabel)
                                .addComponent(pathValueLabel, 256, 256, 256))
                        .addGroup(gl.createSequentialGroup()
                                .addComponent(typeLabel)
                                .addComponent(typeValueLabel, 256, 256, 256))));
        gl.setVerticalGroup(gl.createParallelGroup(Alignment.CENTER)
                .addComponent(fileTypeIconLabel)
                .addGroup(gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                                .addComponent(pathLabel)
                                .addComponent(pathValueLabel))
                        .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                                .addComponent(typeLabel)
                                .addComponent(typeValueLabel))));
        gl.linkSize(SwingConstants.HORIZONTAL, pathLabel, typeLabel);
        gl.linkSize(SwingConstants.HORIZONTAL, pathValueLabel, typeValueLabel);
        return panel;
    }

    private JPanel createFileInfoPanel() {
        sizeLabel = new JLabel("<html><b>Size:</b></html>");
        sizeValueLabel = new ValueLabel();
        modifiedTimeLabel = new JLabel("<html><b>Modified Time:</b></html>");
        modifiedTimeValueLabel = new ValueLabel();

        JPanel panel = new JPanel();
        GroupLayout gl = SwingHelper.createGroupLayout(panel);
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                        .addComponent(sizeLabel)
                        .addComponent(sizeValueLabel))
                .addGroup(gl.createSequentialGroup()
                        .addComponent(modifiedTimeLabel)
                        .addComponent(modifiedTimeValueLabel)));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(sizeLabel)
                        .addComponent(sizeValueLabel))
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(modifiedTimeLabel)
                        .addComponent(modifiedTimeValueLabel)));
        gl.linkSize(SwingConstants.HORIZONTAL, sizeLabel, modifiedTimeLabel);
        gl.linkSize(SwingConstants.HORIZONTAL, sizeValueLabel, modifiedTimeValueLabel);
        panel.setBorder(BorderFactory.createTitledBorder("File Information"));
        return panel;
    }

    private JPanel createCompressionInfoPanel() {
        compressedSizeLabel = new JLabel("<html><b>Compressed Size:</b></html>");
        compressedSizeValueLabel = new ValueLabel();
        methodLabel = new JLabel("<html><b>Method:</b></html>");
        methodValueLabel = new ValueLabel();
        crcLabel = new JLabel("<html><b>CRC-32:</b></html>");
        crcValueLabel = new ValueLabel();
        commentLabel = new JLabel("<html><b>Comment:</b></html>");
        commentValueLabel = new ValueLabel();

        JPanel panel = new JPanel();
        GroupLayout gl = SwingHelper.createGroupLayout(panel);
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                        .addComponent(compressedSizeLabel)
                        .addComponent(compressedSizeValueLabel))
                .addGroup(gl.createSequentialGroup()
                        .addComponent(methodLabel)
                        .addComponent(methodValueLabel))
                .addGroup(gl.createSequentialGroup()
                        .addComponent(crcLabel)
                        .addComponent(crcValueLabel))
                .addGroup(gl.createSequentialGroup()
                        .addComponent(commentLabel)
                        .addComponent(commentValueLabel)));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(compressedSizeLabel)
                        .addComponent(compressedSizeValueLabel))
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(methodLabel)
                        .addComponent(methodValueLabel))
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(crcLabel)
                        .addComponent(crcValueLabel))
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(commentLabel)
                        .addComponent(commentValueLabel)));
        gl.linkSize(SwingConstants.HORIZONTAL, compressedSizeLabel,
                methodLabel, crcLabel, commentLabel);
        gl.linkSize(SwingConstants.HORIZONTAL, compressedSizeValueLabel,
                methodValueLabel, crcValueLabel, commentValueLabel);
        panel.setBorder(BorderFactory.createTitledBorder("Compression Information"));
        return panel;
    }

    private JPanel createZipSystemInfoPanel() {
        itemCountLabel = new JLabel("<html><b>Item Count:</b></html>");
        itemCountValueLabel = new ValueLabel();
        compressionRatioLabel = new JLabel("<html><b>Compression Ratio:</b></html>");
        compressionRatioValueLabel = new ValueLabel();

        JPanel panel = new JPanel();
        GroupLayout gl = SwingHelper.createGroupLayout(panel);
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                        .addComponent(itemCountLabel)
                        .addComponent(itemCountValueLabel))
                .addGroup(gl.createSequentialGroup()
                        .addComponent(compressionRatioLabel)
                        .addComponent(compressionRatioValueLabel)));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(itemCountLabel)
                        .addComponent(itemCountValueLabel))
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(compressionRatioLabel)
                        .addComponent(compressionRatioValueLabel)));
        gl.linkSize(SwingConstants.HORIZONTAL,
                itemCountLabel, compressionRatioLabel);
        gl.linkSize(SwingConstants.HORIZONTAL,
                itemCountValueLabel, compressionRatioValueLabel);
        panel.setBorder(BorderFactory.createTitledBorder("Zip System Information"));
        return panel;
    }

    public void showZipItem(ZipItem zipItem) throws IOException {
        // General Information
        pathValueLabel.setText(zipItem.getFullPath());
        pathValueLabel.setCaretPosition(0);

        FileType fileType = FileType.getType(zipItem.getRelativePath());
        fileTypeIconLabel.setIcon(fileType.getLargeIcon());
        typeValueLabel.setText(fileType.getDescription());
        typeValueLabel.setCaretPosition(0);

        // File Information
        ZipEntry ze = zipItem.getZipEntry();
        if (ze == null) {
            ze = new ZipEntry(zipItem.getRelativePath());
        }

        long size = ze.getSize();
        sizeValueLabel.setText(size == -1 ?
                "----" : FileHelper.formatSize(size));
        sizeValueLabel.setCaretPosition(0);

        long time = ze.getTime();
        modifiedTimeValueLabel.setText(time == -1 ?
                "----" : FileHelper.formatDate(time));
        modifiedTimeValueLabel.setCaretPosition(0);

        // Compression Information
        if (zipItem.getOwner() != null) {
            compressionInfoPanel.setVisible(true);

            long compressedSize = ze.getCompressedSize();
            compressedSizeValueLabel.setText(compressedSize == -1 ?
                    "----" : FileHelper.formatSize(compressedSize));
            compressedSizeValueLabel.setCaretPosition(0);

            switch (ze.getMethod()) {
                case ZipEntry.DEFLATED:
                    methodValueLabel.setText("DEFLATED");
                    break;
                case ZipEntry.STORED:
                    methodValueLabel.setText("STORED");
                    break;
                default:
                    methodValueLabel.setText("----");
            }
            methodValueLabel.setCaretPosition(0);

            long crc = ze.getCrc();
            crcValueLabel.setText(crc == -1 ? "----" : String.format("%08X", crc));
            crcValueLabel.setCaretPosition(0);

            String comment = ze.getComment();
            commentValueLabel.setText(comment == null ? "----" : comment);
            commentValueLabel.setCaretPosition(0);
        } else {
            compressionInfoPanel.setVisible(false);
        }

        // Zip System Information
        if (zipItem instanceof ZipSystem) {
            ZipSystem zs = (ZipSystem) zipItem;
            zipSystemInfoPanel.setVisible(true);
            itemCountValueLabel.setText("" + zs.itemCount());
            itemCountValueLabel.setCaretPosition(0);
            double compressionRatio = (double) size / zs.getUncompressedSize();
            compressionRatioValueLabel.setText(String.format(
                    "%.2f%%", compressionRatio * 100));
        } else {
            zipSystemInfoPanel.setVisible(false);
        }

        pack();
        setTitle(String.format("Properties - %s", zipItem.getName()));
        SwingHelper.showWindow(this, getOwner());
    }

    private static class ValueLabel extends SelectableLabel {
        @Override
        public void setText(String t) {
            super.setText(t);
            setCaretPosition(0);
        }
    }
}
