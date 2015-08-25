/*
 * Context.java
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
package com.zhyi.wordz.common;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.Properties;

/**
 * Provides access to the global context of Wordz.
 */
public class Context {

    public static final Image WORDZ_ICON = Toolkit.getDefaultToolkit().createImage(
            Context.class.getResource("/com/zhyi/wordz/images/icon.png"));
    public static final String WORDZ_HOME
            = System.getProperty("user.home").replace('\\', '/') + "/wordz";

    private static final String OPTIONS_FILE_PATH = WORDZ_HOME + "/wordz.options";
    private static final String WORD_ORDER = "WordOrder";
    private static final String NO_WORDS_PER_PAGE = "NoWordsPerPage";
    private static final String AUTO_JUMP_TO_MODIFIED_WORD = "AutoJumpToModifiedWord";
    private static final String FROM_LANGUAGE = "FromLanguage";
    private static final String TO_LANGUAGE = "ToLanguage";

    private static WordOrder wordOrder;
    private static int noWordsPerPage;
    private static boolean autoJumpToModifiedWord;
    private static String fromLanguage;
    private static String toLanguage;

    /**
     * Loads options from file.
     */
    public static void loadOptions() {
        Properties ps = new Properties();
        try {
            Reader in = new FileReader(OPTIONS_FILE_PATH);
            ps.load(in);
            wordOrder = WordOrder.valueOf(ps.getProperty(WORD_ORDER));
            noWordsPerPage = Integer.parseInt(ps.getProperty(NO_WORDS_PER_PAGE));
            // A page must shows least 20 words.
            if (noWordsPerPage < 20) {
                noWordsPerPage = 20;
            } else if (noWordsPerPage > 40) {
                noWordsPerPage = 40;
            }
            autoJumpToModifiedWord = Boolean.parseBoolean(
                    ps.getProperty(AUTO_JUMP_TO_MODIFIED_WORD));
            fromLanguage = ps.getProperty(FROM_LANGUAGE);
            toLanguage = ps.getProperty(TO_LANGUAGE);
            in.close();
        } catch (Exception ex) {
            // If any error occurred, reset defaults.
            wordOrder = WordOrder.CREATED_TIME;
            noWordsPerPage = 30;
            autoJumpToModifiedWord = true;
            fromLanguage = "en";
            toLanguage = "zh";
        }
    }

    /**
     * Saves options to file.
     * @throws Exception If any error occurred during saving.
     */
    public static void saveOptions() throws Exception {
        Properties ps = new Properties();
        ps.setProperty(WORD_ORDER, wordOrder.name());
        ps.setProperty(NO_WORDS_PER_PAGE, Integer.toString(noWordsPerPage));
        ps.setProperty(AUTO_JUMP_TO_MODIFIED_WORD, Boolean.toString(autoJumpToModifiedWord));
        ps.setProperty(FROM_LANGUAGE, fromLanguage);
        ps.setProperty(TO_LANGUAGE, toLanguage);
        ps.store(new FileWriter(OPTIONS_FILE_PATH),
                "This file contains options for Wordz.\n"
                + "Don't edit manually unless you can predict the result...");
    }

    public static WordOrder getWordOrder() {
        return wordOrder;
    }

    public static void setWordOrder(WordOrder wordOrder) {
        Context.wordOrder = wordOrder;
    }

    public static int getNoWordsPerPage() {
        return noWordsPerPage;
    }

    public static void setNoWordsPerPage(int noWordsPerPage) {
        Context.noWordsPerPage = noWordsPerPage;
    }

    public static boolean isAutoJumpToMdifiedWord() {
        return autoJumpToModifiedWord;
    }

    public static void setAutoJumpToNewWord(boolean autoJumpToModifiedWord) {
        Context.autoJumpToModifiedWord = autoJumpToModifiedWord;
    }

    public static String getFromLanguage() {
        return fromLanguage;
    }

    public static void setFromLanguage(String fromLanguage) {
        Context.fromLanguage = fromLanguage;
    }

    public static String getToLanguage() {
        return toLanguage;
    }

    public static void setToLanguage(String toLanguage) {
        Context.toLanguage = toLanguage;
    }

}
