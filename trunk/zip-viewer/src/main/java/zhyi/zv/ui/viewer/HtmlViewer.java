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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.swing.JEditorPane;
import zhyi.zse.swing.SwingHelper;
import zhyi.zse.zip.ZipItem;

/**
 * @author Zhao Yi
 */
public class HtmlViewer extends JEditorPane implements Viewer {
    @SuppressWarnings("LeakingThisInConstructor")
    public HtmlViewer() {
        setContentType("text/html");
        setEditable(false);
        SwingHelper.addPopupMenuForTextComponent(this);
    }

    @Override
    public void view(ZipItem zipItem, Charset charset) throws IOException {
        try (Reader in = new InputStreamReader(zipItem.openStream(), charset)) {
            read(in, null);
        }
    }
}
