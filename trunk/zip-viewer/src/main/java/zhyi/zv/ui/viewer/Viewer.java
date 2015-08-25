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
import java.nio.charset.Charset;
import zhyi.zse.zip.ZipItem;

/**
 * General interface for zip item viewer components.
 * @author Zhao Yi
 */
public interface Viewer {
    /**
     * View a zip item with the specified charset for text decoding.
     * @throws IOException If an error occurs when reading the zip item.
     */
    public void view(ZipItem zipItem, Charset charset) throws IOException;
}
