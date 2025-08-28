package com.jsdiff.xlsql.database.excel;

import java.io.File;

public class xlXlsInfo {

    private String path;

    private String fileName;

    private String name;

    public xlXlsInfo(String path, String fileName, String name) {
        this.path = path;
        this.fileName = fileName;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static xlXlsInfo createXlXlsInfo(File file) {
        String fileName = file.getName();
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        return new xlXlsInfo(file.getParent(), fileName, name);
    }
}
