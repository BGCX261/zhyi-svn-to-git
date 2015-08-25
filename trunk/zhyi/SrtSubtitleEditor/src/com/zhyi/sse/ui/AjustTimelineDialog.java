/*
 * AjustTimelineDialog.java
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
import com.zhyi.sse.common.SubtitleFormatException;
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * The "Ajust Timeline" dialog. It's used to set parameters for ajusting timeline.
 */
public class AjustTimelineDialog extends JDialog implements ActionListener {

    public static final String TIMELINE_PROPERTY = "timelineProperty";

    private static final String FORWARD = "Forward";
    private static final String BACKWARD = "Backward";

    private JTextField beginIndexTextField;
    private JTextField endIndexTextField;
    private JComboBox directionComboBox;
    private JTextField offsetTextField;
    private JButton okButton;
    private JButton cancelButton;

    public AjustTimelineDialog(Frame owner) {
        super(owner, "Ajust Timeline", true);
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
    }

    private void initComponents() {
        JLabel beginIndexLabel = new JLabel("Begin Index:");
        beginIndexTextField = new JTextField(16);
        SwingToolkit.addPopupMenuForTextComponent(beginIndexTextField, true);

        JLabel endIndexLabel = new JLabel("End Index:");
        endIndexTextField = new JTextField(16);
        SwingToolkit.addPopupMenuForTextComponent(endIndexTextField, true);

        JLabel directionLabel = new JLabel("Direction:");
        directionComboBox = new JComboBox(new String[] {FORWARD, BACKWARD});

        JLabel offsetLabel = new JLabel("Offset (ms):");
        offsetTextField = new JTextField("00:00:00,000", 16);
        offsetTextField.setToolTipText(
                "<html>The format should be <b>hh:MM:ss,mmm</b>.</html>");
        SwingToolkit.addPopupMenuForTextComponent(offsetTextField, true);

        JSeparator separator = new JSeparator();
        okButton = SwingToolkit.createButton("OK", null, this);
        cancelButton = SwingToolkit.createButton("Cancel", null, this);

        GroupLayout gl = SwingToolkit.createGroupLayout(getContentPane(), true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(beginIndexLabel).addComponent(endIndexLabel)
                    .addComponent(directionLabel).addComponent(offsetLabel))
                .addGroup(gl.createParallelGroup()
                    .addComponent(beginIndexTextField).addComponent(endIndexTextField)
                    .addComponent(directionComboBox).addComponent(offsetTextField)))
            .addComponent(separator)
            .addGroup(gl.createSequentialGroup()
                .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(beginIndexLabel).addComponent(beginIndexTextField))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(endIndexLabel).addComponent(endIndexTextField))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(directionLabel).addComponent(directionComboBox))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(offsetLabel).addComponent(offsetTextField))
            .addComponent(separator)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.linkSize(okButton, cancelButton);
    }

    public void setIndexes(int beginIndex, int endIndex) {
        beginIndexTextField.setText(Integer.toString(beginIndex));
        endIndexTextField.setText(Integer.toString(endIndex));
    }

    private void ajustTimeline() {
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
            return;
        }
        if (beginIndex < 1) {
            JOptionPane.showMessageDialog(this,
                    "Begin index mustn't be smaller than 1.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int offset = SrtToolkit.calculateMilliseconds(offsetTextField.getText());
            if (directionComboBox.getSelectedItem().equals(BACKWARD)) {
                offset = -offset;
            }
            firePropertyChange(TIMELINE_PROPERTY, null,
                    new TimelineAjustment(beginIndex, endIndex, offset));
            dispose();
        } catch (SubtitleFormatException ex) {
            SwingToolkit.showRootCause(ex, this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src.equals(okButton)) {
            ajustTimeline();
        } else if (src.equals(cancelButton)) {
            dispose();
        }
    }

    /**
     * This class wraps necessary parameters for a timeline ajustment action.
     * When the "OK"button of "Ajust Timeline" dialog is clicked, an instance
     * of this class may be sent as the new value of a property change event.
     */
    public static class TimelineAjustment {

        private int beginIndex;
        private int endIndex;
        private int offset;

        public TimelineAjustment(int beginIndex, int endIndex, int offset) {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.offset = offset;
        }

        public int getBeginIndex() {
            return beginIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public int getOffset() {
            return offset;
        }

    }

}
