/*
 * ChecksumToolkit.java
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
package com.zhyi.checksumz.util;

import com.zhyi.checksumz.common.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

/**
 * A set of general-purpose methods for this application.
 */
public class ChecksumzToolkit {

    /**
     * Prepends a few zeros to a string.
     * @param s The string to be prepended with zeros.
     * @param width The length of the string after prepending zeros. If it's
     * larger than the length of the source string, the source string will be
     * returned directly.
     * @return The result string.
     */
    public static String prependZero(String s, int width) {
        int numberOfZeros = width - s.length();
        if (numberOfZeros > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numberOfZeros; i++) {
                sb.append("0");
            }
            s = sb.toString() + s;
        }
        return s;
    }

    /**
     * Gets a map of checksum algorithms supported by JDK, which maps from the
     * chechsum's name to its {@code com.zhyi.chechsumz.common.Checksumz}
     * instance. This includes implementations of both
     * {@code java.util.zip.Checksum} and {@code java.security.MessageDigest}.
     * @return A map of checksum algorithms supported by JDK.
     */
    public static Map<String, Checksumz> getSupportedChecksums() {
        Map<String, Checksumz> checksumMap = new LinkedHashMap<String, Checksumz>();
        checksumMap.put("Adler-32", new ZipChecksum(new Adler32()));
        checksumMap.put("CRC-32", new ZipChecksum(new CRC32()));
        for (Provider provider : Security.getProviders()) {
            for (Provider.Service service : provider.getServices()) {
                if (service.getType().equals("MessageDigest")) {
                    try {
                        checksumMap.put(service.getAlgorithm(), new MessageDigestChecksum(
                                MessageDigest.getInstance(service.getAlgorithm())));
                    } catch (NoSuchAlgorithmException ex) {
                        // We have actually made sure there is such an algorithm.
                    }
                }
            }
        }
        return checksumMap;
    }

    /**
     * Computes the checksums of a file.
     * @param file The file to be computed.
     * @param checksums The checksums to be used.
     * @return The results representing in hexadecimal strings.
     * @throws IOException If any I/O error occures.
     */
    public static List<String> computeChecksumsForFile(
            File file, List<Checksumz> checksums) throws IOException {
        return computeChecksumsFromStream(new FileInputStream(file), checksums);
    }

    /**
     * Computes the checksums of a text string.
     * @param text The text string to be computed.
     * @param checksums The checksums to be used.
     * @param charset The charset of the text. Different charset may lead to
     * different results.
     * @return The results representing in hexadecimal strings.
     * @throws IOException If any I/O error occures.
     */
    public static List<String> computeChecksumsForText(String text,
            List<Checksumz> checksums, Charset charset) throws IOException {
        return computeChecksumsFromStream(
                new ByteArrayInputStream(text.getBytes(charset)), checksums);
    }

    private static List<String> computeChecksumsFromStream(
            InputStream in, List<Checksumz> checksums) throws IOException {
        for (Checksumz checksum : checksums) {
            checksum.reset();
        }
        List<String> results = new ArrayList<String>();
        byte[] buffer = new byte[1024 * 1024];
        int read = -1;
        while ((read = in.read(buffer)) != -1) {
            for (Checksumz checksum : checksums) {
                checksum.update(buffer, 0, read);
            }
        }
        in.close();
        for (Checksumz checksum : checksums) {
            results.add(checksum.getHexResult(Context.isShowInUpperCase()));
        }
        return results;
    }

}
