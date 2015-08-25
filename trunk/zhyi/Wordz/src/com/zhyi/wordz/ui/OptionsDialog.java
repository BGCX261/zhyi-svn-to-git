/*
 * OptionsDialog.java
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
import com.zhyi.wordz.common.WordOrder;
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;

/**
 * The Options dialog.
 */
public class OptionsDialog extends JDialog implements ActionListener {

    public static final String REFRESH_NEEDED_PROPERTY = "OptionsDialog.refreshNeeded";

    private JComboBox orderByComboBox;
    private JSpinner noWordsPerPageSpinner;
    private JCheckBox autoJumpToModifiedWordCheckBox;
    private JComboBox fromLanguageComboBox;
    private JComboBox toLanguageComboBox;
    private JButton okButton;
    private JButton cancelButton;

    public OptionsDialog(Frame owner) {
        super(owner, "Options", true);
        setIconImage(Context.WORDZ_ICON);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponent();
    }

    private void initComponent() {
        okButton = SwingToolkit.createButton("OK", null, this);
        cancelButton = SwingToolkit.createButton("Cancel", null, this);

        JPanel wordzOptionspPanel = createWordzOptionsPanel();
        JPanel googleOptionsPanel = createGoogleOptionsPanel();
        JSeparator separator = new JSeparator();

        GroupLayout gl = SwingToolkit.createGroupLayout(
                getContentPane(), true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addComponent(wordzOptionspPanel)
            .addComponent(googleOptionsPanel)
            .addComponent(separator)
            .addGroup(gl.createSequentialGroup()
                .addPreferredGap(ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(wordzOptionspPanel)
            .addComponent(googleOptionsPanel)
            .addComponent(separator)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(okButton).addComponent(cancelButton)));
        gl.linkSize(okButton, cancelButton);
        pack();
    }

    private JPanel createWordzOptionsPanel() {
        orderByComboBox = new JComboBox(WordOrder.values());
        noWordsPerPageSpinner = new JSpinner(new SpinnerNumberModel(30, 20, 40, 1));
        autoJumpToModifiedWordCheckBox = new JCheckBox(
                "Automatically jump to modified word.");
        autoJumpToModifiedWordCheckBox.setToolTipText(
                "Note turning this on may impact the performance "
                + "if the modified word is a newly added one.");
        noWordsPerPageSpinner.setToolTipText("10-50");

        JLabel orderByLabel = new JLabel("Order words by:");
        JLabel noWordsPerPageLabel = new JLabel("Number of words displayed per page:");

        JPanel wordzOptionsPanel = new JPanel();
        wordzOptionsPanel.setBorder(
                BorderFactory.createTitledBorder("Wordz Options"));
        GroupLayout gl = SwingToolkit.createGroupLayout(
                wordzOptionsPanel, true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(orderByLabel).addComponent(orderByComboBox))
            .addGroup(gl.createSequentialGroup()
                .addComponent(noWordsPerPageLabel).addComponent(noWordsPerPageSpinner))
            .addComponent(autoJumpToModifiedWordCheckBox));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(orderByLabel).addComponent(orderByComboBox))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(noWordsPerPageLabel).addComponent(noWordsPerPageSpinner))
            .addComponent(autoJumpToModifiedWordCheckBox));
        return wordzOptionsPanel;
    }

    private JPanel createGoogleOptionsPanel() {
        String[] languagecodes = Locale.getISOLanguages();
        Language[] languages = new Language[languagecodes.length];
        for (int i = 0; i < languages.length; i++) {
            languages[i] = new Language(languagecodes[i]);
        }
        fromLanguageComboBox = new JComboBox(languages);
        toLanguageComboBox = new JComboBox(languages);

        JLabel languagePairLabel = new JLabel("Language pair for Google dictionary:");
        JLabel arrowLabel = new JLabel("->");

        JPanel googleOptionsPanel = new JPanel();
        googleOptionsPanel.setBorder(BorderFactory.createTitledBorder("Google Options"));
        GroupLayout gl = SwingToolkit.createGroupLayout(
                googleOptionsPanel, true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addComponent(languagePairLabel)
            .addGroup(gl.createSequentialGroup()
                .addComponent(fromLanguageComboBox).addComponent(arrowLabel)
                .addComponent(toLanguageComboBox)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(languagePairLabel)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(fromLanguageComboBox).addComponent(arrowLabel)
                .addComponent(toLanguageComboBox)));
        return googleOptionsPanel;
    }

    public void refresh() {
        orderByComboBox.setSelectedItem(Context.getWordOrder());
        noWordsPerPageSpinner.setValue(Context.getNoWordsPerPage());
        autoJumpToModifiedWordCheckBox.setSelected(Context.isAutoJumpToMdifiedWord());
        fromLanguageComboBox.setSelectedItem(new Language(Context.getFromLanguage()));
        toLanguageComboBox.setSelectedItem(new Language(Context.getToLanguage()));
    }

    private void saveChanges() {
        boolean changed = false;

        WordOrder wordOrder = (WordOrder) orderByComboBox.getSelectedItem();
        if (!wordOrder.equals(Context.getWordOrder())) {
            changed = true;
            Context.setWordOrder(wordOrder);
        }

        int noWordsPerPage = (Integer) noWordsPerPageSpinner.getValue();
        if (noWordsPerPage != Context.getNoWordsPerPage()) {
            changed = true;
            Context.setNoWordsPerPage(noWordsPerPage);
        }

        // Thess changes don't require to refresh the UI.
        Context.setAutoJumpToNewWord(autoJumpToModifiedWordCheckBox.isSelected());
        Context.setFromLanguage(((Language) fromLanguageComboBox.getSelectedItem()).code);
        Context.setToLanguage(((Language) toLanguageComboBox.getSelectedItem()).code);

        if (changed) {
            firePropertyChange(REFRESH_NEEDED_PROPERTY, false, true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            saveChanges();
        }
        dispose();
    }

    // A helper class to hold language's code and name.
    private class Language {

        String code;
        String name;

        Language(String code) {
            this.code = code;
            this.name = new Locale(code).getDisplayName();
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Language)) {
                return false;
            }
            return code.equals(((Language) obj).code);
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
