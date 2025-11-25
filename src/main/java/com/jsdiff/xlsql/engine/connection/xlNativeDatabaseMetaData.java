/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
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

/**
 * xlNativeDatabaseMetaData - 自研引擎的数据库元数据适配器
 * 
 * <p>为自研引擎提供基本的数据库元数据信息。
 * 由于自研引擎没有外部数据库连接，需要创建一个适配器来提供元数据。</p>
 * 
 * @author daichangya
 */
class xlNativeDatabaseMetaData implements DatabaseMetaData {
    
    @Override
    public String getDatabaseProductName() throws SQLException {
        return "xlSQL Native Engine";
    }
    
    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return "4.0";
    }
    
    @Override
    public String getDriverName() throws SQLException {
        return "xlSQL Native Driver";
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
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getImportedKeys(String catalog, String schema, 
                                             String table) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getExportedKeys(String catalog, String schema, 
                                             String table) throws SQLException {
        throw new SQLException("Method not supported");
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
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getBestRowIdentifier(String catalog, String schema, 
                                                  String table, int scope, 
                                                  boolean nullable) throws SQLException {
        throw new SQLException("Method not supported");
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
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getColumnPrivileges(String catalog, String schema, 
                                                 String table, String columnNamePattern) 
            throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getColumns(String catalog, String schemaPattern, 
                                        String tableNamePattern, 
                                        String columnNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.Connection getConnection() throws SQLException {
        return null;
    }
    
    @Override
    public java.sql.ResultSet getCrossReference(String parentCatalog, String parentSchema, 
                                               String parentTable, String foreignCatalog, 
                                               String foreignSchema, 
                                               String foreignTable) throws SQLException {
        throw new SQLException("Method not supported");
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
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getProcedureColumns(String catalog, String schemaPattern, 
                                                 String procedureNamePattern, 
                                                 String columnNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getProcedureTerm() throws SQLException {
        return "procedure";
    }
    
    @Override
    public java.sql.ResultSet getProcedures(String catalog, String schemaPattern, 
                                           String procedureNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }
    
    @Override
    public java.sql.ResultSet getSchemas() throws SQLException {
        throw new SQLException("Method not supported");
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
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getSuperTypes(String catalog, String schemaPattern, 
                                          String typeNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getSystemFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public java.sql.ResultSet getTablePrivileges(String catalog, String schemaPattern, 
                                                String tableNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getTables(String catalog, String schemaPattern, 
                                       String tableNamePattern, 
                                       String[] types) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getTableTypes() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "";
    }
    
    @Override
    public java.sql.ResultSet getTypeInfo() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getUDTs(String catalog, String schemaPattern, 
                                     String typeNamePattern, 
                                     int[] types) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getURL() throws SQLException {
        return "jdbc:jsdiff:excel:native";
    }
    
    @Override
    public String getUserName() throws SQLException {
        return "";
    }
    
    @Override
    public java.sql.ResultSet getVersionColumns(String catalog, String schema, 
                                               String table) throws SQLException {
        throw new SQLException("Method not supported");
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
        throw new SQLException("Method not supported");
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
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getFunctions(String catalog, String schemaPattern, 
                                         String functionNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getFunctionColumns(String catalog, String schemaPattern, 
                                               String functionNamePattern, 
                                               String columnNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public java.sql.ResultSet getPseudoColumns(String catalog, String schemaPattern, 
                                             String tableNamePattern, 
                                             String columnNamePattern) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return false;
    }
}

