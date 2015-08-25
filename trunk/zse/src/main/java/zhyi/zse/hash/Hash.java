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
package zhyi.zse.hash;

/**
 * The base interface for hash algorithms.
 * @author Zhao Yi
 */
public interface Hash {
    /**
     * Updates the current hash with the specified array of bytes.
     * @param data The byte array to update the hash with.
     * @param offset The start offset of the data.
     * @param length The number of bytes to use for the update.
     */
    public void update(byte[] data, int offset, int length);

    /**
     * Resets the hash to its initial value.
     */
    public void reset();

    /**
     * Completes the hash computation by reseting it, and returns the hash value
     * as a hexadecimal string (in lower case).
     */
    public String complete();
}
