/*
 * MessageDigestChecksum.java
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
import java.security.MessageDigest;

/**
 * Wraps JDK's {@code java.security.MessageDigest}.
 */
public class MessageDigestChecksum implements Checksumz {

    private MessageDigest md;

    public MessageDigestChecksum(MessageDigest md) {
        this.md = md;
    }

    @Override
    public void update(byte[] data, int offset, int length) {
        md.update(data, offset, length);
    }

    @Override
    public void reset() {
        md.reset();
    }

    @Override
    public String getHexResult(boolean upperCase) {
        byte[] result = md.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length / 4; i++) {
            int tmp = result[4 * i] << 24;
            tmp |= (result[4 * i + 1] << 16) & 0x00FF0000;
            tmp |= (result[4 * i + 2] << 8) & 0x0000FF00;
            tmp |= result[4 * i + 3] & 0x000000FF;
            sb.append(ChecksumzToolkit.prependZero(Integer.toHexString(tmp), 8));
            sb.append(" ");
        }
        String hexResult = sb.substring(0, sb.length() - 1);
        return upperCase ? hexResult.toUpperCase() : hexResult;
    }

}
