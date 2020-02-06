package io.arusland.allinone;

import io.arusland.allinone.file.FileItem;
import io.arusland.allinone.utils.FileUtil;
import io.arusland.allinone.utils.MavenUtil;
import io.arusland.allinone.utils.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ruslan on 17.06.2016.
 */
public class MainApp {
    private final static Logger log = Logger.getLogger(MainApp.class.getName());
    private final HttpServletRequest request;
    private HttpServletResponse response;
    private File root;
    private File currentDir;
    private String currentDirFullPath;
    private String pomContent;
    private final List<Message> messages = new ArrayList<Message>();

    public MainApp(HttpServletRequest request, HttpServletResponse response) {
        this.request = Validate.notNull(request, "request");
        this.response = Validate.notNull(response, "response");
    }

    public void handleIndex() {
        root = getRoot();
        currentDir = getCurrentDir();
        pomContent = loadCurrentPomContent(request);
        currentDirFullPath = FileUtil.getFullPath(currentDir);

        try {
            handleCommand();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            messages.add(new Message("Null pointer exception!", "error"));
        } catch (Exception ex) {
            ex.printStackTrace();
            messages.add(new Message(ex.getMessage(), "error"));
        }

        log.info("BASE DIR: " + root);
        log.info("CURRENT DIR: " + currentDirFullPath);
    }

    public void handleDownload() {
        root = getRoot();
        currentDir = getCurrentDir();

        try {
            log.info("Downloading file: " + currentDir);

            if (currentDir.isFile()) {
                downloadFile(currentDir);
            } else {
                messages.add(new Message("You cannot download this file: " + currentDir, "error"));
            }
        } catch (Exception ex) {
            messages.add(new Message(ex.getMessage(), "error"));
        }
    }

    private void downloadFile(File downloadFile) throws IOException {
        FileInputStream inStream = new FileInputStream(downloadFile);
        String mimeType = "application/octet-stream";
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.close();
    }

    private void handleCommand() {
        String useNewParam = request.getParameter("useNewMethod");
        log.info("param: " + useNewParam);

        if (StringUtils.isNotBlank(request.getParameter("btnMaven1"))) {
            if (StringUtils.isNotBlank(request.getParameter("useNewMethod")))
                MavenUtil.goOffline2(getPomFile(), new File(getPomFile().getParentFile(), "repository"), messages);
            else
                MavenUtil.goOffline(getPomFile(), new File(getPomFile().getParentFile(), "repository"), messages);
        } else {
            boolean isZip = StringUtils.isNotBlank(request.getParameter("btnZip"));
            boolean isDelete = StringUtils.isNotBlank(request.getParameter("btnDelete"));

            if (isZip || isDelete) {
                List<File> selectedFiles = getSelectedFiles();

                if (selectedFiles.size() > 0) {
                    if (isZip) {
                        doZip(selectedFiles);
                    } else if (isDelete) {
                        doDelete(selectedFiles);
                    }
                } else {
                    messages.add(new Message("You must select at least one file", "error"));
                }
            }
        }
    }

    private List<File> getSelectedFiles() {
        if (canShowDir()) {
            String[] selected = request.getParameterValues("selected[]");

            if (selected == null) {
                selected = new String[0];
            }

            List<File> result = new ArrayList<File>();

            for (String selectedFile : selected) {
                File file = new File(currentDir, selectedFile);

                if (file.exists()) {
                    result.add(file);
                }
            }

            return result;
        }

        return Collections.emptyList();
    }

    private void doDelete(List<File> selected) {
        try {
            for (File file : selected) {
                log.info("Deleting " + file);
                FileUtils.deleteQuietly(file);
            }
        } catch (Exception ex) {
            messages.add(new Message(StringEscapeUtils.escapeHtml(ex.getMessage()), "error"));
        }
    }

    private void doZip(List<File> selected) {
        try {
            String targetZipFilePath;

            if (selected.size() == 1) {
                targetZipFilePath = selected.get(0).getAbsolutePath();
            } else {
                File parent = selected.get(0).getParentFile();
                targetZipFilePath = new File(parent, parent.getName()).getAbsolutePath();
            }

            File targetZipFile = new File(targetZipFilePath + ".zip");
            int index = 2;

            while (targetZipFile.exists()) {
                targetZipFile = new File(targetZipFilePath + "_" + (index++) + ".zip");
            }

            ZipUtil.zipDir(selected, targetZipFile);
        } catch (Exception ex) {
            messages.add(new Message(StringEscapeUtils.escapeHtml(ex.getMessage()), "error"));
        }
    }

    public String getCurrentDirFullPath() {
        return currentDirFullPath;
    }

    public boolean canShowDir() {
        return currentDir != null && currentDir.isDirectory();
    }

    public String getPomContent() {
        return pomContent;
    }

    public List<FileItem> getFiles() {
        ArrayList<FileItem> items = new ArrayList<FileItem>();

        if (canShowDir()) {
            File[] files = currentDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    items.add(new FileItem(file.getName(), file, FileUtil.getRelativePath(file, root),
                            getFriendlySize(file)));
                }

                Collections.sort(items);
            } else {
                items.add(new FileItem("!ERROR: Failed to list files!", currentDir,
                        FileUtil.getRelativePath(currentDir, root), "!FAILED!"));
            }

            File upDir = currentDir.getParentFile();

            if (upDir != null) {
                String updirPath = FileUtil.getRelativePath(upDir, root);
                if (StringUtils.isNotBlank(updirPath)) {
                    items.add(0, new FileItem("..", upDir, updirPath, getFriendlySize(upDir)));
                }
            }
        }

        return items;
    }

    private String getFriendlySize(File file) {
        if (file.isDirectory()) {
            return "-";
        }

        return FileUtils.byteCountToDisplaySize(file.length());
    }

    private File getRoot() {
        String fileRoot = ObjectUtils.firstNonNull(request.getServletContext().getInitParameter("ROOT_DIR"),
                System.getProperty("user.home") + "/mavenzip", ".");

        File file = new File(fileRoot);

        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception ex) {
                log.info("ERROR: " + ex.getMessage() + "; stack: " + ex.toString());
            }
        }

        return file;
    }

    private File getCurrentDir() {
        String path = request.getParameter("path");
        File currentDir = root;

        if (StringUtils.isNotBlank(path)) {
            currentDir = new File(root, path.replaceAll("[\\\\/]+$", StringUtils.EMPTY));
        }

        return currentDir;
    }

    private String loadCurrentPomContent(HttpServletRequest request) {
        String pomRaw = StringUtils.EMPTY;

        if (canShowDir()) {
            String postData = request.getMethod() == "POST" ? request.getParameter("pomContent") : StringUtils.EMPTY;
            File pomFile = getPomFile();

            if (StringUtils.isNotEmpty(postData)) {
                FileUtil.writeFile(pomFile, postData);
                pomRaw = postData;
            } else if (pomFile.exists()) {
                pomRaw = FileUtil.getFileContent(pomFile);
            }

            if (StringUtils.isBlank(pomRaw)) {
                pomRaw = FileUtil.getResourceFileContent("template.pom");
            }
        }

        return StringUtils.defaultString(pomRaw);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getPath() {
        String path = request.getParameter("path");

        return StringUtils.defaultIfEmpty(path, "/");
    }

    private File getPomFile() {
        if (canShowDir()) {
            return new File(currentDir, "pom.xml");
        }

        return null;
    }
}
