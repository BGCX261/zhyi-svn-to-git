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
package zhyi.zse.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import zhyi.zse.io.FileHelper;
import zhyi.zse.io.IoHelper;
import zhyi.zse.util.CollectionHelper;
import zhyi.zse.util.ParameterValidator;

/**
 * A simple implementation of zip file system.
 * @author Zhao Yi
 */
public class ZipSystem extends ZipItem {
    private Charset charset;

    /**
     * Same as {@code ZipSystem(file, Charset.defaultCharset())}.
     */
    public ZipSystem(File file) throws IOException {
        this(file, Charset.defaultCharset());
    }

    /**
     * Constructs a normal zip system from a file system file. The full path is
     * set to {@code relativePath}, and owner is {@code null}.
     * @param file The file that in zip format. Note the constructor doesn't
     * check if it's really in zip format.
     * @param charset For decoding child items.
     * @throws IOException If an I/O error occurs when getting the file's
     * canonical path.
     */
    public ZipSystem(File file, Charset charset) throws IOException {
        relativePath = file.getCanonicalPath().replace('\\', '/');
        this.charset = Objects.requireNonNull(charset);
        fullPath = relativePath;
    }

    /**
     * Same as {@code ZipSystem(owner, relativePath, Charset.defaultCharset())}.
     */
    public ZipSystem(ZipSystem owner, String relativePath) {
        this(owner, relativePath, Charset.defaultCharset());
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Constructs a nested zip system.
     * @param owner The zip system inside which this zip system is compressed.
     * @param relativePath Relative path of this This zip system to the owner.
     * @param charset The charset for decoding child items.
     */
    public ZipSystem(ZipSystem owner, String relativePath, Charset charset) {
        super(owner, ParameterValidator.requireUnmatched(
                relativePath, ".*!/", "`path' denotes a directory."));
        this.charset = Objects.requireNonNull(charset);
    }

    /**
     * Returns the number of items stored in the zip system. Because directories
     * can be omitted in the zip file, so the returned value isn't necessarily
     * equal to the number of files produced after decompression.
     */
    public int itemCount() throws IOException {
        if (isZipFile()) {
            try (ZipFile zip = new ZipFile(relativePath, charset)) {
                return zip.size();
            }
        } else {
            try (ZipInputStream zin = new ZipInputStream(openStream(), charset)) {
                int count = 0;
                while (zin.getNextEntry() != null) {
                    count++;
                }
                return count;
            }
        }
    }

    /**
     * Returns the total size in bytes of all files produced by uncompressing
     * this zip system. Note this may not be accurate, because it's possible
     * that the size of a zip entry is unknown.
     */
    public long getUncompressedSize() throws IOException {
        long size = 0;
        if (isZipFile()) {
            try (ZipFile zip = new ZipFile(relativePath, charset)) {
                for (ZipEntry ze : CollectionHelper.iterable(zip.entries())) {
                    if (ze.getSize() != -1) {
                        size += ze.getSize();
                    }
                }
            }
        } else {
            try (ZipInputStream zin = new ZipInputStream(openStream(), charset)) {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    zin.closeEntry();
                    if (ze.getSize() != -1) {
                        size += ze.getSize();
                    }
                }
            }
        }
        return size;
    }

    /**
     * Returns all root child items as a list, some of which can be instances of
     * {@link ZipSystem}. Directories ignored in the zip file are made up.
     */
    public List<ZipItem> listRoots() throws IOException {
        return listChildren("");
    }

    /**
     * Returns the give directory's child items, or {@code null} if it doesn't
     * denotes a directory. Specially, returns root items if {@code dir} is
     * empty.
     */
    public List<ZipItem> listChildren(String dir) throws IOException {
        if (!dir.isEmpty() && !dir.endsWith("/")) {
            return null;
        }

        List<ZipItem> children = new ArrayList<>();
        Set<String> childDirs = new HashSet<>();

        ZipEntryReader zer = isZipFile() ?
                new FileZipEntryReader(new ZipFile(relativePath, charset))
                : new StreamZipEntryReader(new ZipInputStream(openStream(), charset));
        ZipEntry ze = null;
        while ((ze = zer.readNextEntry()) != null) {
            String name = ze.getName();
            if (!ze.isDirectory() && FileHelper.getParentPath(name).equals(dir)) {
                ZipInputStream zin = new ZipInputStream(zer.getInputStream(), charset);
                if (zin.getNextEntry() != null) {
                    children.add(new ZipSystem(this, name, charset));
                } else {
                    children.add(new ZipItem(this, name));
                }
            }

            // The zip specification doesn't require directories to be included
            // in the zip file, so them have to be made up manually.
            if (name.startsWith(dir)) {
                int index = name.indexOf('/', dir.length());
                if (index != -1) {
                    childDirs.add(name.substring(0, index + 1));
                }
            }
        }
        zer.close();

        for (String childDir : childDirs) {
            children.add(new ZipItem(this, childDir));
        }
        return children;
    }

    /**
     * A helper interface to harmonize walking through entries from {@code
     * ZipFile} and {@link ZipInputStream}.
     */
    private abstract static interface ZipEntryReader {
        /**
         * Reads next {@link ZipEntry}, or {@code null} if no more entry is
         * available.
         */
        public ZipEntry readNextEntry() throws IOException;

        /**
         * Gets the {@link InputStream} for reading data from the current
         * {@link ZipEntry}.
         */
        public InputStream getInputStream() throws IOException;

        /**
         * Releases resources.
         */
        public void close();
    }

    private static class FileZipEntryReader implements ZipEntryReader {
        private ZipFile zip;
        private Enumeration<? extends ZipEntry> e;
        private ZipEntry ze;

        public FileZipEntryReader(ZipFile zip) {
            this.zip = zip;
            e = zip.entries();
        }

        @Override
        public ZipEntry readNextEntry() throws IOException {
            if (e.hasMoreElements()) {
                ze = e.nextElement();
            } else {
                ze = null;
            }
            return ze;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return zip.getInputStream(ze);
        }

        @Override
        public void close() {
            IoHelper.closeSilently(zip);
        }
    }

    private static class StreamZipEntryReader implements ZipEntryReader {
        private ZipInputStream zin;

        public StreamZipEntryReader(ZipInputStream zin) {
            this.zin = zin;
        }

        @Override
        public ZipEntry readNextEntry() throws IOException {
            ZipEntry ze = zin.getNextEntry();
            if (ze == null) {
                return null;
            }
            return ze;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return zin;
        }

        @Override
        public void close() {
            IoHelper.closeSilently(zin);
        }
    }
}
