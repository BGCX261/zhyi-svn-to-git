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

import java.security.MessageDigest;
import java.util.Objects;

/**
 * Adapts {@link MessageDigest} to {@link Hash}.
 * @author Zhao Yi
 */
public class MessageDigestHash implements Hash {
    private MessageDigest md;

    public MessageDigestHash(MessageDigest md) {
        this.md = Objects.requireNonNull(md);
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
    public String complete() {
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
