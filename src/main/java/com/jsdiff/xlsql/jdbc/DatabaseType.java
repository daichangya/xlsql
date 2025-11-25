package com.jsdiff.xlsql.jdbc;

/**
 * 数据库类型枚举
 * 
 * <p>定义了xlSQL支持的数据库类型，包括MySQL、H2、HSQLDB和自研NATIVE引擎。</p>
 */
public enum DatabaseType {
    /** MySQL数据库类型 */
    MYSQL("mysql","MySQL"),
    
    /** H2数据库类型 */
    H2("h2","H2"),
    
    /** HSQLDB数据库类型 */
    HSQLDB("hsqldb","HSQL Database Engine"),
    
    /** 自研SQL引擎（不依赖外部数据库） */
    NATIVE("native","xlSQL Native Engine");

    private final String name;

    private final String engineName;
    
    /**
     * 构造数据库类型枚举
     * 
     * @param engineName 数据库引擎名称
     */
    DatabaseType(String name,String engineName) {
        this.name = name;
        this.engineName = engineName;
    }
    
    /**
     * 根据数据库引擎名称获取对应的数据库类型
     * 
     * @param engineName 数据库引擎名称
     * @return 对应的数据库类型枚举值，如果未匹配则默认返回H2
     */
    public static DatabaseType fromEngineName(String engineName) {
        if (engineName == null) {
            return H2;
        }
        
        // 特殊处理：如果明确指定为native，直接返回NATIVE
        if (engineName.equalsIgnoreCase("native") || engineName.contains("Native")) {
            return NATIVE;
        }
        
        for (DatabaseType type : values()) {
            if (type == NATIVE) continue; // 跳过NATIVE，已在上面处理
            if (engineName.contains(type.engineName) || engineName.contains(type.name)) {
                return type;
            }
        }
        return H2;
    }

    public String getName() {
        return name;
    }
}