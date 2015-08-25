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
package zhyi.zv.ui.viewer;

/**
 * Supported viewer types.
 * @author Zhao Yi
 */
public enum ViewerType {
    PLAIN_TEXT("Plain Text", PlainTextViewer.class),
    RICH_TEXT("Rich Text", RichTextViewer.class),
    HTML("HTML", HtmlViewer.class),
    IMAGE("Image", ImageViewer.class),
    HEXADECIMAL("Hexadecimal", HexViewer.class);

    private String displayName;
    private Class<? extends Viewer> viewerClass;

    private ViewerType(String displayName, Class<? extends Viewer> viewerClass) {
        this.displayName = displayName;
        this.viewerClass = viewerClass;
    }

    public Class<? extends Viewer> getViewClass() {
        return viewerClass;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
