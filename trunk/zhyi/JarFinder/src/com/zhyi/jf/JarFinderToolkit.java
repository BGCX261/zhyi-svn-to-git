package com.zhyi.jf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFinderToolkit {

    private static final String CLASS_EXTENTION = ".class";

    public static List<String> findClass(String key, JarFile jarFile) {
        List<String> classNames = new ArrayList<String>();
        Enumeration<JarEntry> jes = jarFile.entries();
        while (jes.hasMoreElements()) {
            JarEntry je = jes.nextElement();
            String className = getClassName(je);
            if (className != null && className.contains(key)) {
                classNames.add(className);
            }
        }
        return classNames;
    }

    private static String getClassName(JarEntry je) {
        String name = je.getName().replaceAll("/", "\\.");
        if (name.endsWith(CLASS_EXTENTION)) {
            return name.substring(0, name.length() - CLASS_EXTENTION.length());
        } else {
            return null;
        }
    }

}
