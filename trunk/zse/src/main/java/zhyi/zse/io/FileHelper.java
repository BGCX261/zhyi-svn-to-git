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

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility methods for file.
 * @author Zhao Yi
 */
public final class FileHelper {
    private static final long KB = 1024L;
    private static final long MB = KB * KB;
    private static final long GB = MB * KB;
    private static final long TB = GB * KB;
    private static final String BYTE_FORMAT = "%,d bytes";
    private static final String KMGTB_FORMAT = "%,.2f %s (%,d bytes)";
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

    private FileHelper() {
    }

    /**
     * Extracts filename from its path. The tailing slash is removed for a
     * directory path.
     */
    public static String getFilename(String path) {
        path = path.replace('\\', '/');
        int index = getFilenameIndex(path);
        String filename = index == -1 ? path : path.substring(index + 1);
        // Remove the possible tailing slash.
        return filename.endsWith("/") ?
                filename.substring(0, filename.length() - 1) : filename;
    }

    /**
     * Gets the file's extension (always in lower case) from its path.
     * <p><b><i>Special Cases:</i></b><br>Returns {@code "/"} if the path ends
     * with a slash character, or {@code "."} if the path ends with a dot
     * character, or the filename if the file has no extension.
     * </p>
     */
    public static String getExtension(String path) {
        if (path.endsWith("/")) {
            return "/";
        } else if (path.endsWith(".")) {
            return "";
        } else {
            String filename = getFilename(path).toLowerCase(Locale.ENGLISH);
            int index = filename.lastIndexOf('.');
            return index != -1 ? filename.substring(index + 1) : filename;
        }
    }

    /**
     * Returns a path's parent path with the tailing slash, or an empty string
     * if the path has no parent.
     */
    public static String getParentPath(String path) {
        path = path.replace('\\', '/');
        int index = getFilenameIndex(path);
        return index == -1 ? "" : path.substring(0, index + 1);
    }

    /**
     * Gets the index after which the filename begins.
     */
    private static int getFilenameIndex(String path) {
        int index = path.lastIndexOf('/');
        // If the path denotes a directory, a second search is needed.
        if (index == path.length() - 1) {
            index = path.lastIndexOf('/', index - 1);
        }
        return index;
    }

    /**
     * Formats the file size in bytes to a readable string. For example,
     * 4082549760 is formatted to "3.80 GB (4,082,549,760 bytes)".
     */
    public static String formatSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException(
                    String.format("Size is negative: <[%d]>.", size));
        }

        if (size < KB) {
            return String.format(BYTE_FORMAT, size);
        } else if (size < MB) {
            return String.format(KMGTB_FORMAT, (double) size / KB, "KB", size);
        } else if (size < GB) {
            return String.format(KMGTB_FORMAT, (double) size / MB, "MB", size);
        } else if (size < TB) {
            return String.format(KMGTB_FORMAT, (double) size / GB, "GB", size);
        } else {
            return String.format(KMGTB_FORMAT, (double) size / TB, "TB", size);
        }
    }

    /**
     * Formats a time in milliseconds to a readable string, with the default
     * date/time formatter {@link DateFormat#getDateTimeInstance()}.
     * @param time The milliseconds since the epoch.
     */
    public static String formatDate(long time) {
        return DATE_FORMAT.format(new Date(time));
    }
}
