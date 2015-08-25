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
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

/**
 * All JDK supported algorithm types.
 * @author Zhao Yi
 */
public enum HashType {
    ADLER32("Adler-32"), CRC32("CRC-32"), MD2("MD2"), MD5("MD5"),
    SHA("SHA"), SHA256("SHA-256"), SHA384("SHA-384"), SHA512("SHA-512");

    private String algorithm;
    private ThreadLocal<Hash> localHash = new ThreadLocal<Hash>() {
        @Override
        protected Hash initialValue() {
            switch (algorithm) {
                case "Adler-32":
                    return new ChecksumHash(new Adler32());
                case "CRC-32":
                    return new ChecksumHash(new CRC32());
                default:
                    try {
                        return new MessageDigestHash(MessageDigest.getInstance(algorithm));
                    } catch (NoSuchAlgorithmException ex) {
                        // This should never happen.
                        throw new RuntimeException(ex);
                    }
            }
        }
    };

    private HashType(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Hash getHash() {
        return localHash.get();
    }
}
