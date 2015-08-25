/*
 * SrtToolkit.java
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

import com.zhyi.sse.common.Context;
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * The "Options" dialog. It's used to change the application's configurations.
 */
public class OptionsDialog extends JDialog implements ActionListener {

    private JComboBox charsetComboBox;
    private JCheckBox escCheckBox;
    private JButton okButton;
    private JButton cancelButton;

    public OptionsDialog(Frame owner) {
        super(owner, "Options", true);
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
    }

    private void initComponents() {
        JLabel charsetLabel = new JLabel("Charset:");
        charsetComboBox = new JComboBox(Charset.availableCharsets().values().toArray());
        charsetComboBox.setToolTipText("<html><b>Select the charset for processing SRT file</b><br>"
                + "An improper charset might result in messy code.</html>");
        charsetComboBox.setSelectedItem(Context.getCharset());
        escCheckBox = new JCheckBox("Edit subtitles continuously", Context.isEditContinuously());
        escCheckBox.setToolTipText("<html><b>A handy option for subtitle translators</b><br>"
                + "If checked, after saving a subtitle, the next one will be automatically selected.</html>");
        JSeparator separator = new JSeparator();
        okButton = SwingToolkit.createButton("OK", null, this);
        cancelButton = SwingToolkit.createButton("Cancel", null, this);

        GroupLayout gl = SwingToolkit.createGroupLayout(getContentPane(), true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(charsetLabel).addComponent(charsetComboBox))
            .addComponent(escCheckBox).addComponent(separator)
            .addGroup(gl.createSequentialGroup()
                .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(charsetLabel).addComponent(charsetComboBox))
            .addComponent(escCheckBox).addComponent(separator)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.linkSize(okButton, cancelButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == okButton) {
            Context.setCharset((Charset) charsetComboBox.getSelectedItem());
            Context.setEditContinuously(escCheckBox.isSelected());
        } else if (src == cancelButton) {
            charsetComboBox.setSelectedItem(Context.getCharset());
            escCheckBox.setSelected(Context.isEditContinuously());
        }
        dispose();
    }

}
