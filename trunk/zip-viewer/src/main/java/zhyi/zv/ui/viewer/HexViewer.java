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
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import javax.swing.JTextArea;
import zhyi.zse.swing.SwingHelper;
import zhyi.zse.zip.ZipItem;

/**
 * @author Zhao Yi
 */
public class HexViewer extends JTextArea implements Viewer {
    @SuppressWarnings("LeakingThisInConstructor")
    public HexViewer() {
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        SwingHelper.addPopupMenuForTextComponent(this);
    }

    @Override
    public void view(ZipItem zipItem, Charset charset) throws IOException {
        try (Reader in = new HexReader(zipItem.openStream())) {
            read(in, null);
        }
    }

    /**
     * Reads bytes as hex characters every four of which are separated by a
     * whitespace.
     */
    private static class HexReader extends Reader {
        private InputStream in;
        private char[] cache;

        public HexReader(InputStream in) {
            this.in = in;
            cache = new char[0];
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (cache.length >= len) {
                System.arraycopy(cache, 0, cbuf, off, len);
                cache = cache.length == len ? new char[0]
                        : Arrays.copyOfRange(cache, len, cache.length);
                return len;
            }

            int count = 0;
            if (cache.length > 0) {
                System.arraycopy(cache, 0, cbuf, off, cache.length);
                count += cache.length;
                off += cache.length;
                len -= cache.length;
            }

            // Every hex group contains four hex characters and one whitespace
            // (totally five characters), which requires two bytes to be read
            // from the original stream.
            // `(len / 5 + 1) * 2' is the number of bytes to be read. This is
            // inaccurate when len is a multiple of 5, but is't absolutly okay
            // to read more bytes than just adequate.
            byte[] bytes = new byte[(len / 5 + 1) * 2];
            cache = new char[(len / 5 + 1) * 5];
            int pos = 0;
            int nread = in.read(bytes);
            if (nread == -1) {
                return -1;
            }

            for (int i = 0; i < nread; i++) {
                String hex = String.format("%02X", bytes[i]);
                cache[pos++] = hex.charAt(0);
                cache[pos++] = hex.charAt(1);
                if (pos % 5 == 4) {
                    cache[pos++] = ' ';
                }
            }

            // Now `pos' is the actual length of valid data in `cache'.
            if (pos >= len) {
                System.arraycopy(cache, 0, cbuf, off, len);
                cache = pos == len ? new char[0]
                        : Arrays.copyOfRange(cache, len, pos);
                count += len;
            } else {
                System.arraycopy(cache, 0, cbuf, off, pos);
                cache = new char[0];
                count += pos;
            }

            return count;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}
