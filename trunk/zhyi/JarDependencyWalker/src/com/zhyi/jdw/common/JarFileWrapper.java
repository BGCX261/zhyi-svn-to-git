/*
 * FileWrapper.java
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
package com.zhyi.jdw.common;

import com.zhyi.zylib.toolkit.FileToolkit;
import java.util.jar.JarFile;

/**
 * Wraps {@code java.util.jar.JarFile} for providing the file name instead of
 * the file path via {@code toString()}.
 */
public class JarFileWrapper {

    private JarFile jar;

    public JarFileWrapper(JarFile jar) {
        this.jar = jar;
    }

    public JarFile getJarFile() {
        return jar;
    }

    @Override
    public String toString() {
        return FileToolkit.getFileNameFromPath(jar.getName());
    }

}
