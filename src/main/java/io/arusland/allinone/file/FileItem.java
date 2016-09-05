package io.arusland.allinone.file;

import org.apache.commons.lang3.Validate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ruslan on 17.06.2016.
 */
public class FileItem implements Comparable<FileItem> {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final File file;
    private final String path;
    private final String name;
    private final String sizeStr;
    private final String dateStr;


    public FileItem(String name, File file, String path, String sizeStr) {
        this.name = Validate.notBlank(name, "name");
        this.file = Validate.notNull(file, "file");
        this.path = Validate.notBlank(path, "path");
        this.sizeStr = Validate.notNull(sizeStr, "sizeStr");
        this.dateStr = DATE_FORMAT.format(new Date(file.lastModified()));
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isFile() {
        return file.isFile();
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public String getSizeStr() {
        return sizeStr;
    }

    public String getDateStr() {
        return dateStr;
    }

    @Override
    public int compareTo(FileItem rhs) {
        if (isDirectory()) {
            if (rhs.isDirectory()) {
                return getName().compareTo(rhs.getName());
            } else {
                return -1;
            }
        } else if (rhs.isDirectory()){
            return 1;
        }

        return getName().compareTo(rhs.getName());
    }
}
