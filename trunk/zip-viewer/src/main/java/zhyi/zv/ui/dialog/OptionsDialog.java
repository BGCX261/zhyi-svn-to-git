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

import zhyi.zv.util.StringListConverter;
import zhyi.zv.common.Options;
import zhyi.zv.ui.viewer.ViewerType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import zhyi.zse.swing.SwingHelper;

/**
 * Options Dialog.
 * @author Zhao Yi
 */
public class OptionsDialog extends JDialog {
    private Options options;
    private JComboBox<Charset> charsetComboBox;
    /**
     * Maps the file extensions for each viewer type to a text field.
     */
    private Map<ViewerType, JTextField> vttfMap;
    private JComboBox<ViewerType> defaultViewerComboBox;

    public OptionsDialog(MainFrame owner) {
        super(owner, true);
        options = Options.getInstance();

        initComponents();
        setResizable(false);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Zip Viewer - Options");

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                resetOptions();
            }
        });
    }

    private void initComponents() {
        JLabel charsetLabel = new JLabel("Default Charset:");
        charsetComboBox = new JComboBox<>(
                Charset.availableCharsets().values().toArray(new Charset[0]));
        charsetComboBox.setToolTipText(
                "<html><b>Select the default charset to decode zip items and display text.</b>"
                + "<br>Note an improper charset may lead to decoding failure or messy codes.</html>");
        charsetComboBox.setSelectedItem(options.getCharset());

        JPanel mappingPanel = createMappingPanel();

        JButton okButton = SwingHelper.createButton("OK", KeyEvent.VK_O, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveOptions();
                dispose();
            }
        });
        JButton cancelButton = SwingHelper.createButton("Cancel", KeyEvent.VK_C,
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        GroupLayout gl = SwingHelper.createGroupLayout(getContentPane());
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                        .addComponent(charsetLabel)
                        .addComponent(charsetComboBox))
                .addComponent(mappingPanel)
                .addGroup(gl.createSequentialGroup()
                        .addGap(0, 0, Integer.MAX_VALUE)
                        .addComponent(okButton)
                        .addComponent(cancelButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(charsetLabel)
                        .addComponent(charsetComboBox))
                .addComponent(mappingPanel)
                .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                        .addComponent(okButton)
                        .addComponent(cancelButton)));
        gl.linkSize(okButton, cancelButton);
    }

    private JPanel createMappingPanel() {
        JPanel mappingPanel = new JPanel();
        mappingPanel.setBorder(BorderFactory.createTitledBorder(
                "\"Viewer <-> File Type\" Mapping"));

        GroupLayout gl = SwingHelper.createGroupLayout(mappingPanel);
        GroupLayout.ParallelGroup pg = gl.createParallelGroup();
        gl.setHorizontalGroup(pg);
        GroupLayout.SequentialGroup sg = gl.createSequentialGroup();
        gl.setVerticalGroup(sg);

        List<JLabel> viewerLabels = new ArrayList<>();
        vttfMap = new EnumMap<>(ViewerType.class);
        for (ViewerType vt : ViewerType.values()) {
            JLabel label = new JLabel(vt + ":");
            viewerLabels.add(label);
            JTextField textField = new JTextField();
            textField.setToolTipText("<html><b>Type in file extensions for this viewer.</b>"
                + "<br>Separate multiple file extensions with whitespaces.</html>");
            vttfMap.put(vt, textField);

            pg.addGroup(gl.createSequentialGroup()
                    .addComponent(label).addComponent(textField));
            sg.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(label).addComponent(textField));
        }
        gl.linkSize(SwingConstants.HORIZONTAL, viewerLabels.toArray(new JLabel[0]));

        JSeparator separator = new JSeparator();
        JLabel defaultViewerLabel1 = new JLabel("By default, view zip item as");
        defaultViewerComboBox = new JComboBox<>(ViewerType.values());
        JLabel defaultViewerLabel2 = new JLabel("file.");
        defaultViewerComboBox.setSelectedItem(options.getDefaultViewerType());
        pg.addGroup(gl.createParallelGroup()
                .addComponent(separator)
                .addGroup(gl.createSequentialGroup()
                        .addComponent(defaultViewerLabel1)
                        .addComponent(defaultViewerComboBox)
                        .addComponent(defaultViewerLabel2)));
        sg.addGroup(gl.createSequentialGroup()
                .addComponent(separator)
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(defaultViewerLabel1)
                        .addComponent(defaultViewerComboBox)
                        .addComponent(defaultViewerLabel2)));

        return mappingPanel;
    }

    private void resetOptions() {
        charsetComboBox.setSelectedItem(options.getCharset());
        for (ViewerType vt : ViewerType.values()) {
            vttfMap.get(vt).setText(StringListConverter.asString(
                    options.getVtftMap().get(vt)));
        }
        defaultViewerComboBox.setSelectedItem(options.getDefaultViewerType());
    }

    private void saveOptions() {
        options.setCharset((Charset) charsetComboBox.getSelectedItem());
        for (ViewerType vt : ViewerType.values()) {
            options.getVtftMap().put(vt, StringListConverter.asList(
                    vttfMap.get(vt).getText()));
        }
        options.setDefaultViewerType(
                (ViewerType) defaultViewerComboBox.getSelectedItem());
    }
}
