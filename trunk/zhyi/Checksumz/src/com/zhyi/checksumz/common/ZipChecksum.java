/*
 * ZipChecksum.java
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

import com.zhyi.checksumz.util.ChecksumzToolkit;
import java.util.zip.Checksum;

/**
 * Wraps JDK's {@code java.util.zip.Checksum}.
 */
public class ZipChecksum implements Checksumz {

    private Checksum checksum;

    public ZipChecksum(Checksum checksum) {
        this.checksum = checksum;
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
    public String getHexResult(boolean upperCase) {
        String hexResult = ChecksumzToolkit.prependZero(
                Long.toHexString(checksum.getValue()), 8);
        return upperCase ? hexResult.toUpperCase() : hexResult;
    }

}
