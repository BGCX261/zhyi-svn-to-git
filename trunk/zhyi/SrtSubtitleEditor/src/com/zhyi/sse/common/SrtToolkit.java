/*
 * SrtToolkit.java
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
package com.zhyi.sse.common;

import com.zhyi.sse.model.Subtitle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

/**
 * This class contains methods for processing SRT files.
 */
public class SrtToolkit {

    private static final Pattern TIME_PATTERN = Pattern.compile(
            "^\\d{2}(:[0-5]\\d){2},\\d{3}$");
    private static final String TIME_DELIMITER = "-->";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int HOUR_TO_MS = 60 * 60 * 1000;
    private static final int MINUTE_TO_MS = 60 * 1000;
    // Used for indicating which line in the SRT file is bad formatted.
    private static int lineNumber = 0;

    /**
     * Checks whether the time is legal for subtitle. A leagl time should be
     * formatted as {@code hh:MM:ss,mmm}.
     * @param time The time to be checked.
     * @return {@code true} if legal, otherwise {@code false}.
     */
    public static boolean isLegalTime(String time) {
        return TIME_PATTERN.matcher(time).matches();
    }

    /**
     * Moves a time by adding {@code offset} milliseconds (can be negative)
     * to it. The time string must be formatted as hh:MM:ss,mmm.
     * @param originalTime The original time.
     * @param offset The offset to be added to the original time.
     * @return The new time.
     * @throws SubtitleFormatException If {@code originalTime} is not
     * formatted in hh:MM:ss,mmm.
     */
    public static String moveTime(String originalTime, int offset)
            throws SubtitleFormatException {
        return formatMilliseconds(calculateMilliseconds(originalTime) + offset);
    }

    /**
     * Calculates how many milliseconds a given time of format hh:MM:ss,mmm has.
     * @param time The time string formatted in hh:MM:ss,mmm.
     * @return The milliseconds the time has.
     * @throws SubtitleFormatException If {@code time} is not formatted in
     * hh:MM:ss,mmm.
     */
    public static int calculateMilliseconds(String time)
            throws SubtitleFormatException {
        if (!isLegalTime(time)) {
            throw new SubtitleFormatException(
                    "Time should be formatted in hh:MM:ss,mmm.\n");
        }
        int hours = Integer.parseInt(time.substring(0, 2));
        int minutes = Integer.parseInt(time.substring(3, 5));
        int seconds = Integer.parseInt(time.substring(6, 8));
        int milliseconds = Integer.parseInt(time.substring(9));
        return hours * HOUR_TO_MS + minutes * MINUTE_TO_MS + seconds * 1000 + milliseconds;
    }

    /**
     * Returns the string representation for a given time. The returned value is
     * of format hh:MM:ss,mmm.
     * @param ms The time in milliseconds.
     * @return The string representation for the time.
     */
    public static String formatMilliseconds(int ms) {
        int hour = ms / HOUR_TO_MS;
        int remainder = ms % HOUR_TO_MS;
        int minutes = remainder / MINUTE_TO_MS;
        remainder = remainder % MINUTE_TO_MS;
        int seconds = remainder / 1000;
        int milliseconds  = remainder % 1000;
        return prependZero(hour, 2) + ":" + prependZero(minutes, 2) + ":"
                + prependZero(seconds, 2) + "," + prependZero(milliseconds, 3);
    }

    /**
     * Parses an SRT file to a list of {@code Subtitle} objects. If the SRT
     * file contains no subtitle, an empty list will be returned.
     * @param srtFile The SRT file.
     * @param charset The charset used to read the file.
     * @return A list containing all subtitles in the SRT file.
     * @throws SubtitleFormatException If a subtitle has bad formatted begin
     * or end time.
     * @throws IOException If any I/O error occureed.
     */
    public static List<Subtitle> parseSrtFile(File srtFile, Charset charset)
            throws SubtitleFormatException, IOException {
        lineNumber = 0;
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(srtFile), charset));
        List<Subtitle> subtitles = new ArrayList<Subtitle>();
        Subtitle subtitle = readNextSubtitle(in, true);
        while (subtitle != null) {
            subtitles.add(subtitle);
            subtitle = readNextSubtitle(in, false);
        }
        in.close();
        return subtitles;
    }

    /**
     * Saves subtitles to an SRT file.
     * @param subtitles The list of subtitles to save.
     * @param srtFile The target SRT file.
     * @param charset The charset used to save the file.
     * @throws IOException If any I/O error occureed.
     */
    public static void saveSrtFile(List<Subtitle> subtitles, File srtFile,
            Charset charset) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(srtFile), charset));
        int index = 1;
        for (Subtitle subtitle : subtitles) {
            out.println(index++);
            out.println(subtitle.getBeginTime() + " " + TIME_DELIMITER + " "
                    + subtitle.getEndTime());
            out.println(subtitle.getContent());
            out.println();
        }
        out.close();
    }

    /**
     * This method makes sure the file seleted from {@code srtFileChooser} has
     * a ".srt" extension.
     * @param srtFileChooser The SRT file chooser.
     * @return The selected file having a ".srt" extension.
     */
    public static File getSelectedSrtFile(JFileChooser srtFileChooser) {
        String path = srtFileChooser.getSelectedFile().getPath();
        if (!path.toLowerCase().endsWith(".srt")) {
            path += ".srt";
        }
        return new File(path);
    }

    // Returns the next subtitle in the reader, or null if no subtitle is available.
    private static Subtitle readNextSubtitle(BufferedReader in,
            boolean isFirstSubtitle) throws SubtitleFormatException, IOException {
        String line = readNonEmptyLine(in);
        if (line == null) {
            // The end of SRT file is reached.
            return null;
        }
        line = line.trim();

        // Passby the index of the first subtitle. The other indexes will be
        // processed while reading the content of previous subtitle.
        if (isFirstSubtitle) {
            try {
                Integer.parseInt(line);
                line = readNonEmptyLine(in).trim();
            } catch (NumberFormatException ex) {
            }
        }

        Subtitle subtitle = new Subtitle();
        String[] timelines = parseTimeline(line);
        subtitle.setBeginTime(timelines[0]);
        subtitle.setEndTime(timelines[1]);
        subtitle.setContent(readContent(in));
        return subtitle;
    }

    private static String[] parseTimeline(String timeline)
            throws SubtitleFormatException {
        String[] times = timeline.split(TIME_DELIMITER);
        String[] timelines = new String[times.length];
        for (int i = 0; i < timelines.length; i++) {
            timelines[i] = times[i].trim();
        }
        if (timelines.length != 2 || !isLegalTime(timelines[0]) || !isLegalTime(timelines[1])) {
            throw new SubtitleFormatException(
                    "Timeline is bad formatted at line "+ lineNumber + " in the SRT file.");
        }
        return timelines;
    }

    private static String readContent(BufferedReader in) throws IOException {
        StringBuilder content = new StringBuilder();
        String line = readLine(in);
        boolean previousLineIsEmpty = false;
        while (line != null) {
            boolean encounteredInteger = true;
            try {
                Integer.parseInt(line.trim());
            } catch (NumberFormatException ex) {
                encounteredInteger = false;
            }

            // A line separator followed by an integer marks the beginning
            // of the next subtitle.
            if (previousLineIsEmpty && encounteredInteger) {
                break;
            } else {
                previousLineIsEmpty = line.isEmpty();
                content.append(line);
                content.append(LINE_SEPARATOR);
                line = readLine(in);
            }
        }
        int length = content.length();
        if (length != 0) {
            // Remove the redundant line separators.
            content.delete(length - LINE_SEPARATOR.length(), length);
            // If it's not the end of file, one more redundant line separator
            // should be removed because next subtitle's index was also read.
            if (line != null) {
                length = content.length();
                content.delete(length - LINE_SEPARATOR.length(), length);
            }
        }
        return content.toString();
    }

    private static String readLine(BufferedReader in) throws IOException {
        String line = in.readLine();
        lineNumber++;
        return line;
    }

    private static String readNonEmptyLine(BufferedReader in) throws IOException {
        String line = null;
        // Skip empty lines.
        do {
            line = readLine(in);
            if (line == null) {
                // The end of file.
                return null;
            }
        } while (line.isEmpty());
        return line;
    }

    private static String prependZero(int n, int width) {
        String s = Integer.toString(n);
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

}
