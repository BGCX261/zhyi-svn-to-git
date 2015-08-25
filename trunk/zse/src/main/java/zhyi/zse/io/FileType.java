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

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.swing.ImageIcon;
import sun.awt.shell.ShellFolder;

/**
 * Holds several properties (type description, small icon and large icon) to
 * describe a file's type. For a regular file, the type is determined by its
 * extension. Instances are obtained from {@link #getType(String)} so
 * the same type instance for different files is reusable.
 * @author Zhao Yi
 */
public class FileType {
    private static final FileType DUMMY_FILE_TYPE
            = new FileType("", new ImageIcon(), new ImageIcon());
    private static final ConcurrentMap<String, FileType> CACHE = new ConcurrentHashMap<>();
    private static final String TMP_FILE_PREFIX = "tmpFilePrefix";

    private String description;
    private ImageIcon smallIcon;
    private ImageIcon largeIcon;

    private FileType(String description, ImageIcon smallIcon, ImageIcon largeIcon) {
        this.description = description;
        this.smallIcon = smallIcon;
        this.largeIcon = largeIcon;
    }

    public String getDescription() {
        return description;
    }

    public ImageIcon getLargeIcon() {
        return largeIcon;
    }

    public ImageIcon getSmallIcon() {
        return smallIcon;
    }

    /**
     * Gets the type for a file by checking the file's extension. If the file
     * doesn't have an extension, it's considered to have an empty extension.
     * If the path ends with "/", it's considered as a directory.
     * @param path Path to the file.
     * @return The file's type, or a dummy type with empty description and icons
     * if an error has occurred.
     */
    public static FileType getType(String path) {
        try {
            String extension = FileHelper.getExtension(path);
            FileType ft = CACHE.get(extension);
            if (ft == null) {
                ft = createType(extension);
                CACHE.putIfAbsent(extension, ft);
            }
            return ft;
        } catch (Exception ex) {
            // This only happens if creation of temporary file in `createType'
            // fails, which is ideally impossible.
            ex.printStackTrace();
            return DUMMY_FILE_TYPE;
        }
    }

    private static FileType createType(String extension) throws Exception {
        FileType fileType = null;
        if (extension.equals("/")) {
            fileType = getType(new File(System.getProperty("java.io.tmpdir")));
        } else {
            // Empty extension is regarded as a unknown extension, so let JDK
            // generate a random extension.
            File tmpFile = extension.isEmpty() ?
                    File.createTempFile(TMP_FILE_PREFIX, TMP_FILE_PREFIX)
                    : File.createTempFile(TMP_FILE_PREFIX, "." + extension);
            fileType = getType(tmpFile);
            tmpFile.delete();
        }
        return fileType;
    }

    private static FileType getType(File file) throws Exception {
        ShellFolder shellFolder = ShellFolder.getShellFolder(file);
        return new FileType(shellFolder.getFolderType(),
                new ImageIcon(shellFolder.getIcon(false)),
                new ImageIcon(shellFolder.getIcon(true)));
    }
}
