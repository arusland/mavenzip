package io.arusland.allinone.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ruslan on 21.06.2016.
 *
 * From http://www.mkyong.com/java/how-to-compress-files-in-zip-format/
 */
public class ZipUtil {
    private final List<String> fileList;
    private final String sourceFullpath;
    private final File sourceDir;

    ZipUtil(File dir) {
        this.fileList = new ArrayList<String>();
        this.sourceDir = dir.isDirectory() ? dir : dir.getParentFile();
        try {
            this.sourceFullpath = this.sourceDir.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDir(List<File> files, File destinationZipFile) {
        File dir = files.size() == 1 ? files.get(0) : files.get(0).getParentFile();
        ZipUtil appZip = new ZipUtil(dir);
        appZip.generateFileList(files);
        appZip.zipIt(destinationZipFile);
    }

    /**
     * Zip it
     *
     * @param zipFile output ZIP file location
     */
    public void zipIt(File zipFile) {

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);

            for (String file : this.fileList) {

                System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(sourceFullpath + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     *
     * @param node file or directory
     */
    public void generateFileList(File node) {
        //add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    public void generateFileList(List<File> files) {
        for (File file : files) {
            generateFileList(file);
        }
    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file) {
        return file.substring(sourceFullpath.length() + 1, file.length());
    }
}
