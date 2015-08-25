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
package zhyi.zv.util;

import java.util.Arrays;
import java.util.List;

/**
 * Helps convert between string list and whitespace-separated string. This is
 * tailor-made for file extensions so leading and tailing spaces are omitted.
 * @author Zhao Yi
 */
public final class StringListConverter {
    private StringListConverter() {
    }

    public static String asString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }

    public static List<String> asList(String s) {
        return Arrays.asList(s.trim().split("\\s+"));
    }
}
