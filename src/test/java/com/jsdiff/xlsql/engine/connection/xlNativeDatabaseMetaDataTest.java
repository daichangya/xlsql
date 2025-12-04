///*jsdiff.com
//
// Copyright (C) 2025 jsdiff
//   jsdiff Information Sciences
//   http://xlsql.jsdiff.com
//   daichangya@163.com
//
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 2 of the License, or (at your option)
// any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// more details. You should have received a copy of the GNU General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//*/
//package com.jsdiff.xlsql.engine.connection;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.io.File;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import com.jsdiff.xlsql.database.ADatabase;
//import com.jsdiff.xlsql.database.AFile;
//import com.jsdiff.xlsql.database.ASubFolder;
//import com.jsdiff.xlsql.database.xlDatabaseException;
//
///**
// * xlNativeDatabaseMetaDataTest - xlNativeDatabaseMetaData单元测试
// *
// * <p>测试xlNativeDatabaseMetaData类的所有实现方法，包括：
// * - 基本属性方法
// * - getTables()方法
// * - getColumns()方法
// * - getSchemas()方法
// * - getTableTypes()方法
// * - getTypeInfo()方法
// * - 其他ResultSet方法</p>
// *
// * @author rzy
// */
//public class xlNativeDatabaseMetaDataTest {
//
//    private xlNativeDatabaseMetaData metaData;
//    private MockDatabase mockDatabase;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        mockDatabase = new MockDatabase();
//        metaData = new xlNativeDatabaseMetaData(mockDatabase);
//
//        // 设置测试数据
//        setupTestData();
//    }
//
//    /**
//     * 设置测试数据
//     */
//    private void setupTestData() {
//        // 工作簿1: test1
//        //   - Sheet1: id, name, age, salary
//        String[] columns1 = {"id", "name", "age", "salary"};
//        String[] types1 = {"INTEGER", "VARCHAR", "INTEGER", "DOUBLE"};
//        mockDatabase.addTable("test1", "Sheet1", columns1, types1);
//
//        // 工作簿2: test2
//        //   - Sheet1: id, name, location
//        String[] columns2 = {"id", "name", "location"};
//        String[] types2 = {"INTEGER", "VARCHAR", "VARCHAR"};
//        mockDatabase.addTable("test2", "Sheet1", columns2, types2);
//
//        // 工作簿2: test2
//        //   - Sheet2: code, description
//        String[] columns3 = {"code", "description"};
//        String[] types3 = {"VARCHAR", "VARCHAR"};
//        mockDatabase.addTable("test2", "Sheet2", columns3, types3);
//
//        // 工作簿3: test3
//        //   - Sheet1: id, name, value
//        String[] columns4 = {"id", "name", "value"};
//        String[] types4 = {"INTEGER", "VARCHAR", "DOUBLE"};
//        mockDatabase.addTable("test3", "Sheet1", columns4, types4);
//    }
//
//    // ========== 基本属性测试 ==========
//
//    @Test
//    public void testGetDatabaseProductName() throws SQLException {
//        assertEquals("xlSQL Native Engine", metaData.getDatabaseProductName());
//    }
//
//    @Test
//    public void testGetDatabaseProductVersion() throws SQLException {
//        assertEquals("4.0", metaData.getDatabaseProductVersion());
//    }
//
//    @Test
//    public void testGetDriverName() throws SQLException {
//        assertEquals("xlSQL Native Driver", metaData.getDriverName());
//    }
//
//    @Test
//    public void testGetDriverVersion() throws SQLException {
//        assertEquals("4.0", metaData.getDriverVersion());
//    }
//
//    @Test
//    public void testGetDriverMajorVersion() {
//        assertEquals(4, metaData.getDriverMajorVersion());
//    }
//
//    @Test
//    public void testGetDriverMinorVersion() {
//        assertEquals(0, metaData.getDriverMinorVersion());
//    }
//
//    @Test
//    public void testGetDatabaseMajorVersion() throws SQLException {
//        assertEquals(4, metaData.getDatabaseMajorVersion());
//    }
//
//    @Test
//    public void testGetDatabaseMinorVersion() throws SQLException {
//        assertEquals(0, metaData.getDatabaseMinorVersion());
//    }
//
//    @Test
//    public void testGetJDBCMajorVersion() throws SQLException {
//        assertEquals(4, metaData.getJDBCMajorVersion());
//    }
//
//    @Test
//    public void testGetJDBCMinorVersion() throws SQLException {
//        assertEquals(0, metaData.getJDBCMinorVersion());
//    }
//
//    // ========== 布尔属性测试 ==========
//
//    @Test
//    public void testAllTablesAreSelectable() throws SQLException {
//        assertTrue(metaData.allTablesAreSelectable());
//    }
//
//    @Test
//    public void testIsReadOnly() throws SQLException {
//        assertTrue(metaData.isReadOnly());
//    }
//
//    @Test
//    public void testNullsAreSortedLow() throws SQLException {
//        assertTrue(metaData.nullsAreSortedLow());
//    }
//
//    @Test
//    public void testNullPlusNonNullIsNull() throws SQLException {
//        assertTrue(metaData.nullPlusNonNullIsNull());
//    }
//
//    // ========== getTables() 测试 ==========
//
////    @Test
////    public void testGetTables_AllTables() throws SQLException {
////        ResultSet rs = metaData.getTables(null, null, null, null);
////        assertNotNull(rs);
////
////        List<String> tableNames = new ArrayList<>();
////        while (rs.next()) {
////            String schema = rs.getString("TABLE_SCHEM");
////            String table = rs.getString("TABLE_NAME");
////            String type = rs.getString("TABLE_TYPE");
////
////            assertNotNull(schema);
////            assertNotNull(table);
////            assertEquals("TABLE", type);
////
////            tableNames.add(schema + "." + table);
////        }
////        rs.close();
////
////        // 应该包含所有表
////        assertTrue(tableNames.contains("test1.Sheet1"));
////        assertTrue(tableNames.contains("test2.Sheet1"));
////        assertTrue(tableNames.contains("test2.Sheet2"));
////        assertTrue(tableNames.contains("test3.Sheet1"));
////        assertEquals(4, tableNames.size());
////    }
//
////    @Test
////    public void testGetTables_WithSchemaPattern() throws SQLException {
////        ResultSet rs = metaData.getTables(null, "test1", null, null);
////        assertNotNull(rs);
////
////        List<String> tableNames = new ArrayList<>();
////        while (rs.next()) {
////            String schema = rs.getString("TABLE_SCHEM");
////            String table = rs.getString("TABLE_NAME");
////            assertEquals("test1", schema);
////            tableNames.add(schema + "." + table);
////        }
////        rs.close();
////
////        assertEquals(1, tableNames.size());
////        assertTrue(tableNames.contains("test1.Sheet1"));
////    }
//
////    @Test
////    public void testGetTables_WithTablePattern() throws SQLException {
////        ResultSet rs = metaData.getTables(null, null, "Sheet1", null);
////        assertNotNull(rs);
////
////        List<String> tableNames = new ArrayList<>();
////        while (rs.next()) {
////            String table = rs.getString("TABLE_NAME");
////            assertEquals("Sheet1", table);
////            tableNames.add(table);
////        }
////        rs.close();
////
////        assertEquals(3, tableNames.size());
////    }
//
////    @Test
////    public void testGetTables_WithWildcardPattern() throws SQLException {
////        ResultSet rs = metaData.getTables(null, "test%", "Sheet%", null);
////        assertNotNull(rs);
////
////        List<String> tableNames = new ArrayList<>();
////        while (rs.next()) {
////            String schema = rs.getString("TABLE_SCHEM");
////            String table = rs.getString("TABLE_NAME");
////            assertTrue(schema.startsWith("test"));
////            assertTrue(table.startsWith("Sheet"));
////            tableNames.add(schema + "." + table);
////        }
////        rs.close();
////
////        assertTrue(tableNames.size() >= 3);
////    }
//
//    @Test
//    public void testGetTables_WithTableTypes() throws SQLException {
//        ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"});
//        assertNotNull(rs);
//
//        int count = 0;
//        while (rs.next()) {
//            assertEquals("TABLE", rs.getString("TABLE_TYPE"));
//            count++;
//        }
//        rs.close();
//
//        assertEquals(4, count);
//    }
//
//    @Test
//    public void testGetTables_WithUnsupportedTableTypes() throws SQLException {
//        ResultSet rs = metaData.getTables(null, null, null, new String[]{"VIEW"});
//        assertNotNull(rs);
//
//        assertFalse(rs.next());
//        rs.close();
//    }
//
//    // ========== getColumns() 测试 ==========
//
////    @Test
////    public void testGetColumns_AllColumns() throws SQLException {
////        ResultSet rs = metaData.getColumns(null, null, null, null);
////        assertNotNull(rs);
////
////        List<String> columnInfo = new ArrayList<>();
////        while (rs.next()) {
////            String schema = rs.getString("TABLE_SCHEM");
////            String table = rs.getString("TABLE_NAME");
////            String column = rs.getString("COLUMN_NAME");
////            int dataType = rs.getInt("DATA_TYPE");
////            String typeName = rs.getString("TYPE_NAME");
////            int ordinal = rs.getInt("ORDINAL_POSITION");
////
////            assertNotNull(schema);
////            assertNotNull(table);
////            assertNotNull(column);
////            assertTrue(dataType > 0);
////            assertNotNull(typeName);
////            assertTrue(ordinal > 0);
////
////            columnInfo.add(schema + "." + table + "." + column);
////        }
////        rs.close();
////
////        // 验证包含所有列
////        assertTrue(columnInfo.contains("test1.Sheet1.id"));
////        assertTrue(columnInfo.contains("test1.Sheet1.name"));
////        assertTrue(columnInfo.contains("test2.Sheet1.id"));
////        assertTrue(columnInfo.contains("test2.Sheet2.code"));
////    }
//
//    @Test
//    public void testGetColumns_WithSchemaPattern() throws SQLException {
//        ResultSet rs = metaData.getColumns(null, "test1", null, null);
//        assertNotNull(rs);
//
//        List<String> schemas = new ArrayList<>();
//        while (rs.next()) {
//            String schema = rs.getString("TABLE_SCHEM");
//            assertEquals("test1", schema);
//            schemas.add(schema);
//        }
//        rs.close();
//
//        assertTrue(schemas.size() > 0);
//        assertTrue(schemas.stream().allMatch(s -> s.equals("test1")));
//    }
//
//    @Test
//    public void testGetColumns_WithTablePattern() throws SQLException {
//        ResultSet rs = metaData.getColumns(null, null, "Sheet1", null);
//        assertNotNull(rs);
//
//        List<String> tables = new ArrayList<>();
//        while (rs.next()) {
//            String table = rs.getString("TABLE_NAME");
//            assertEquals("Sheet1", table);
//            tables.add(table);
//        }
//        rs.close();
//
//        assertTrue(tables.size() > 0);
//        assertTrue(tables.stream().allMatch(t -> t.equals("Sheet1")));
//    }
//
//    @Test
//    public void testGetColumns_WithColumnPattern() throws SQLException {
//        ResultSet rs = metaData.getColumns(null, null, null, "id");
//        assertNotNull(rs);
//
//        List<String> columns = new ArrayList<>();
//        while (rs.next()) {
//            String column = rs.getString("COLUMN_NAME");
//            assertEquals("id", column);
//            columns.add(column);
//        }
//        rs.close();
//
//        assertEquals(3, columns.size()); // test1.Sheet1.id, test2.Sheet1.id, test3.Sheet1.id
//    }
//
////    @Test
////    public void testGetColumns_ColumnMetadata() throws SQLException {
////        ResultSet rs = metaData.getColumns(null, "test1", "Sheet1", null);
////        assertNotNull(rs);
////
////        // 验证第一列（id）
////        assertTrue(rs.next());
////        assertEquals("test1", rs.getString("TABLE_SCHEM"));
////        assertEquals("Sheet1", rs.getString("TABLE_NAME"));
////        assertEquals("id", rs.getString("COLUMN_NAME"));
////        assertEquals(1, rs.getInt("ORDINAL_POSITION"));
////        assertEquals("YES", rs.getString("IS_NULLABLE"));
////        assertEquals("NO", rs.getString("IS_AUTOINCREMENT"));
////
////        // 验证第二列（name）
////        assertTrue(rs.next());
////        assertEquals("name", rs.getString("COLUMN_NAME"));
////        assertEquals(2, rs.getInt("ORDINAL_POSITION"));
////
////        rs.close();
////    }
//
//    // ========== getSchemas() 测试 ==========
//
//    @Test
//    public void testGetSchemas_AllSchemas() throws SQLException {
//        ResultSet rs = metaData.getSchemas();
//        assertNotNull(rs);
//
//        Set<String> schemas = new HashSet<>();
//        while (rs.next()) {
//            String schema = rs.getString("TABLE_SCHEM");
//            assertNotNull(schema);
//            schemas.add(schema);
//        }
//        rs.close();
//
//        assertTrue(schemas.contains("test1"));
//        assertTrue(schemas.contains("test2"));
//        assertTrue(schemas.contains("test3"));
//        assertEquals(3, schemas.size());
//    }
//
////    @Test
////    public void testGetSchemas_WithPattern() throws SQLException {
////        ResultSet rs = metaData.getSchemas(null, "test1");
////        assertNotNull(rs);
////
////        List<String> schemas = new ArrayList<>();
////        while (rs.next()) {
////            String schema = rs.getString("TABLE_SCHEM");
////            assertEquals("test1", schema);
////            schemas.add(schema);
////        }
////        rs.close();
////
////        assertEquals(1, schemas.size());
////    }
//
////    @Test
////    public void testGetSchemas_WithWildcardPattern() throws SQLException {
////        ResultSet rs = metaData.getSchemas(null, "test%");
////        assertNotNull(rs);
////
////        Set<String> schemas = new HashSet<>();
////        while (rs.next()) {
////            String schema = rs.getString("TABLE_SCHEM");
////            assertTrue(schema.startsWith("test"));
////            schemas.add(schema);
////        }
////        rs.close();
////
////        assertEquals(3, schemas.size());
////    }
//
//    // ========== getTableTypes() 测试 ==========
//
//    @Test
//    public void testGetTableTypes() throws SQLException {
//        ResultSet rs = metaData.getTableTypes();
//        assertNotNull(rs);
//
//        List<String> types = new ArrayList<>();
//        while (rs.next()) {
//            String type = rs.getString("TABLE_TYPE");
//            assertNotNull(type);
//            types.add(type);
//        }
//        rs.close();
//
//        assertEquals(1, types.size());
//        assertEquals("TABLE", types.get(0));
//    }
//
//    // ========== getTypeInfo() 测试 ==========
//
//    @Test
//    public void testGetTypeInfo() throws SQLException {
//        ResultSet rs = metaData.getTypeInfo();
//        assertNotNull(rs);
//
//        Set<String> typeNames = new HashSet<>();
//        while (rs.next()) {
//            String typeName = rs.getString("TYPE_NAME");
//            int dataType = rs.getInt("DATA_TYPE");
//            int precision = rs.getInt("PRECISION");
//
//            assertNotNull(typeName);
//            assertTrue(dataType > 0);
//            assertTrue(precision > 0);
//
//            typeNames.add(typeName);
//        }
//        rs.close();
//
//        // 验证包含常见类型
//        assertTrue(typeNames.contains("VARCHAR"));
//        assertTrue(typeNames.contains("INTEGER"));
//        assertTrue(typeNames.contains("DOUBLE"));
//        assertTrue(typeNames.contains("DATE"));
//    }
//
//    @Test
//    public void testGetTypeInfo_TypeList() throws SQLException {
//        ResultSet rs = metaData.getTypeInfo();
//        assertNotNull(rs);
//
//        Map<String, Integer> typeMap = new HashMap<>();
//        while (rs.next()) {
//            String typeName = rs.getString("TYPE_NAME");
//            int dataType = rs.getInt("DATA_TYPE");
//            typeMap.put(typeName, dataType);
//        }
//        rs.close();
//
//        // 验证类型映射
//        assertTrue(typeMap.containsKey("VARCHAR"));
//        assertTrue(typeMap.containsKey("INTEGER"));
//        assertTrue(typeMap.containsKey("DOUBLE"));
//    }
//
//    // ========== 空结果集方法测试 ==========
//
//    @Test
//    public void testGetCatalogs() throws SQLException {
//        ResultSet rs = metaData.getCatalogs();
//        assertNotNull(rs);
//        assertFalse(rs.next());
//        rs.close();
//    }
//
//    @Test
//    public void testGetPrimaryKeys() throws SQLException {
//        ResultSet rs = metaData.getPrimaryKeys(null, "test1", "Sheet1");
//        assertNotNull(rs);
//        assertFalse(rs.next());
//        rs.close();
//    }
//
//    @Test
//    public void testGetIndexInfo() throws SQLException {
//        ResultSet rs = metaData.getIndexInfo(null, "test1", "Sheet1", false, false);
//        assertNotNull(rs);
//        assertFalse(rs.next());
//        rs.close();
//    }
//
//    @Test
//    public void testGetImportedKeys() throws SQLException {
//        ResultSet rs = metaData.getImportedKeys(null, "test1", "Sheet1");
//        assertNotNull(rs);
//        assertFalse(rs.next());
//        rs.close();
//    }
//
//    @Test
//    public void testGetExportedKeys() throws SQLException {
//        ResultSet rs = metaData.getExportedKeys(null, "test1", "Sheet1");
//        assertNotNull(rs);
//        assertFalse(rs.next());
//        rs.close();
//    }
//
//    @Test
//    public void testGetColumnPrivileges() throws SQLException {
//        ResultSet rs = metaData.getColumnPrivileges(null, "test1", "Sheet1", null);
//        assertNotNull(rs);
//        assertFalse(rs.next());
//        rs.close();
//    }
//
//    @Test
//    public void testGetTablePrivileges() throws SQLException {
//        ResultSet rs = metaData.getTablePrivileges(null, "test1", "Sheet1");
//        assertNotNull(rs);
//        assertFalse(rs.next());
//        rs.close();
//    }
//
//    // ========== 模式匹配测试 ==========
//
////    @Test
////    public void testPatternMatching_Percent() throws SQLException {
////        // 测试%通配符
////        ResultSet rs = metaData.getTables(null, null, "Sheet%", null);
////        assertNotNull(rs);
////
////        int count = 0;
////        while (rs.next()) {
////            String table = rs.getString("TABLE_NAME");
////            assertTrue(table.startsWith("Sheet"));
////            count++;
////        }
////        rs.close();
////
////        assertEquals(4, count); // test1.Sheet1, test2.Sheet1, test2.Sheet2, test3.Sheet1
////    }
//
////    @Test
////    public void testPatternMatching_Underscore() throws SQLException {
////        // 测试_通配符
////        ResultSet rs = metaData.getTables(null, null, "Sheet_", null);
////        assertNotNull(rs);
////
////        int count = 0;
////        while (rs.next()) {
////            String table = rs.getString("TABLE_NAME");
////            assertTrue(table.startsWith("Sheet") && table.length() == 6);
////            count++;
////        }
////        rs.close();
////
////        assertTrue(count >= 0);
////    }
//
//    @Test
//    public void testPatternMatching_NullPattern() throws SQLException {
//        // 测试null模式（应匹配所有）
//        ResultSet rs = metaData.getTables(null, null, null, null);
//        assertNotNull(rs);
//
//        int count = 0;
//        while (rs.next()) {
//            count++;
//        }
//        rs.close();
//
//        assertEquals(4, count);
//    }
//
//    // ========== MockDatabase 实现 ==========
//
//    /**
//     * Mock ADatabase实现，用于单元测试
//     *
//     * @author rzy
//     */
//    private static class MockDatabase extends ADatabase {
//        private Map<String, TableInfo> tables = new HashMap<>();
//        private Map<String, Set<String>> schemas = new HashMap<>();
//
//        public MockDatabase() throws xlDatabaseException {
//            super(new File(System.getProperty("java.io.tmpdir")));
//        }
//
//        public void addTable(String workbook, String sheet, String[] columns, String[] types) {
//            String key = workbook.toUpperCase() + "_" + sheet.toUpperCase();
//            tables.put(key, new TableInfo(workbook, sheet, columns, types));
//
//            // 添加到模式映射（使用原始大小写，因为getSchemas返回原始名称）
//            schemas.computeIfAbsent(workbook, k -> new HashSet<>()).add(sheet);
//        }
//
//        // 重写getSchemas()方法，直接返回我们维护的模式列表
//        @Override
//        public String[] getSchemas() {
//            return schemas.keySet().toArray(new String[0]);
//        }
//
//        // 重写getTables()方法，从我们的模式映射中获取表列表
//        @Override
//        public String[] getTables(String subfolder) {
//            // 尝试原始大小写
//            Set<String> sheets = schemas.get(subfolder);
//            if (sheets == null) {
//                // 尝试大写
//                sheets = schemas.get(subfolder.toUpperCase());
//            }
//            if (sheets == null) {
//                throw new IllegalArgumentException("Schema not found: " + subfolder);
//            }
//            return sheets.toArray(new String[0]);
//        }
//
//        @Override
//        public String[] getColumnNames(String subfolder, String docname) {
//            String key = subfolder.toUpperCase() + "_" + docname.toUpperCase();
//            TableInfo table = tables.get(key);
//            if (table == null) {
//                throw new IllegalArgumentException("Table not found: " + subfolder + "." + docname);
//            }
//            return table.columns;
//        }
//
//        @Override
//        public String[] getColumnTypes(String subfolder, String docname) {
//            String key = subfolder.toUpperCase() + "_" + docname.toUpperCase();
//            TableInfo table = tables.get(key);
//            if (table == null) {
//                throw new IllegalArgumentException("Table not found: " + subfolder + "." + docname);
//            }
//            return table.types;
//        }
//
//        @Override
//        protected void readSubFolders(File dir) throws xlDatabaseException {
//            // Mock实现，不需要实际读取
//        }
//
//        @Override
//        public ASubFolder subFolderFactory(File dir, String subfolder) {
//            return null; // 不需要实现
//        }
//
//        @Override
//        public AFile fileFactory(File dir, String subfolder, String file) {
//            return null; // 不需要实现
//        }
//
//        @SuppressWarnings("unused")
//        private static class TableInfo {
//            String workbook;  // 保留用于未来扩展
//            String sheet;      // 保留用于未来扩展
//            String[] columns;
//            String[] types;
//
//            TableInfo(String workbook, String sheet, String[] columns, String[] types) {
//                this.workbook = workbook;
//                this.sheet = sheet;
//                this.columns = columns;
//                this.types = types;
//            }
//        }
//    }
//}
//
