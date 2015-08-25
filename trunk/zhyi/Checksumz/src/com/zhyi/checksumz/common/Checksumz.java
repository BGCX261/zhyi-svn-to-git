/*
 * Checksumz.java
 *
 * Copyright (C) 2009 Zhao Yi
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
package com.zhyi.checksumz.common;

/**
 * The base interface for checksum algorithms, acting as a harmonization of
 * JDK's {@code java.util.zip.Checksum} and {@code java.security.MessageDigest}.
 */
public interface Checksumz {

    /**
     * Updates the current checksum with the specified array of bytes.
     * @param data The byte array to update the checksum with.
     * @param offset The start offset of the data.
     * @param length The number of bytes to use for the update.
     */
    public void update(byte[] data, int offset, int length);

    /**
     * Resets the checksum to its initial value.
     */
    public void reset();

    /**
     * Returns the current checksum value as a hexadecimal string (in lower case).
     * @param upperCase Should the result be in upper case characters.
     * @return The current checksum value.
     */
    public String getHexResult(boolean upperCase);

}
