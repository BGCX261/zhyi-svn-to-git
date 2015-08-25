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
package zhyi.zse.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility methods for I/O.
 * @author Zhao Yi
 */
public final class IoHelper {
    private IoHelper() {
    }

    /**
     * Closes an {@link Closeable} and ignores any exception thrown from {@link
     * Closeable#close()}.
     */
    public static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception ex) {
        }
    }

    /**
     * Writes all bytes available from an {@link InputStream} to an {@link
     * OutputStream}. Both streams remain open after this method returns.
     * @throws IOException If an I/O error has occurred.
     */
    public static void transfer(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024 * 1024];
        int read = -1;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
