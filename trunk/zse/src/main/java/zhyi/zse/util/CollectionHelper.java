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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Utility methods for collection framework.
 * @author Zhao Yi
 */
public final class CollectionHelper {
    private CollectionHelper() {
    }

    /**
     * Returns an {@link ArrayList} containing all elements from the specified
     * {@link Iterator}.
     */
    public static <T> ArrayList<T> list(Iterator<T> it) {
        ArrayList<T> list = new ArrayList<>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    /**
     * Adapts an {@link Enumeration} to an {@link Iterator}. The returned
     * {@link Iterator} doesn't support operation {@link Iterator#remove()}.
     */
    public static <T> Iterator<T> iterate(final Enumeration<T> e) {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return e.hasMoreElements();
            }

            @Override
            public T next() {
                return e.nextElement();
            }

            @Override
            public void remove() {
            }
        };
    }

    /**
     * Turns an {@link Iterator} to an {@link Iterable} that can be used with a
     * for-each loop.
     */
    public static <T> Iterable<T> iterable(final Iterator<T> it) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return it;
            }
        };
    }

    /**
     * Turns an {@link Enumeration} to an {@link Iterable} that can be used with
     * a for-each loop.
     */
    public static <T> Iterable<T> iterable(final Enumeration<T> e) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterate(e);
            }
        };
    }
}
