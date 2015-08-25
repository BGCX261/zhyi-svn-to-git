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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Adler32;

/**
 * Provides convenient methods to compute hashes for a string or some bytes.
 * @author Zhao Yi
 */
public final class HashHelper {
    private HashHelper() {
    }

    private static ThreadLocal<ChecksumHash> adler32 = new ThreadLocal<ChecksumHash>() {
        @Override
        protected ChecksumHash initialValue() {
            return new ChecksumHash(new Adler32());
        }
    };

    /**
     * Computes hash for a string.
     * @param s The string to compute hash for.
     * @param charset Charset of the string.
     * @param hashType Type of hash algorithm to be used.
     */
    public static String hash(String s, Charset charset, HashType hashType) {
        return hash(s, charset, Arrays.asList(hashType)).get(0);
    }

    /**
     * Computes hash for the bytes read from a stream. The stream remains open
     * after this method returns.
     * @param in The input stream to read bytes from.
     * @param hashType Type of hash algorithm to be used.
     * @throws IOException If an I/O error occurs while reading bytes from the
     * input stream.
     */
    public static String hash(InputStream in, HashType hashType) throws IOException {
        return hash(in, Arrays.asList(hashType)).get(0);
    }

    /**
     * Computes hashes for a string.
     * @param s The string to compute hashes for.
     * @param charset Charset of the string.
     * @param hashTypes Types of hash algorithm to be used.
     * @return A list of hash results as hexadecimal strings (in lower case),
     * with the order corresponding to the order of {@code hashTypes}.
     */
    public static List<String> hash(String s, Charset charset, List<HashType> hashTypes) {
        List<String> results = null;
        try {
            results = hash(new ByteArrayInputStream(
                    s.getBytes(charset)), hashTypes);
        } catch (IOException ex) {
            // This shouldn't happen.
        }
        return results;
    }

    /**
     * Computes hashes for the bytes read from a stream. The stream remains open
     * after this method returns.
     * @param in The input stream to read bytes from.
     * @param hashTypes Types of hash algorithm to be used.
     * @return A list of hash results as hexadecimal strings (in lower case),
     * with the order corresponding to the order of {@code hashTypes}.
     * @throws IOException If an I/O error occurs while reading bytes from the
     * input stream.
     */
    public static List<String> hash(InputStream in, List<HashType> hashTypes)
            throws IOException {
        List<Hash> hashes = new ArrayList<>(hashTypes.size());
        for (HashType hashType : hashTypes) {
            hashes.add(hashType.getHash());
        }

        List<String> results = new ArrayList<>(hashes.size());
        byte[] buffer = new byte[1024 * 1024];
        int nread = 0;
        while ((nread = in.read(buffer)) != -1) {
            for (Hash hash : hashes) {
                hash.update(buffer, 0, nread);
            }
        }
        for (Hash hash : hashes) {
            results.add(hash.complete());
        }
        return results;
    }
}
