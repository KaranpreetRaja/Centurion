/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * A utility class for handle path list
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public class PathList {
    /**
     * Utility method for appending path from pathFrom to pathTo.
     *
     * @param pathTo the target path
     * @param pathFrom the path to be appended to pathTo
     * @return the resulting path
     */
    public static String appendPath(String pathTo, String pathFrom) {
        if (pathTo == null || pathTo.isEmpty()) {
            return pathFrom;
        } else if (pathFrom == null || pathFrom.isEmpty()) {
            return pathTo;
        } else {
            return pathTo  + File.pathSeparator + pathFrom;
        }
    }

    /**
     * Utility method for converting a search path string to an array
     * of directory and JAR file URLs.
     *
     * @param path the search path string
     * @return the resulting array of directory and JAR file URLs
     */
    public static URL[] pathToURLs(String path) {
        StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
        URL[] urls = new URL[st.countTokens()];
        int count = 0;
        while (st.hasMoreTokens()) {
            URL url = fileToURL(new File(st.nextToken()));
            urls[count++] = url;
        }
        if (urls.length != count) {
            URL[] tmp = new URL[count];
            System.arraycopy(urls, 0, tmp, 0, count);
            urls = tmp;
        }
        return urls;
    }

    /**
     * Returns the directory or JAR file URL corresponding to the specified
     * local file name.
     *
     * @param file the File object
     * @return the resulting directory or JAR file URL
     */
    private static URL fileToURL(File file) {
        String name;
        try {
            name = file.getCanonicalPath();
        } catch (IOException e) {
            name = file.getAbsolutePath();
        }
        name = name.replace(File.separatorChar, '/');
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        // If the file does not exist, then assume that it's a directory
        if (!file.isFile()) {
            name = name + "/";
        }
        try {
            @SuppressWarnings("deprecation")
            var result = new URL("file", "", name);
            return result;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("file");
        }
    }
}
