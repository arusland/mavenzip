package io.arusland.allinone.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by ruslan on 18.06.2016.
 */
public final class FileUtil {
    public static String getRelativePath(File file, File root) {
        if (file == null || root == null) {
            return StringUtils.EMPTY;
        }

        if (file.equals(root)) {
            return "/";
        }

        try {
            String filePath = file.getCanonicalPath();
            String rootPath = root.getCanonicalPath();

            if (filePath.startsWith(rootPath)) {
                return filePath.substring(rootPath.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return StringUtils.EMPTY;
    }

    public static String getFileContent(File file) {
        try {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                try {
                    return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getResourceFileContent(String fileName) {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        return getFileContent(file);
    }


    public static void writeFile(File file, String content) {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static String getFullPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
