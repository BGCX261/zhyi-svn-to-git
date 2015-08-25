/*
 * Context.java
 *
 * Copyright (C) 2009 Zhao Yi
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
package com.zhyi.sse.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * This class manages the application's context, such as options.
 */
public class Context {

    private static final File OPTIONS_FILE = new File(
            System.getProperty("user.home"), "sse.options");
    private static final String CHARSET = "charset";
    private static final String EDIT_CONTINUOUSLY = "editcontinuously";

    private static Charset charset;
    private static boolean editContinuously;

    /**
     * Loads options from file {@code <Home>/sse.options}. If the file doesn't
     * exist, or if has syntax error, the default options will be set.
     */
    public static void loadOptions() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(OPTIONS_FILE));
            charset = Charset.forName(properties.getProperty(CHARSET));
            editContinuously = Boolean.parseBoolean(
                    properties.getProperty(EDIT_CONTINUOUSLY));
        } catch (Exception ex) {
            charset = Charset.defaultCharset();
            editContinuously = true;
        }
    }

    /**
     * Saves options to file {@code <Home>/sse.options}.
     * @throws IOException If any I/O error occurred.
     */
    public static void saveOptions() throws IOException {
        Properties properties = new Properties();
        properties.put(CHARSET, charset.name());
        properties.put(EDIT_CONTINUOUSLY, editContinuously ? "true" : "false");
        properties.store(new FileWriter(OPTIONS_FILE),
                " This file contains options for SRT Subtitle Editor.\n"
                + " Don't edit manually unless you can predict the result...");
    }

    public static Charset getCharset() {
        return charset;
    }

    public static void setCharset(Charset charset) {
        Context.charset = charset;
    }

    public static boolean isEditContinuously() {
        return editContinuously;
    }

    public static void setEditContinuously(boolean editContinuously) {
        Context.editContinuously = editContinuously;
    }

}
