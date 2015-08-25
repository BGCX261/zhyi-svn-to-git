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
package zhyi.zse.swing;

import java.awt.Cursor;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * Simulates a label using {@link JTextField} so that the user can select and
 * copy its text.
 * @author Zhao Yi
 */
public class SelectableLabel extends JTextField {
    /**
     * Creates a new instance with no text.
     */
    public SelectableLabel() {
        this(null, 0);
    }

    /**
     * Creates a new instance with the specified text.
     */
    public SelectableLabel(String text) {
        this(text, 0);
    }

    /**
     * Creates a new instance with no text and columns to calculate preferred
     * width.
     */
    public SelectableLabel(int columns) {
        this(null, columns);
    }

    /**
     * Creates a new instance with the specified text and columns to calculate
     * preferred width.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public SelectableLabel(String text, int columns) {
        super(text, columns);
        setEditable(false);
        setForeground(UIManager.getColor("Label.foreground"));
        setBackground(UIManager.getColor("Label.background"));
        setBorder(null);
        setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        SwingHelper.addPopupMenuForTextComponent(this);
    }
}
