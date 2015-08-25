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
package zhyi.zse.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * Utility methods for validation of method parameters. Each method in this
 * class throws an {@link IllegalArgumentException} if validation fails.
 * @author Zhao Yi
 */
public class ParameterValidator {
    private static final ConcurrentMap<String, Pattern> PATTERN_CACHE
            = new ConcurrentHashMap<>();

    /**
     * Returns {@code s} if it matches the specified regular expression, or an
     * {@link IllegalArgumentException} will be thrown.
     * @param errorMessage Detail error message for the exception on validation
     * failure. If it's {@code null}, a default error message will be used.
     */
    public static String requireMatched(String s, String regex, String errorMessage) {
        return requireMatched(s, getPattern(regex), errorMessage);
    }

    /**
     * Returns {@code s} if it matches the specified {@link Pattern}, or an
     * {@link IllegalArgumentException} will be thrown.
     * @param errorMessage Detail error message for the exception on validation
     * failure. If it's {@code null}, a default error message will be used.
     */
    public static String requireMatched(String s, Pattern pattern, String errorMessage) {
        if (!pattern.matcher(s).matches()) {
            throw new IllegalArgumentException(errorMessage != null ?
                    errorMessage : String.format("<[%s]> doesn't match <[%s]>.", s, pattern));
        }
        return s;
    }

    /**
     * Returns {@code s} if it doesn't match the specified regular expression,
     * or an {@link IllegalArgumentException} will be thrown.
     * @param errorMessage Detail error message for the exception on validation
     * failure. If it's {@code null}, a default error message will be used.
     */
    public static String requireUnmatched(String s, String regex, String errorMessage) {
        return requireUnmatched(s, getPattern(regex), errorMessage);
    }

    /**
     * Returns {@code s} if it doesn't match the specified {@link Pattern}, or
     * an {@link IllegalArgumentException} will be thrown.
     * @param errorMessage Detail error message for the exception on validation
     * failure. If it's {@code null}, a default error message will be used.
     */
    public static String requireUnmatched(String s, Pattern pattern, String errorMessage) {
        if (pattern.matcher(s).matches()) {
            throw new IllegalArgumentException(errorMessage != null ?
                    errorMessage : String.format("<[%s]> mustn't match <[%s]>.", s, pattern));
        }
        return s;
    }

    private static Pattern getPattern(String regex) {
        // Only check `PATTERN_CACHE.containsKey(regex)' can lead to concurrency
        // problems: The target `pattern' may be deleted quickly after this check.
        Pattern pattern = PATTERN_CACHE.get(regex);
        if (pattern == null) {
            pattern = Pattern.compile(regex);
            PATTERN_CACHE.putIfAbsent(regex, pattern);
        }
        return pattern;
    }
}
