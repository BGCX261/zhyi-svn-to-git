/*
 * WordzFrame.java
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
import com.zhyi.wordz.db.entity.Word;
import com.zhyi.wordz.db.WordHandler;
import com.zhyi.wordz.db.entity.Example;
import com.zhyi.zylib.toolkit.SwingToolkit;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The main frame.
 */
public class WordzFrame extends JFrame {

    private static enum DisplayMode {
        ALL, SEARCH;
    }

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(
            DateFormat.FULL, DateFormat.FULL, Locale.getDefault());

    private static final String GOOGLE_DICTIONARY_URL
            = "http://www.google.com/dictionary?langpair=%s%%7C%s&q=%s";
    private static final String PARAGRAPH_TEMPLATE =
            "<p style=\"font-family: SansSerif; font-weight: bold; font-style: italic;"
            + " padding: 3pt 12pt 3pt 12pt; background: #ccccff; color: #ff5500;\">%s</p>";
    private static final String WORD_TEMPLATE =
            "<h1 style=\"text-align: center; margin: 0\">%s</h1>"
            + String.format(PARAGRAPH_TEMPLATE, "Pronunciation")
            + "<p style=\"margin: 3pt 24pt 0pt 24pt;\">%s</p>"
            + String.format(PARAGRAPH_TEMPLATE, "Explanation")
            + "<p style=\"margin: 3pt 24pt 0pt 24pt;\">%s</p>"
            + String.format(PARAGRAPH_TEMPLATE, "Examples") + "%s"
            + String.format(PARAGRAPH_TEMPLATE, "Created Time")
            + "<p style=\"margin: 3pt 24pt 0pt 24pt;\">%s</p>";

    // Event handlers.
    private MenuHandler menuHandler;
    private WordNavigationHandler wordNavigationHandler;
    private WordManagementHandler wordManagementHandler;

    // Menu.
    private JMenuItem optionsMenuItem;
    private JMenuItem exitmMenuItem;
    private JMenuItem aboutMenuItem;

    // Navigation panel.
    private JTextField searchTextField;
    private JButton searchButton;
    private JButton resetButton;
    private JList wordList;
    private JButton addButton;
    private JButton deleteButton;
    private JButton firstPageButton;
    private JButton previousPageButton;
    private JLabel pageLabel;
    private JButton nextPageButton;
    private JButton lastPageButton;
    private JTextField goToPageTextField;

    // Word panel.
    private JEditorPane wordViewer;
    private JButton editButton;
    private JButton googleButton;

    // Dialogs.
    private WordDialog wordDialog;
    private OptionsDialog optionsDialog;
    private AboutDialog aboutDialog;

    private Word currentWord;
    private DisplayMode displayMode;
    private int page;
    private int pageCount;

    public WordzFrame(String title) {
        super(title);

        try {
            WordHandler.connectDataBase();
        } catch (Exception ex) {
            SwingToolkit.showRootCause(ex, this);
            System.exit(0);
        }
        Context.loadOptions();

        menuHandler = new MenuHandler();
        wordNavigationHandler = new WordNavigationHandler();
        wordManagementHandler = new WordManagementHandler();

        setIconImage(Context.WORDZ_ICON);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        initComponents();

        displayMode = DisplayMode.ALL;
        try {
            refreshWordList();
        } catch (Exception ex) {
            SwingToolkit.showRootCause(ex, this);
        }
    }

    private void initComponents() {
        setJMenuBar(createMenuBar());
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createNavigationPanel(), createWordPanel()));
    }

    private JMenuBar createMenuBar() {
        optionsMenuItem = SwingToolkit.createMenuItem("Options...", 0, null, menuHandler);
        exitmMenuItem = SwingToolkit.createMenuItem("Exit", 0, null, menuHandler);
        aboutMenuItem = SwingToolkit.createMenuItem("About...", 0, null, menuHandler);

        JMenuBar menuBar = new JMenuBar();

        JMenu wordMenu = menuBar.add(new JMenu("Wordz"));
        wordMenu.add(optionsMenuItem);
        wordMenu.addSeparator();
        wordMenu.add(exitmMenuItem);

        JMenu helpMenu = menuBar.add(new JMenu("Help"));
        helpMenu.add(aboutMenuItem);

        return menuBar;
    }

    private JPanel createNavigationPanel() {
        searchTextField = new JTextField();
        SwingToolkit.addPopupMenuForTextComponent(searchTextField, true);
        searchTextField.addActionListener(wordNavigationHandler);
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                searchButton.setEnabled(!searchTextField.getText().isEmpty());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchButton.setEnabled(!searchTextField.getText().isEmpty());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchButton.setEnabled(!searchTextField.getText().isEmpty());
            }

        });
        searchButton = SwingToolkit.createButton(
                "Search", null, wordNavigationHandler);
        searchButton.setEnabled(false);
        resetButton = SwingToolkit.createButton(
                "Reset", null, wordNavigationHandler);

        wordList = new JList();
        wordList.setToolTipText("Double-click a word to edit it.");
        wordList.addListSelectionListener(wordNavigationHandler);
        wordList.addMouseListener(wordNavigationHandler);
        JScrollPane wordListScrollPane = new JScrollPane(wordList);

        addButton = SwingToolkit.createButton(
                "+", "Add a new word.", wordManagementHandler);
        deleteButton = SwingToolkit.createButton(
                "-", "Delete selected word(s).", wordManagementHandler);
        deleteButton.setEnabled(false);
        firstPageButton = SwingToolkit.createButton(
                "<<", "Go to the first page.", wordNavigationHandler);
        firstPageButton.setEnabled(false);
        previousPageButton = SwingToolkit.createButton(
                "<", "Go to the previous page.", wordNavigationHandler);
        previousPageButton.setEnabled(false);
        pageLabel = new JLabel("1/1", SwingConstants.CENTER);
        pageLabel.setSize(48, pageLabel.getHeight());
        nextPageButton = SwingToolkit.createButton(
                ">", "Go to the next page.", wordNavigationHandler);
        nextPageButton.setEnabled(false);
        lastPageButton = SwingToolkit.createButton(
                ">>", "Go to the last page.", wordNavigationHandler);
        lastPageButton.setEnabled(false);

        goToPageTextField = new JTextField();
        goToPageTextField.setToolTipText("Type in page number and press ENTER.");
        SwingToolkit.addPopupMenuForTextComponent(goToPageTextField, true);
        goToPageTextField.addActionListener(wordNavigationHandler);

        JToolBar backwardNavigationToolBar = createToolBar(
                firstPageButton, previousPageButton);
        JToolBar forwardNavigationToolBar = createToolBar(
                nextPageButton, lastPageButton);

        JPanel navigationPanel = new JPanel();
        GroupLayout gl = SwingToolkit.createGroupLayout(navigationPanel, true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(searchTextField).addComponent(searchButton).addComponent(resetButton))
            .addComponent(wordListScrollPane)
            .addGroup(gl.createSequentialGroup()
                .addComponent(addButton).addComponent(deleteButton)
                .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(backwardNavigationToolBar)
                .addComponent(pageLabel, 32, 32, 32)
                .addComponent(forwardNavigationToolBar)
                .addComponent(goToPageTextField, 28, 28, 28)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(searchTextField).addComponent(searchButton).addComponent(resetButton))
            .addComponent(wordListScrollPane)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(addButton).addComponent(deleteButton)
                .addComponent(backwardNavigationToolBar).addComponent(pageLabel)
                .addComponent(forwardNavigationToolBar).addComponent(goToPageTextField)));
        gl.linkSize(searchButton, resetButton);
        gl.linkSize(addButton, deleteButton);
        return navigationPanel;
    }

    private JPanel createWordPanel() {
        wordViewer = new JEditorPane();
        wordViewer.setContentType("text/html; charset=UTF-8");
        wordViewer.setEditable(false);
        SwingToolkit.addPopupMenuForTextComponent(wordViewer, false);
        JScrollPane wordScrollPane = new JScrollPane(wordViewer);

        editButton = SwingToolkit.createButton(
                "Edit", null, wordManagementHandler);
        editButton.setEnabled(false);
        googleButton = SwingToolkit.createButton(
                "Google it!", null, wordNavigationHandler);
        googleButton.setEnabled(false);

        JPanel wordPanel = new JPanel();
        GroupLayout gl = SwingToolkit.createGroupLayout(wordPanel, true, true);
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addComponent(wordScrollPane)
            .addGroup(gl.createSequentialGroup()
                .addComponent(editButton)
                .addPreferredGap(ComponentPlacement.UNRELATED, 0, Short.MAX_VALUE)
                .addComponent(googleButton)));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(wordScrollPane)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(editButton).addComponent(googleButton)));
        return wordPanel;
    }

    private JToolBar createToolBar(Component... components) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        for (Component c : components) {
            toolBar.add(c);
        }
        return toolBar;
    }

    private void createWordDialog() {
        wordDialog = new WordDialog(this);
        wordDialog.setSize(480, 300);
        wordDialog.addPropertyChangeListener(
                WordDialog.WORD_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final Word newWord = (Word) evt.getNewValue();
                new SwingWorker<Boolean, Void>() {

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        boolean shouldRefresh = newWord.getId() == null;
                        WordHandler.saveWord(newWord);
                        return shouldRefresh;
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean shouldRefresh = get();
                            currentWord = newWord;
                            if (shouldRefresh) {
                                // A new word has just been added.
                                refreshWordList();
                            } else {
                                // An exsting word has just been updated.
                                updateWordViewer();
                            }
                        } catch (Exception ex) {
                            SwingToolkit.showRootCause(ex, WordzFrame.this);
                        }
                    }
                }.execute();
            }
        });
    }

    private void createOptionsDialog() {
        optionsDialog = new OptionsDialog(this);
        optionsDialog.addPropertyChangeListener(
                OptionsDialog.REFRESH_NEEDED_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                refreshWordList();
            }

        });
    }

    private void onExit() {
        try {
            Context.saveOptions();
            WordHandler.disconnectDataBase();
        } catch (Exception ex) {
            SwingToolkit.showRootCause(ex, this);
        } finally {
            System.exit(0);
        }
    }

    private void showOptionsDialog() {
        if (optionsDialog == null) {
            createOptionsDialog();
        }
        optionsDialog.refresh();
        SwingToolkit.showWindow(optionsDialog, this);
    }

    private void showAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(this);
        }
        SwingToolkit.showWindow(aboutDialog, this);
    }

    private void refreshWordList() {
        new SwingWorker<Void, Void>() {

            private long wordCount;
            private List<Word> words;

            @Override
            protected Void doInBackground() throws Exception {
                String key = null;
                if (displayMode == DisplayMode.SEARCH) {
                    key = searchTextField.getText();
                }

                // Paging.
                wordCount = WordHandler.getWordCount(key);
                if (wordCount == 0) {
                    pageCount = 1;
                } else {
                    int noWordsPerPage = Context.getNoWordsPerPage();
                    pageCount = (int) ((wordCount + noWordsPerPage - 1) / noWordsPerPage);
                    if (page > pageCount - 1) {
                        page = pageCount - 1;
                    }
                }

                // Repaging if autoJumpToModifiedWord is on.
                if (currentWord != null && Context.isAutoJumpToMdifiedWord()) {
                    int index = (int) WordHandler.getPosition(
                            currentWord, Context.getWordOrder());
                    page = index / Context.getNoWordsPerPage();
                }

                int maxResults = Context.getNoWordsPerPage();
                int firstResult = page * maxResults;
                if (displayMode == DisplayMode.ALL) {
                    words = WordHandler.getWords(
                            firstResult, maxResults, Context.getWordOrder());
                } else {
                    words = WordHandler.searchWords(key,
                            firstResult, maxResults, Context.getWordOrder());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    int selectedIndex = wordList.getSelectedIndex();
                    if (selectedIndex == -1) {
                        selectedIndex = 0;
                    }
                    wordList.setModel(new AbstractListModel() {

                        @Override
                        public int getSize() {
                            return words.size();
                        }

                        @Override
                        public Object getElementAt(int index) {
                            return words.get(index);
                        }

                    });
                    if (words.size() > 0) {
                        // Jump to the new word.
                        wordList.setSelectedValue(currentWord, true);
                        // Restore the originally selected index.
                        if (wordList.getSelectedIndex() == -1) {
                            if (selectedIndex >= words.size()) {
                                selectedIndex = 0;
                            }
                            wordList.setSelectedIndex(selectedIndex);
                        }
                    } else {
                        wordViewer.setText("");
                    }

                    pageLabel.setText((page + 1) + "/" + pageCount);
                    deleteButton.setEnabled(wordCount != 0);
                    firstPageButton.setEnabled(page != 0);
                    previousPageButton.setEnabled(page != 0);
                    nextPageButton.setEnabled(page != pageCount - 1);
                    lastPageButton.setEnabled(page != pageCount - 1);
                } catch (Exception ex) {
                    SwingToolkit.showRootCause(ex, WordzFrame.this);
                }
            }

        }.execute();
    }

    private void showWordDialog(Word word) {
        if (wordDialog == null) {
            createWordDialog();
        }
        wordDialog.setWord(word);
        SwingToolkit.showWindow(wordDialog, this);
    }

    private void updateWordViewer() {
        StringBuilder examples = new StringBuilder(
                "<ol style=\"margin: 3pt 24pt 3pt 38pt;\">");
        for (Example example : currentWord.getExamples()) {
            examples.append("<li>");
            examples.append(example);
            examples.append("</li>");
        }
        examples.append("</ol>");
        wordViewer.setText(String.format(WORD_TEMPLATE,
                currentWord.getSpelling(), "[" + currentWord.getPronunciation() + "]",
                currentWord.getExplanation(), examples.toString(),
                DATE_FORMAT.format(currentWord.getCreatedTime())));
        editButton.setEnabled(true);
        googleButton.setEnabled(true);
    }

    private void deleteWords() {
        int option = JOptionPane.showConfirmDialog(this,
                "Do you want to delete the selected word(s) from database?",
                "Delete Word(s)", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.NO_OPTION) {
            return;
        }

        Object[] values = wordList.getSelectedValues();
        final List<Word> selectedWords = new ArrayList<Word>();
        for (Object value : values) {
            selectedWords.add((Word) value);
        }
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                WordHandler.deleteWords(selectedWords);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    refreshWordList();
                } catch (Exception ex) {
                    SwingToolkit.showRootCause(ex, WordzFrame.this);
                }
            }

        }.execute();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            onExit();
        }
    }

    private class MenuHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == optionsMenuItem) {
                showOptionsDialog();
            } else if (src == exitmMenuItem) {
                onExit();
                System.exit(0);
            } else if (src == aboutMenuItem) {
                showAboutDialog();
            }
        }

    }

    private class WordNavigationHandler extends MouseAdapter
            implements ActionListener, ListSelectionListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2 && e.getSource() == wordList) {
                if (currentWord != null) {
                    showWordDialog(currentWord);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == searchTextField || src == searchButton) {
                if (searchTextField.getText().isEmpty()) {
                    return;
                }
                displayMode = DisplayMode.SEARCH;
            } else if (src == resetButton) {
                displayMode = DisplayMode.ALL;
            } else if (src == firstPageButton) {
                page = 0;
            } else if (src == previousPageButton) {
                page--;
            } else if (src == nextPageButton) {
                page++;
            } else if (src == lastPageButton) {
                page = pageCount - 1;
            } else if (src == goToPageTextField) {
                try {
                    int pageToGo = Integer.parseInt(goToPageTextField.getText()) - 1;
                    if (pageToGo < 0) {
                        page = 0;
                    } else if (pageToGo > pageCount - 1) {
                        page = pageCount - 1;
                    } else {
                        page = pageToGo;
                    }
                } catch (Exception ex) {
                    SwingToolkit.showRootCause(ex, WordzFrame.this);
                    return;
                }
            } else if (src == googleButton) {
                try {
                    Desktop.getDesktop().browse(new URI(String.format(
                            GOOGLE_DICTIONARY_URL, Context.getFromLanguage(),
                            Context.getToLanguage(), currentWord.getSpelling())));
                } catch (Exception ex) {
                    SwingToolkit.showRootCause(ex, WordzFrame.this);
                }
                return;
            }
            currentWord = null;
            refreshWordList();
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                Object value = wordList.getSelectedValue();
                if (value != null) {
                    currentWord = (Word) value;
                    updateWordViewer();
                } else {
                    wordViewer.setText("");
                    editButton.setEnabled(false);
                    googleButton.setEnabled(false);
                }
            }
        }

    }

    private class WordManagementHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == addButton) {
                showWordDialog(new Word());
            } else if (src == deleteButton) {
                deleteWords();
            } else if (src == editButton) {
                showWordDialog(currentWord);
            }
        }

    }

}
