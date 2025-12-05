/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://xlsql.jsdiff.com
   daichangya@163.com

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by the Free 
 Software Foundation; either version 2 of the License, or (at your option) 
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public 
 License along with this program; if not, write to the Free Software 
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package com.jsdiff.xlsql.engine.connection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.jsdiff.xlsql.database.AReader;
import com.jsdiff.xlsql.engine.resultset.xlNativeResultSet;

import static com.jsdiff.xlsql.engine.core.NativeSqlEngine.STATIC_TABLE_SCHEM;

/**
 * xlNativeDatabaseMetaData - 自研引擎的数据库元数据适配器
 * 
 * <p>为自研引擎提供基本的数据库元数据信息。
 * 由于自研引擎没有外部数据库连接，需要创建一个适配器来提供元数据。</p>
 * 
 * @author rzy
 */
class xlNativeDatabaseMetaData implements DatabaseMetaData {
    
    /** 数据存储读取器 */
    private final AReader datastore;
    
    /**
     * 创建xlNativeDatabaseMetaData实例
     * 
     * @param datastore 数据存储读取器
     */
    public xlNativeDatabaseMetaData(AReader datastore) {
        this.datastore = datastore;
    }
    
    @Override
    public String getDatabaseProductName() throws SQLException {
        return "XLSQL Native Engine";
    }
    
    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return "4.0";
    }
    
    @Override
    public String getDriverName() throws SQLException {
        return "XLSQL Native Driver";
    }
    
    @Override
    public String getDriverVersion() throws SQLException {
        return "4.0";
    }
    
    @Override
    public int getDriverMajorVersion() {
        return 4;
    }
    
    @Override
    public int getDriverMinorVersion() {
        return 0;
    }
    
    // 其他方法返回默认值或抛出异常
    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }
    
    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return true;
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        return true;
    }
    
    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }
    
    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return true;
    }
    
    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }
    
    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }
    
    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }
    
    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }
    
    @Override
    public String getExtraNameCharacters() throws SQLException {
        return "";
    }
    
    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 255;
    }
    
    @Override
    public java.sql.ResultSet getIndexInfo(String catalog, String schema, 
                                          String table, boolean unique, 
                                          boolean approximate) throws SQLException {
        // Excel没有索引概念，返回空结果集
        return createEmptyResultSet(new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "NON_UNIQUE", "INDEX_QUALIFIER", "INDEX_NAME", "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY", "PAGES", "FILTER_CONDITION"});
    }
    
    @Override
    public java.sql.ResultSet getImportedKeys(String catalog, String schema, 
                                             String table) throws SQLException {
        // Excel没有外键概念，返回空结果集
        return createEmptyResultSet(new String[]{"PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME", "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME", "DEFERRABILITY"});
    }
    
    @Override
    public java.sql.ResultSet getExportedKeys(String catalog, String schema, 
                                             String table) throws SQLException {
        // Excel没有外键概念，返回空结果集
        return createEmptyResultSet(new String[]{"PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME", "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME", "DEFERRABILITY"});
    }
    
    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }
    
    // 其他必需方法简化实现
    // DatabaseMetaData接口有很多方法，这里只实现关键方法
    // 其他方法可以返回默认值或抛出SQLException("Method not supported")
    
    // 以下方法返回默认值
    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }
    
    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return false;
    }
    
    @Override
    public java.sql.ResultSet getAttributes(String catalog, String schemaPattern, 
                                           String typeNamePattern, 
                                           String attributeNamePattern) throws SQLException {
        // Excel没有UDT属性概念，返回空结果集
        return createEmptyResultSet(new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "ATTR_NAME", "DATA_TYPE", "ATTR_TYPE_NAME", "ATTR_SIZE", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS", "ATTR_DEF", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATALOG", "SCOPE_SCHEMA", "SCOPE_TABLE"});
    }
    
    @Override
    public java.sql.ResultSet getBestRowIdentifier(String catalog, String schema, 
                                                  String table, int scope, 
                                                  boolean nullable) throws SQLException {
        // Excel没有行标识符概念，返回空结果集
        return createEmptyResultSet(new String[]{"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"});
    }
    
    @Override
    public String getCatalogSeparator() throws SQLException {
        return ".";
    }
    
    @Override
    public String getCatalogTerm() throws SQLException {
        return "catalog";
    }
    
    @Override
    public java.sql.ResultSet getCatalogs() throws SQLException {
        // Excel没有catalog概念，返回空结果集
        return createEmptyResultSet(new String[]{"TABLE_CAT"});
    }
    
    @Override
    public java.sql.ResultSet getColumnPrivileges(String catalog, String schema, 
                                                 String table, String columnNamePattern) 
            throws SQLException {
        // Excel没有权限概念，返回空结果集
        return createEmptyResultSet(new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "GRANTOR", "GRANTEE", "PRIVILEGE", "IS_GRANTABLE"});
    }
    
    @Override
    public java.sql.ResultSet getColumns(String catalog, String schemaPattern, 
                                        String tableNamePattern, 
                                        String columnNamePattern) throws SQLException {
        try {
            List<String[]> rows = new ArrayList<>();
            
            // 获取所有模式（工作簿）
            String[] schemas = datastore.getSchemas();
            
            // 遍历所有模式
            for (String schema : schemas) {
//                // 检查模式是否匹配
//                if (!matchesPattern(schema, schemaPattern)) {
//                    continue;
//                }
                
                try {
                    // 获取该模式下的所有表
                    String[] tables = datastore.getTables(schema);
                    
                    for (String table : tables) {
                        String tableName = schema + "_" + table;
                        // 检查表名是否匹配
                        if (!matchesPattern(tableName, tableNamePattern)) {
                            continue;
                        }
                        
                        try {
                            // 获取列信息
                            String[] columnNames = datastore.getColumnNames(schema, table);
                            String[] columnTypes = datastore.getColumnTypes(schema, table);
                            
                            for (int i = 0; i < columnNames.length; i++) {
                                String columnName = columnNames[i];
                                
                                // 检查列名是否匹配
                                if (!matchesPattern(columnName, columnNamePattern)) {
                                    continue;
                                }
                                
                                String sqlType = columnTypes != null && i < columnTypes.length ? columnTypes[i] : "VARCHAR";
                                
                                // 添加一行数据
                                // TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, 
                                // BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, 
                                // SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, 
                                // SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT, IS_GENERATEDCOLUMN
                                String[] row = new String[24];
                                row[0] = null; // TABLE_CAT
                                row[1] = STATIC_TABLE_SCHEM; // TABLE_SCHEM
                                row[2] = tableName; // TABLE_NAME
                                row[3] = columnName; // COLUMN_NAME
                                row[4] = String.valueOf(mapSqlType(sqlType)); // DATA_TYPE
                                row[5] = sqlType; // TYPE_NAME
                                row[6] = getColumnSize(sqlType); // COLUMN_SIZE
                                row[7] = null; // BUFFER_LENGTH
                                row[8] = null; // DECIMAL_DIGITS
                                row[9] = "10"; // NUM_PREC_RADIX
                                row[10] = String.valueOf(DatabaseMetaData.columnNullable); // NULLABLE
                                row[11] = null; // REMARKS
                                row[12] = null; // COLUMN_DEF
                                row[13] = null; // SQL_DATA_TYPE
                                row[14] = null; // SQL_DATETIME_SUB
                                row[15] = null; // CHAR_OCTET_LENGTH
                                row[16] = String.valueOf(i + 1); // ORDINAL_POSITION
                                row[17] = "YES"; // IS_NULLABLE
                                row[18] = null; // SCOPE_CATALOG
                                row[19] = null; // SCOPE_SCHEMA
                                row[20] = null; // SCOPE_TABLE
                                row[21] = null; // SOURCE_DATA_TYPE
                                row[22] = "NO"; // IS_AUTOINCREMENT
                                row[23] = "NO"; // IS_GENERATEDCOLUMN
                                rows.add(row);
                            }
                        } catch (IllegalArgumentException e) {
                            // 忽略不存在的表
                            continue;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // 忽略不存在的模式
                    continue;
                }
            }
            
            // 构建结果集
            return buildResultSet(
                new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", 
                             "BUFFER_LENGTH", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS", "COLUMN_DEF", 
                             "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", 
                             "SCOPE_CATALOG", "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE", "IS_AUTOINCREMENT", "IS_GENERATEDCOLUMN"},
                new String[]{"VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "INTEGER", "VARCHAR", "INTEGER", 
                             "INTEGER", "INTEGER", "INTEGER", "INTEGER", "VARCHAR", "VARCHAR", 
                             "INTEGER", "INTEGER", "INTEGER", "INTEGER", "VARCHAR", 
                             "VARCHAR", "VARCHAR", "VARCHAR", "INTEGER", "VARCHAR", "VARCHAR"},
                rows
            );
        } catch (Exception e) {
            throw new SQLException("Failed to get columns: " + e.getMessage(), e);
        }
    }
    
    @Override
    public java.sql.Connection getConnection() throws SQLException {
        // 返回null，因为自研引擎没有外部连接
        return null;
    }
    
    @Override
    public java.sql.ResultSet getCrossReference(String parentCatalog, String parentSchema, 
                                               String parentTable, String foreignCatalog, 
                                               String foreignSchema, 
                                               String foreignTable) throws SQLException {
        // Excel没有外键概念，返回空结果集
        return createEmptyResultSet(new String[]{"PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME", "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME", "DEFERRABILITY"});
    }
    
    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }
    
    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return 4;
    }
    
    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return 0;
    }
    
    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 4;
    }
    
    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 255;
    }
    
    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxConnections() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxIndexLength() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxRowSize() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 255;
    }
    
    @Override
    public int getMaxStatementLength() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxStatements() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxTableNameLength() throws SQLException {
        return 255;
    }
    
    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }
    
    @Override
    public int getMaxUserNameLength() throws SQLException {
        return 255;
    }
    
    @Override
    public String getNumericFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public java.sql.ResultSet getPrimaryKeys(String catalog, String schema, 
                                            String table) throws SQLException {
        // Excel没有主键概念，返回空结果集
        return createEmptyResultSet(new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "KEY_SEQ", "PK_NAME"});
    }
    
    @Override
    public java.sql.ResultSet getProcedureColumns(String catalog, String schemaPattern, 
                                                 String procedureNamePattern, 
                                                 String columnNamePattern) throws SQLException {
        // Excel没有存储过程概念，返回空结果集
        return createEmptyResultSet(new String[]{"PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "COLUMN_NAME", "COLUMN_TYPE", "DATA_TYPE", "TYPE_NAME", "PRECISION", "LENGTH", "SCALE", "RADIX", "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SPECIFIC_NAME"});
    }
    
    @Override
    public String getProcedureTerm() throws SQLException {
        return "procedure";
    }
    
    @Override
    public java.sql.ResultSet getProcedures(String catalog, String schemaPattern, 
                                           String procedureNamePattern) throws SQLException {
        // Excel没有存储过程概念，返回空结果集
        return createEmptyResultSet(new String[]{"PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "REMARKS", "PROCEDURE_TYPE", "SPECIFIC_NAME"});
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }
    
    @Override
    public java.sql.ResultSet getSchemas() throws SQLException {
        return getSchemas(null, null);
    }
    
    @Override
    public String getSchemaTerm() throws SQLException {
        return "schema";
    }
    
    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }
    
    @Override
    public String getSQLKeywords() throws SQLException {
        return "";
    }
    
    @Override
    public int getSQLStateType() throws SQLException {
        return DatabaseMetaData.sqlStateSQL;
    }
    
    @Override
    public String getStringFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public java.sql.ResultSet getSuperTables(String catalog, String schemaPattern, 
                                            String tableNamePattern) throws SQLException {
        // Excel没有超表概念，返回空结果集
        return createEmptyResultSet(new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "SUPERTABLE_NAME"});
    }
    
    @Override
    public java.sql.ResultSet getSuperTypes(String catalog, String schemaPattern, 
                                          String typeNamePattern) throws SQLException {
        // Excel没有超类型概念，返回空结果集
        return createEmptyResultSet(new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SUPERTYPE_CAT", "SUPERTYPE_SCHEM", "SUPERTYPE_NAME"});
    }
    
    @Override
    public String getSystemFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public java.sql.ResultSet getTablePrivileges(String catalog, String schemaPattern, 
                                                String tableNamePattern) throws SQLException {
        // Excel没有权限概念，返回空结果集
        return createEmptyResultSet(new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "GRANTOR", "GRANTEE", "PRIVILEGE", "IS_GRANTABLE"});
    }
    
    @Override
    public java.sql.ResultSet getTables(String catalog, String schemaPattern, 
                                       String tableNamePattern, 
                                       String[] types) throws SQLException {
        try {
            List<String[]> rows = new ArrayList<>();
            
            // 获取所有模式（工作簿）
            String[] schemas = datastore.getSchemas();

            // 检查是否接受TABLE类型
            boolean acceptTable = (types == null || types.length == 0);
            if (types != null) {
                for (String type : types) {
                    if ("TABLE".equalsIgnoreCase(type)) {
                        acceptTable = true;
                        break;
                    }
                }
            }
            
            if (!acceptTable) {
                // 如果不接受TABLE类型，返回空结果集
                return createEmptyResultSet(new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS", "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION"});
            }
            
            // 遍历所有模式
            for (String schema : schemas) {
//                // 检查模式是否匹配
//                if (!matchesPattern(schema, schemaPattern)) {
//                    continue;
//                }
                
                try {
                    // 获取该模式下的所有表
                    String[] tables = datastore.getTables(schema);
                    
                    for (String table : tables) {
                        String tablename = schema + "_" + table;
                        // 检查表名是否匹配
                        if (!matchesPattern(table, tableNamePattern)) {
                            continue;
                        }

                        // 添加一行数据
                        // TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_CAT, TYPE_SCHEM, TYPE_NAME, SELF_REFERENCING_COL_NAME, REF_GENERATION
                        String[] row = new String[10];
                        row[0] = null; // TABLE_CAT
                        row[1] = STATIC_TABLE_SCHEM; // TABLE_SCHEM
                        row[2] = tablename; // TABLE_NAME
                        row[3] = "TABLE"; // TABLE_TYPE
                        row[4] = null; // REMARKS
                        row[5] = null; // TYPE_CAT
                        row[6] = null; // TYPE_SCHEM
                        row[7] = null; // TYPE_NAME
                        row[8] = null; // SELF_REFERENCING_COL_NAME
                        row[9] = null; // REF_GENERATION
                        rows.add(row);
                    }
                } catch (IllegalArgumentException e) {
                    // 忽略不存在的模式
                    continue;
                }
            }
            
            // 构建结果集
            return buildResultSet(
                new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS", "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION"},
                new String[]{"VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR"},
                rows
            );
        } catch (Exception e) {
            throw new SQLException("Failed to get tables: " + e.getMessage(), e);
        }
    }
    
    @Override
    public java.sql.ResultSet getTableTypes() throws SQLException {
        try {
            List<String[]> rows = new ArrayList<>();
            
            // Excel只支持TABLE类型
            String[] row = new String[1];
            row[0] = "TABLE"; // TABLE_TYPE
            rows.add(row);
            
            // 构建结果集
            return buildResultSet(
                new String[]{"TABLE_TYPE"},
                new String[]{"VARCHAR"},
                rows
            );
        } catch (Exception e) {
            throw new SQLException("Failed to get table types: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public java.sql.ResultSet getTypeInfo() throws SQLException {
        try {
            List<String[]> rows = new ArrayList<>();
            
            // 定义支持的数据类型
            // TYPE_NAME, DATA_TYPE, PRECISION, LITERAL_PREFIX, LITERAL_SUFFIX, CREATE_PARAMS, NULLABLE, 
            // CASE_SENSITIVE, SEARCHABLE, UNSIGNED_ATTRIBUTE, FIXED_PREC_SCALE, AUTO_INCREMENT, LOCAL_TYPE_NAME, 
            // MINIMUM_SCALE, MAXIMUM_SCALE, SQL_DATA_TYPE, SQL_DATETIME_SUB, NUM_PREC_RADIX
            
            // VARCHAR
            addTypeInfoRow(rows, "VARCHAR", Types.VARCHAR, 65535, true, true, true);
            // INTEGER
            addTypeInfoRow(rows, "INTEGER", Types.INTEGER, 10, false, true, true);
            // DOUBLE
            addTypeInfoRow(rows, "DOUBLE", Types.DOUBLE, 15, false, true, true);
            // DATE
            addTypeInfoRow(rows, "DATE", Types.DATE, 10, false, true, true);
            // TIMESTAMP
            addTypeInfoRow(rows, "TIMESTAMP", Types.TIMESTAMP, 23, false, true, true);
            // BOOLEAN
            addTypeInfoRow(rows, "BOOLEAN", Types.BOOLEAN, 1, false, true, true);
            
            // 构建结果集
            return buildResultSet(
                new String[]{"TYPE_NAME", "DATA_TYPE", "PRECISION", "LITERAL_PREFIX", "LITERAL_SUFFIX", "CREATE_PARAMS", "NULLABLE", 
                             "CASE_SENSITIVE", "SEARCHABLE", "UNSIGNED_ATTRIBUTE", "FIXED_PREC_SCALE", "AUTO_INCREMENT", "LOCAL_TYPE_NAME", 
                             "MINIMUM_SCALE", "MAXIMUM_SCALE", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "NUM_PREC_RADIX"},
                new String[]{"VARCHAR", "INTEGER", "INTEGER", "VARCHAR", "VARCHAR", "VARCHAR", "INTEGER", 
                             "BOOLEAN", "INTEGER", "BOOLEAN", "BOOLEAN", "BOOLEAN", "VARCHAR", 
                             "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER"},
                rows
            );
        } catch (Exception e) {
            throw new SQLException("Failed to get type info: " + e.getMessage(), e);
        }
    }
    
    @Override
    public java.sql.ResultSet getUDTs(String catalog, String schemaPattern, 
                                     String typeNamePattern, 
                                     int[] types) throws SQLException {
        // Excel没有UDT概念，返回空结果集
        return createEmptyResultSet(new String[]{"TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "CLASS_NAME", "DATA_TYPE", "REMARKS", "BASE_TYPE"});
    }
    
    @Override
    public String getURL() throws SQLException {
        return "jdbc:xlsql:excel:native";
    }
    
    @Override
    public String getUserName() throws SQLException {
        return "";
    }
    
    @Override
    public java.sql.ResultSet getVersionColumns(String catalog, String schema, 
                                               String table) throws SQLException {
        // Excel没有版本列概念，返回空结果集
        return createEmptyResultSet(new String[]{"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"});
    }
    
    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }
    
    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return false;
    }
    
    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }
    
    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }
    
    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return true;
    }
    
    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }
    
    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }
    
    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsConvert() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsGroupBy() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return holdability == ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }
    
    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        return type == ResultSet.TYPE_FORWARD_ONLY;
    }
    
    @Override
    public boolean supportsSavepoints() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return true;
    }
    
    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return level == Connection.TRANSACTION_NONE;
    }
    
    @Override
    public boolean supportsTransactions() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsUnion() throws SQLException {
        return false;
    }
    
    @Override
    public boolean supportsUnionAll() throws SQLException {
        return false;
    }
    
    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }
    
    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }
    
    @Override
    public boolean usesLocalFiles() throws SQLException {
        return false;
    }
    
    // JDBC 4.0+ 方法
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public java.sql.RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }
    
    @Override
    public java.sql.ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        try {
            List<String[]> rows = new ArrayList<>();
            
//            // 获取所有模式（工作簿）
//            String[] schemas = datastore.getSchemas();
//
//            for (String schema : schemas) {
////                // 检查模式是否匹配
////                if (!matchesPattern(schema, schemaPattern)) {
////                    continue;
////                }
//
//            }
            // 添加一行数据
            // TABLE_SCHEM, TABLE_CATALOG
            String[] row = new String[2];
            row[0] = STATIC_TABLE_SCHEM; // TABLE_SCHEM
            row[1] = null; // TABLE_CATALOG
            rows.add(row);

            // 构建结果集
            return buildResultSet(
                new String[]{"TABLE_SCHEM", "TABLE_CATALOG"},
                new String[]{"VARCHAR", "VARCHAR"},
                rows
            );
        } catch (Exception e) {
            throw new SQLException("Failed to get schemas: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return false;
    }
    
    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }
    
    @Override
    public java.sql.ResultSet getClientInfoProperties() throws SQLException {
        // 返回空结果集
        return createEmptyResultSet(new String[]{"NAME", "MAX_LEN", "DEFAULT_VALUE", "DESCRIPTION"});
    }
    
    @Override
    public java.sql.ResultSet getFunctions(String catalog, String schemaPattern, 
                                         String functionNamePattern) throws SQLException {
        // Excel没有用户定义函数概念，返回空结果集
        return createEmptyResultSet(new String[]{"FUNCTION_CAT", "FUNCTION_SCHEM", "FUNCTION_NAME", "REMARKS", "FUNCTION_TYPE", "SPECIFIC_NAME"});
    }
    
    @Override
    public java.sql.ResultSet getFunctionColumns(String catalog, String schemaPattern, 
                                               String functionNamePattern, 
                                               String columnNamePattern) throws SQLException {
        // Excel没有用户定义函数概念，返回空结果集
        return createEmptyResultSet(new String[]{"FUNCTION_CAT", "FUNCTION_SCHEM", "FUNCTION_NAME", "COLUMN_NAME", "COLUMN_TYPE", "DATA_TYPE", "TYPE_NAME", "PRECISION", "LENGTH", "SCALE", "RADIX", "NULLABLE", "REMARKS", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SPECIFIC_NAME"});
    }
    
    @Override
    public java.sql.ResultSet getPseudoColumns(String catalog, String schemaPattern, 
                                             String tableNamePattern, 
                                             String columnNamePattern) throws SQLException {
        // Excel没有伪列概念，返回空结果集
        return createEmptyResultSet(new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "COLUMN_SIZE", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "COLUMN_USAGE", "REMARKS", "CHAR_OCTET_LENGTH", "IS_NULLABLE"});
    }
    
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return false;
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 模式匹配（支持%和_通配符）
     * 
     * @param value 要匹配的值
     * @param pattern 模式（null表示匹配所有）
     * @return 如果匹配返回true
     */
    private boolean matchesPattern(String value, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (value == null) {
            return false;
        }
        
        // 将SQL LIKE模式转换为正则表达式
        String regex = pattern.replace("%", ".*").replace("_", ".");
        return value.matches(regex);
    }
    
    /**
     * 构建结果集
     * 
     * @param columnNames 列名数组
     * @param columnTypes 列类型数组
     * @param rows 数据行列表
     * @return ResultSet对象
     */
    private ResultSet buildResultSet(String[] columnNames, String[] columnTypes, List<String[]> rows) {
        if (rows.isEmpty()) {
            return createEmptyResultSet(columnNames);
        }
        
        // 转换为列优先的数据矩阵
        int colCount = columnNames.length;
        int rowCount = rows.size();
        String[][] values = new String[colCount][rowCount];
        
        for (int i = 0; i < rowCount; i++) {
            String[] row = rows.get(i);
            for (int j = 0; j < colCount && j < row.length; j++) {
                values[j][i] = row[j];
            }
        }
        
        return new xlNativeResultSet(columnNames, columnTypes, values, rowCount);
    }
    
    /**
     * 创建空结果集
     * 
     * @param columnNames 列名数组
     * @return 空结果集
     */
    private ResultSet createEmptyResultSet(String[] columnNames) {
        String[] columnTypes = new String[columnNames.length];
        for (int i = 0; i < columnTypes.length; i++) {
            columnTypes[i] = "VARCHAR";
        }
        return new xlNativeResultSet(columnNames, columnTypes, new String[columnNames.length][0], 0);
    }
    
    /**
     * 映射SQL类型名称到JDBC类型代码
     * 
     * @param sqlType SQL类型名称
     * @return JDBC类型代码
     */
    private int mapSqlType(String sqlType) {
        if (sqlType == null) {
            return Types.VARCHAR;
        }
        
        String upperType = sqlType.toUpperCase();
        if (upperType.contains("INT")) {
            return Types.INTEGER;
        } else if (upperType.contains("DOUBLE") || upperType.contains("FLOAT") || upperType.contains("DECIMAL") || upperType.contains("NUMERIC")) {
            return Types.DOUBLE;
        } else if (upperType.contains("DATE") && !upperType.contains("TIME")) {
            return Types.DATE;
        } else if (upperType.contains("TIMESTAMP") || upperType.contains("DATETIME")) {
            return Types.TIMESTAMP;
        } else if (upperType.contains("BOOLEAN") || upperType.contains("BIT")) {
            return Types.BOOLEAN;
        } else {
            return Types.VARCHAR;
        }
    }
    
    /**
     * 获取列大小
     * 
     * @param sqlType SQL类型名称
     * @return 列大小
     */
    private String getColumnSize(String sqlType) {
        if (sqlType == null) {
            return "65535";
        }
        
        String upperType = sqlType.toUpperCase();
        if (upperType.contains("INT")) {
            return "10";
        } else if (upperType.contains("DOUBLE") || upperType.contains("FLOAT") || upperType.contains("DECIMAL") || upperType.contains("NUMERIC")) {
            return "15";
        } else if (upperType.contains("DATE")) {
            return "10";
        } else if (upperType.contains("TIMESTAMP") || upperType.contains("DATETIME")) {
            return "23";
        } else if (upperType.contains("BOOLEAN") || upperType.contains("BIT")) {
            return "1";
        } else {
            return "65535";
        }
    }
    
    /**
     * 添加类型信息行
     * 
     * @param rows 行列表
     * @param typeName 类型名称
     * @param dataType JDBC类型代码
     * @param precision 精度
     * @param nullable 是否可为空
     * @param caseSensitive 是否大小写敏感
     * @param searchable 是否可搜索
     */
    private void addTypeInfoRow(List<String[]> rows, String typeName, int dataType, int precision, 
                                boolean nullable, boolean caseSensitive, boolean searchable) {
        String[] row = new String[18];
        row[0] = typeName; // TYPE_NAME
        row[1] = String.valueOf(dataType); // DATA_TYPE
        row[2] = String.valueOf(precision); // PRECISION
        row[3] = null; // LITERAL_PREFIX
        row[4] = null; // LITERAL_SUFFIX
        row[5] = null; // CREATE_PARAMS
        row[6] = String.valueOf(nullable ? DatabaseMetaData.typeNullable : DatabaseMetaData.typeNoNulls); // NULLABLE
        row[7] = String.valueOf(caseSensitive); // CASE_SENSITIVE
        row[8] = String.valueOf(searchable ? DatabaseMetaData.typeSearchable : DatabaseMetaData.typePredNone); // SEARCHABLE
        row[9] = "false"; // UNSIGNED_ATTRIBUTE
        row[10] = "false"; // FIXED_PREC_SCALE
        row[11] = "false"; // AUTO_INCREMENT
        row[12] = null; // LOCAL_TYPE_NAME
        row[13] = "0"; // MINIMUM_SCALE
        row[14] = "0"; // MAXIMUM_SCALE
        row[15] = String.valueOf(dataType); // SQL_DATA_TYPE
        row[16] = "0"; // SQL_DATETIME_SUB
        row[17] = "10"; // NUM_PREC_RADIX
        rows.add(row);
    }
}

