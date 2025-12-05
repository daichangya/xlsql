package io.github.daichangya.xlsql.database;

import java.io.File;

/**
 * FileType - 文件类型枚举
 * 
 * <p>定义了xlSQL支持的文件类型，当前支持Excel 97-2003（.xls）和Excel 2007+（.xlsx）格式。</p>
 * 
 * @author daichangya
 */
public enum FileType {
    /** Excel 97-2003格式 */
    XLS("xls","Excel 97-2003"),
    /** Excel 2007+格式 */
    XLSX("xlsx","Excel 2007+");

    /** 文件类型描述信息 */
    private String desc;

    /** 文件类型枚举值（文件扩展名） */
    private String value;

    /**
     * 构造函数
     * 
     * @param value 文件扩展名
     * @param desc 文件类型描述
     */
    FileType(String value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 获取文件扩展名
     * 
     * @return 文件扩展名（如"xls"或"xlsx"）
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据扩展名获取文件类型
     * 
     * @param value 文件扩展名
     * @return 对应的FileType枚举值，如果不存在则返回null
     */
    public static FileType getByValue(String value) {
        for (FileType type : FileType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据文件对象获取文件类型
     * 
     * <p>从文件名中提取扩展名，然后查找对应的文件类型。</p>
     * 
     * @param file 文件对象
     * @return 对应的FileType枚举值，如果无法识别则返回null
     */
    public static FileType getFileType(File file) {
        // 从文件名中提取扩展名（最后一个点之后的部分）
        return FileType.getByValue(file.getName().substring(file.getName().lastIndexOf(".") + 1));
    }
}
