/*
 * AboutDialog.java
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
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * The About dialog.
 */
public class AboutDialog extends JDialog implements ActionListener {

    private JButton closeButton;

    public AboutDialog(Frame owner) {
        super(owner, "About Wordz", true);
        setIconImage(Context.WORDZ_ICON);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponent();
    }

    private void initComponent() {
        JLabel logoLabel = new JLabel(new ImageIcon(
                getClass().getResource("/com/zhyi/wordz/images/splash.png")));
        closeButton = SwingToolkit.createButton("Close", null, this);

        GroupLayout gl = SwingToolkit.createGroupLayout(
                getContentPane(), true, true);
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER)
            .addComponent(logoLabel).addComponent(closeButton));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(logoLabel).addComponent(closeButton));
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            dispose();
        }
    }

}
