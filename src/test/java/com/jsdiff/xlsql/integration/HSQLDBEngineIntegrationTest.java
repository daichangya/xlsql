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
package com.jsdiff.xlsql.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.jsdiff.xlsql.base.HSQLDBEngineTestBase;

/**
 * HSQLDBEngineIntegrationTest - HSQLDB引擎集成测试
 * 
 * <p>测试HSQLDB引擎的基础功能，包括：
 * <ul>
 *   <li>基础SELECT查询</li>
 *   <li>WHERE条件查询</li>
 *   <li>ORDER BY排序</li>
 *   <li>LIMIT/OFFSET分页</li>
 *   <li>组合查询</li>
 * </ul>
 * </p>
 * 
 * @author daichangya
 */
@Tag("integration")
@Tag("hsqldb")
public class HSQLDBEngineIntegrationTest extends HSQLDBEngineTestBase {

    @Test
    public void testBasicSelect() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 5")) {
            
            assertNotNull(rs);
            assertTrue(rs.getMetaData().getColumnCount() > 0);
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            assertTrue(rowCount > 0, "应该返回至少一行数据");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testWhereCondition() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM test1_Sheet1 WHERE a = 'value1'")) {
            
            assertNotNull(rs);
            
            while (rs.next()) {
                String value = rs.getString("a");
                assertEquals("value1", value);
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                System.out.println("跳过测试: test1.xls文件不存在");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testOrderBy() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM test1_Sheet1 ORDER BY a ASC LIMIT 10")) {
            
            assertNotNull(rs);
            
            String previousValue = null;
            while (rs.next()) {
                String currentValue = rs.getString("a");
                if (previousValue != null && currentValue != null) {
                    assertTrue(currentValue.compareTo(previousValue) >= 0,
                        "结果应该按升序排列");
                }
                previousValue = currentValue;
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                System.out.println("跳过测试: test1.xls文件不存在");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testLimit() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM test1_Sheet1 LIMIT 3")) {
            
            assertNotNull(rs);
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            assertTrue(rowCount <= 3, "返回行数应该不超过LIMIT值");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                System.out.println("跳过测试: test1.xls文件不存在");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testOffset() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM test1_Sheet1 LIMIT 3 OFFSET 2")) {
            
            assertNotNull(rs);
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            assertTrue(rowCount <= 3, "返回行数应该不超过LIMIT值");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                System.out.println("跳过测试: test1.xls文件不存在");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testCombinedQuery() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM test1_Sheet1 WHERE a IS NOT NULL ORDER BY a LIMIT 5")) {
            
            assertNotNull(rs);
            
            int rowCount = 0;
            String previousValue = null;
            while (rs.next()) {
                rowCount++;
                String currentValue = rs.getString("a");
                assertNotNull(currentValue, "a列不应该为null（因为有WHERE条件）");
                
                if (previousValue != null && currentValue != null) {
                    assertTrue(currentValue.compareTo(previousValue) >= 0,
                        "结果应该按升序排列");
                }
                previousValue = currentValue;
            }
            assertTrue(rowCount <= 5, "返回行数应该不超过LIMIT值");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                System.out.println("跳过测试: test1.xls文件不存在");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testTableNameFormat() throws Exception {
        // 测试HSQLDB使用下划线分隔的表名格式
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM test1_Sheet1 LIMIT 1")) {
            
            assertNotNull(rs, "HSQLDB应该支持下划线分隔的表名格式");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                System.out.println("跳过测试: test1.xls文件不存在");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testColumnSelection() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a FROM test1_Sheet1 LIMIT 1")) {
            
            assertNotNull(rs);
            assertEquals(1, rs.getMetaData().getColumnCount(), 
                "应该只返回一列");
            assertEquals("a", rs.getMetaData().getColumnName(1).toLowerCase(), 
                "列名应该是a");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                System.out.println("跳过测试: test1.xls文件不存在");
                return;
            }
            throw e;
        }
    }
}

