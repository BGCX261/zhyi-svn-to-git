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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import zhyi.zse.io.FileHelper;
import zhyi.zse.io.IoHelper;
import zhyi.zse.util.Pair;

/**
 * Provides extensive support for a file that is compressed inside a zip file,
 * compared to {@link ZipFile} and {@link ZipEntry}.
 * @author Zhao Yi
 */
public class ZipItem implements Comparable<ZipItem> {
    public static final String SECTION_SEPARATOR = "!/";

    protected ZipSystem owner;
    protected String relativePath;
    protected String fullPath;

    /**
     * For subclasses.
     */
    protected ZipItem() {
    }

    /**
     * Constructs a new instance. The full path is set to the concatenation of
     * the owner's full path, {@code "!/"}, and {@code relativePath}.
     * @param owner The {@link ZipSystem} inside which this {@link ZipItem} is compressed.
     * @param relativePath Relative path of this {@link ZipItem} to the owner.
     */
    public ZipItem(ZipSystem owner, String relativePath) {
        this.owner = Objects.requireNonNull(owner);
        this.relativePath = Objects.requireNonNull(relativePath);
        fullPath = owner.fullPath + SECTION_SEPARATOR + relativePath;
    }

    public ZipSystem getOwner() {
        return owner;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getName() {
        return FileHelper.getFilename(relativePath);
    }

    public boolean isDirectory() {
        return relativePath.endsWith("/");
    }

    /**
     * Queries the {@link ZipEntry} that corresponds to this {@link ZipItem},
     * from the owner. If not found, returns {@code null}.
     * <p>
     * <b><i>Note:</i></b><br>The zip specification doesn't require directories
     * to be included in the zip file, so for a directory item, even if this
     * method returns {@code null}, it doesn't necessarily mean this zip item
     * doesn't exist.
     * </p>
     * @throws IOException If an I/O error occurs during query.
     */
    public ZipEntry getZipEntry() throws IOException {
        if (isZipFile()) {
            // Use `ZipEntry' merely to hold some properties.
            File file = new File(relativePath);
            ZipEntry ze = new ZipEntry(relativePath);
            ze.setSize(file.length());
            ze.setTime(file.lastModified());
            return ze;
        }

        if (isNormalZipItem()) {
            try (ZipFile zip = new ZipFile(owner.relativePath)) {
                ZipEntry ze = zip.getEntry(relativePath);
                if (ze != null) {
                    return ze;
                }
            }
        }

        Pair<? extends InputStream, ZipEntry> pair = locateNestedZipItem();
        if (pair == null) {
            return null;
        } else {
            ZipInputStream zin = (ZipInputStream) pair.getFirst();
            zin.closeEntry();
            IoHelper.closeSilently(zin);
            return pair.getSecond();
        }
    }

    /**
     * Opens an {@code InputStream} for reading data from this zip item. Returns
     * {@code null} if this zip item denotes a directory.
     * @throws IOException If the zip item doesn't exist, or an I/O error occurs.
     */
    public InputStream openStream() throws IOException {
        if (isDirectory()) {
            return null;
        }

        if (isZipFile()) {
            return new FileInputStream(relativePath);
        }

        if (isNormalZipItem()) {
            ZipFile zip = new ZipFile(owner.relativePath, owner.getCharset());
            return new ZipEntryInputStream(zip, zip.getEntry(relativePath));
        }

        Pair<? extends InputStream, ZipEntry> pair = locateNestedZipItem();
        if (pair != null) {
            return pair.getFirst();
        }
        throw new FileNotFoundException(String.format(
                "Zip item not found - <[%s]>.", fullPath));
    }

    /**
     * Peel the onion for a nested {@link ZipItem}.
     */
    private Pair<? extends InputStream, ZipEntry>
            locateNestedZipItem() throws IOException {
        final ZipInputStream zin = new ZipInputStream(
                owner.openStream(), owner.getCharset());
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.getName().equals(relativePath)) {
                return new Pair<>(zin, ze);
            }
        }
        return null;
    }

    protected boolean isZipFile() {
        return owner == null;
    }

    protected boolean isNormalZipItem() {
        return owner.owner == null;
    }

    /**
     * Returns this zip item's child items, if it denotes a directory. Otherwise,
     * returns {@code null}. The owner's charset is used to decode children.
     * <p>There is no guarantee that the returned list will appear in any
     * specific order; they are not, in particular, guaranteed to appear in
     * alphabetical order.</p>
     * @throws IOException If an I/O error occurs.
     */
    public List<ZipItem> listChildren() throws IOException {
        return owner.listChildren(relativePath);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ZipItem other = (ZipItem) obj;
        return Objects.equals(fullPath, other.fullPath);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fullPath);
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Helps to sort {@link ZipItem}'s in a file system like order.
     */
    @Override
    public int compareTo(ZipItem o) {
        int pathOrder = fullPath.compareTo(o.fullPath);
        if (isDirectory()) {
            return o.isDirectory() ? pathOrder : -1;
        } else {
            return o.isDirectory() ? 1 : pathOrder;
        }
    }

    /**
     * For reading a {@link ZipEntry}. Close this stream also closes the
     * associated {@link ZipFile}.
     */
    private static class ZipEntryInputStream extends InputStream {
        private ZipFile zip;
        private InputStream in;

        public ZipEntryInputStream(ZipFile zip, ZipEntry ze) throws IOException {
            this.zip = zip;
            in = zip.getInputStream(ze);
        }

        @Override
        public int available() throws IOException {
            return in.available();
        }

        @Override
        public void close() throws IOException {
            IoHelper.closeSilently(in);
            IoHelper.closeSilently(zip);
        }

        @Override
        public synchronized void mark(int readlimit) {
            in.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            return in.markSupported();
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return in.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return in.read(b, off, len);
        }

        @Override
        public synchronized void reset() throws IOException {
            in.reset();
        }

        @Override
        public long skip(long n) throws IOException {
            return in.skip(n);
        }
    }
}
