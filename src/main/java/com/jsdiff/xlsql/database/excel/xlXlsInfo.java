package com.jsdiff.xlsql.database.excel;

import java.io.File;

/**
 * xlXlsInfo - Excel文件信息类
 * 
 * <p>该类封装了Excel文件的基本信息，包括文件路径、完整文件名和文件名（不含扩展名）。</p>
 * 
 * @author daichangya
 */
public class xlXlsInfo {

    /** 文件所在目录路径 */
    private String path;

    /** 完整文件名（含扩展名） */
    private String fileName;

    /** 文件名（不含扩展名） */
    private String name;

    /**
     * 创建Excel文件信息对象
     * 
     * @param path 文件所在目录路径
     * @param fileName 完整文件名（含扩展名）
     * @param name 文件名（不含扩展名）
     */
    public xlXlsInfo(String path, String fileName, String name) {
        this.path = path;
        this.fileName = fileName;
        this.name = name;
    }

    /**
     * 获取文件所在目录路径
     * 
     * @return 目录路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置文件所在目录路径
     * 
     * @param path 目录路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取完整文件名（含扩展名）
     * 
     * @return 完整文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置完整文件名（含扩展名）
     * 
     * @param fileName 完整文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件名（不含扩展名）
     * 
     * @return 文件名（不含扩展名）
     */
    public String getName() {
        return name;
    }

    /**
     * 设置文件名（不含扩展名）
     * 
     * @param name 文件名（不含扩展名）
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 从文件对象创建xlXlsInfo对象
     * 
     * <p>从文件对象中提取路径、完整文件名和文件名（不含扩展名）。</p>
     * 
     * @param file Excel文件对象
     * @return xlXlsInfo对象
     */
    public static xlXlsInfo createXlXlsInfo(File file) {
        String fileName = file.getName();
        // 提取文件名（不含扩展名）：从开头到最后一个点之前
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        return new xlXlsInfo(file.getParent(), fileName, name);
    }
}
