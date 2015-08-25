/*
 * WordzLauncher.java
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
package com.zhyi.wordz;

import com.zhyi.wordz.ui.WordzFrame;
import com.zhyi.zylib.toolkit.SwingToolkit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Prepares and launches Wordz.
 */
public class WordzLauncher {

    public static void main(String[] args) {
        SwingToolkit.initSystemlookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new WordzFrame("Wordz");
                frame.setSize(800, 500);
                SwingToolkit.showWindow(frame, null);
            }

        });
    }

}
