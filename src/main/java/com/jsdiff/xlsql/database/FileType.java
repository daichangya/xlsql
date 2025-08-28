package com.jsdiff.xlsql.database;

import java.io.File;

public enum FileType {
    XLS("xls","Excel 97-2003"),
    XLSX("xlsx","Excel 2007+");

    /**
     * 文件类型描述信息
     */
    private String desc;

    /**
     * 文件类型枚举值
     */
    private String value;

    FileType(String value,String desc) {
        this.value = value;
        this.desc = desc;
    }


    public String getValue() {
        return value;
    }

    public static FileType getByValue(String value) {
        for (FileType type : FileType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    public static FileType getFileType(File file) {
        return FileType.getByValue(file.getName().substring(file.getName().lastIndexOf(".") + 1));
    }
}
