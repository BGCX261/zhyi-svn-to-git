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
package com.zhyi.checksumz.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * This application's context.
 */
public class Context {

    private static final File OPTIONS_FILE = new File(
            System.getProperty("user.home"), "Checksumz.options");
    private static final String SHOW_IN_UPPER_CASE = "ShowInUpperCase";
    private static final String CHOSEN_CHECKSUMS = "ChosenChecksums";

    private static boolean showInUpperCase;
    private static List<String> chosenChecksums;

    /**
     * Loads options from file {@code <Home>/Checksumz.options}, if it exists.
     */
    public static void load() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(OPTIONS_FILE));
            showInUpperCase = Boolean.parseBoolean(
                    properties.getProperty(SHOW_IN_UPPER_CASE));
            chosenChecksums = Arrays.asList(
                    properties.getProperty(CHOSEN_CHECKSUMS).split(","));
        } catch (Exception ex) {
            showInUpperCase = false;
            chosenChecksums = new ArrayList<String>();
        }
    }

    /**
     * Saves options to file {@code <Home>/Checksumz.options}.
     * @throws IOException If any I/O error occurs.
     */
    public static void save() throws IOException {
        String checksums = "";
        for (String checksum : chosenChecksums) {
            checksums += checksum + ",";
        }
        if (!checksums.isEmpty()) {
            checksums = checksums.substring(0, checksums.length() - 1);
        }

        Properties properties = new Properties();
        properties.put(SHOW_IN_UPPER_CASE, showInUpperCase ? "true" : "false");
        properties.put(CHOSEN_CHECKSUMS, checksums);

        properties.store(new FileWriter(OPTIONS_FILE),
                " This file contains options for Checksumz.\n"
                + " Don't edit manually unless you can predict the result...");
    }

    public static boolean isShowInUpperCase() {
        return showInUpperCase;
    }

    public static void setShowInUpperCase(boolean showInUpperCase) {
        Context.showInUpperCase = showInUpperCase;
    }

    public static List<String> getChosenChecksums() {
        return chosenChecksums;
    }

    public static void setChosenChecksums(List<String> chosenChecksums) {
        Context.chosenChecksums = chosenChecksums;
    }

}
