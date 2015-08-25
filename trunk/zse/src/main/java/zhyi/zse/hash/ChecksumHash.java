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

import java.util.Objects;
import java.util.zip.Checksum;

/**
 * Adapts {@link Checksum} to {@link Hash}.
 * @author Zhao Yi
 */
public class ChecksumHash implements Hash {
    private Checksum checksum;

    public ChecksumHash(Checksum checksum) {
        this.checksum = Objects.requireNonNull(checksum);
    }

    @Override
    public void update(byte[] data, int offset, int length) {
        checksum.update(data, offset, length);
    }

    @Override
    public void reset() {
        checksum.reset();
    }

    @Override
    public String complete() {
        String result = String.format("%08x", checksum.getValue());
        checksum.reset();
        return result;
    }
}
