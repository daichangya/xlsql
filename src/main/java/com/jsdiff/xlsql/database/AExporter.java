/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
   daichangya@163.com

 This program is free software; you can redistribute it and/or modify it under 
 the terms of the GNU General Public License as published by the Free Software 
 Foundation; either version 2 of the License, or (at your option) any later 
 version.

 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software Foundation, 
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package com.jsdiff.xlsql.database;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.jsdiff.xlsql.database.export.ASqlFormatter;
import com.jsdiff.xlsql.database.export.IExportHandler;
import com.jsdiff.xlsql.database.export.XmlFormatter;
import com.jsdiff.xlsql.database.export.xlExportHandlerFactory;
import com.jsdiff.xlsql.database.export.xlHsqldbFormatter;


/**
 * AExporter - 导出器抽象基类
 * 
 * <p>该类继承自AReader，提供了将数据导出为不同格式的功能。
 * 支持导出为HSQLDB SQL和XML格式。</p>
 * 
 * @author daichangya
 */
public abstract class AExporter extends AReader {

    /** 导出格式：HSQLDB SQL */
    public static final int HSQLDB = 1;
    /** 导出格式：XML */
    public static final int XML = 2;
    
    /** 数据读取器 */
    private AReader reader;
    /** SQL格式化器 */
    private ASqlFormatter formatter;
    /** 导出处理器 */
    private IExportHandler handler;
    /** 导出的SQL语句列表 */
    private List lines = new ArrayList();
    
    /**
     * Creates a new Exporter object.
     * 
     * @param dir ( root ) directory where datasource is stored
     * 
     * @throws xlDatabaseException when a database error occurs
     */
    public AExporter(File dir) throws xlDatabaseException {
        super(dir);
    }

    /**
     * 导出所有数据到打印流
     * 
     * @param format 导出格式（HSQLDB或XML）
     * @param ps 输出打印流
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(int format, PrintStream ps) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create();
        export();
        handler.close();
    }
    
    /**
     * 导出指定模式（工作簿）的数据到打印流
     * 
     * @param schema 模式名称（对应Excel文件名）
     * @param format 导出格式（HSQLDB或XML）
     * @param ps 输出打印流
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(String schema, int format, PrintStream ps) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create();
        exportSchema(schema);
        handler.close();
    }
    
    /**
     * 导出指定表（工作表）的数据到打印流
     * 
     * @param schema 模式名称（对应Excel文件名）
     * @param table 表名称（对应Excel工作表名）
     * @param format 导出格式（HSQLDB或XML）
     * @param ps 输出打印流
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(String schema, String table, int format, PrintStream ps) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create();
        exportTable(schema, table);
        handler.close();
    }

    /**
     * 导出所有数据到文件
     * 
     * @param format 导出格式（HSQLDB或XML）
     * @param exportfile 导出文件对象
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(int format, File exportfile) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(exportfile);
        export();
        handler.close();
    }

    /**
     * 导出指定模式（工作簿）的数据到文件
     * 
     * @param schema 模式名称（对应Excel文件名）
     * @param format 导出格式（HSQLDB或XML）
     * @param exportfile 导出文件对象
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(String schema, int format, File exportfile) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(exportfile);
        exportSchema(schema);
        handler.close();
    }

    /**
     * 导出指定表（工作表）的数据到文件
     * 
     * @param schema 模式名称（对应Excel文件名）
     * @param table 表名称（对应Excel工作表名）
     * @param format 导出格式（HSQLDB或XML）
     * @param exportfile 导出文件对象
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(String schema, String table, int format, File exportfile)
                throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(exportfile);
        exportTable(schema, table);
        handler.close();
    }
    
    /**
     * 导出所有数据到JDBC连接
     * 
     * @param format 导出格式（HSQLDB或XML）
     * @param jdbc JDBC连接对象
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(int format, java.sql.Connection jdbc) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(jdbc);
        export();
        handler.close();
    }
    
    /**
     * 导出指定模式（工作簿）的数据到JDBC连接
     * 
     * @param schema 模式名称（对应Excel文件名）
     * @param format 导出格式（HSQLDB或XML）
     * @param jdbc JDBC连接对象
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(String schema, int format, java.sql.Connection jdbc) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(jdbc);
        exportSchema(schema);
        handler.close();
    }
    
    /**
     * 导出指定表（工作表）的数据到JDBC连接
     * 
     * @param schema 模式名称（对应Excel文件名）
     * @param table 表名称（对应Excel工作表名）
     * @param format 导出格式（HSQLDB或XML）
     * @param jdbc JDBC连接对象
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    public void export(String schema, String table, int format, java.sql.Connection jdbc) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(jdbc);
        exportTable(schema, table);
        handler.close();
    }
    
    /**
     * 根据格式创建SQL格式化器
     * 
     * @param format 导出格式（HSQLDB或XML）
     */
    private void createFormatter(int format) {
        switch (format) {
            case HSQLDB:
                this.formatter = new xlHsqldbFormatter();
                break;
            case XML:
                this.formatter = new XmlFormatter();
                break;
            default:
                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }

    /**
     * 导出所有模式（工作簿）的数据
     * 
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    private void export() throws xlDatabaseException {
        String[] schemas = reader.getSchemas();
        // 遍历所有模式并导出
        for (int i=0; i < schemas.length; i++) {
            exportSchema(schemas[i]);
        }
    }
    
    /**
     * 导出指定模式（工作簿）的数据
     * 
     * <p>生成CREATE SCHEMA语句，然后导出该模式下的所有表。</p>
     * 
     * @param schema 模式名称（对应Excel文件名）
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    private void exportSchema(String schema) throws xlDatabaseException {
        assert (formatter != null);
        
        // 生成CREATE SCHEMA语句
        lines.add(formatter.wCreateSchema(schema));
        String[] tables = reader.getTables(schema);
        // 导出该模式下的所有表
        for (int i=0; i < tables.length; i++) {
            exportTable(schema, tables[i]);
        }
    }
    
    /**
     * 导出指定表（工作表）的数据
     * 
     * <p>生成DROP TABLE、CREATE TABLE和INSERT语句。</p>
     * 
     * @param schema 模式名称（对应Excel文件名）
     * @param table 表名称（对应Excel工作表名）
     * @throws xlDatabaseException 如果导出失败则抛出异常
     */
    private void exportTable(String schema, String table) throws xlDatabaseException { 
        // 获取列名和列类型
        String[] columns = reader.getColumnNames(schema, table);
        String[] types = reader.getColumnTypes(schema, table);

        // 生成DROP TABLE语句（如果格式化器支持）
        if (formatter.wDropTable(schema, table) != null) {
            lines.add(formatter.wDropTable(schema, table));
        }

        // 生成CREATE TABLE语句
        lines.add(formatter.wCreateTable(schema, table, columns, types));

        // 获取数据行数和列数
        int R = reader.getRows(schema, table);
        int C = columns.length;
        String[][] matrix = reader.getValues(schema, table);
        String[] va = new String[C];

        // 生成INSERT语句
        if ((columns != null) && (types != null)) {
            for (int k = 0; k < R; k++) {
                // 提取一行数据
                for (int m = 0; m < C; m++) {
                    va[m] = matrix[m][k];
                }
                // 生成INSERT语句
                lines.add(formatter.wInsert(schema, table, columns, types, va));
            }
            // 添加结束标记
            lines.add(formatter.wLast());
        }
        // 写入导出处理器并清空列表
        handler.write(lines);
        lines.clear();
    }
}