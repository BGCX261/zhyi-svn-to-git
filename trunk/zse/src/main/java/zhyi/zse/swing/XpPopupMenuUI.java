/*
 * UIHelper.java
 *
 * Copyright (C) 2011 Zhao Yi
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
package zhyi.zse.swing;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

/**
 * This class fixes the following issue of popup menu's UI: In Windows XP and
 * Windows Server 2003, under Windows look and feel, popup menu lacks a padding
 * between the text and the border.
 * @author Zhao Yi
 */
public class XpPopupMenuUI extends BasicPopupMenuUI {
    public static ComponentUI createUI(JComponent c) {
        return new XpPopupMenuUI();
    }

    @Override
    public Popup getPopup(JPopupMenu popup, int x, int y) {
        // JComboBox has different UI than normal popup menu. Ignore it.
        if (!(popup.getInvoker() instanceof JComboBox)) {
            popup.setBorder(BorderFactory.createCompoundBorder(
                    UIManager.getBorder("PopupMenu.border"),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        }
        return super.getPopup(popup, x, y);
    }
}
