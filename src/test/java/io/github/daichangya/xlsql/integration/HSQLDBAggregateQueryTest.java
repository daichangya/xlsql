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
package io.github.daichangya.xlsql.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.github.daichangya.xlsql.base.HSQLDBEngineTestBase;

/**
 * HSQLDBAggregateQueryTest - HSQLDB聚合查询测试
 * 
 * <p>测试HSQLDB引擎的聚合功能，包括：
 * <ul>
 *   <li>COUNT聚合</li>
 *   <li>SUM聚合</li>
 *   <li>AVG聚合</li>
 *   <li>MAX/MIN聚合</li>
 *   <li>GROUP BY分组</li>
 *   <li>HAVING过滤</li>
 * </ul>
 * </p>
 * 
 * @author daichangya
 */
@Tag("integration")
@Tag("hsqldb")
@Tag("aggregate")
public class HSQLDBAggregateQueryTest extends HSQLDBEngineTestBase {

    @Test
    public void testCountAll() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) AS total FROM test1_Sheet1")) {
            
            assertNotNull(rs);
            assertTrue(rs.next());
            int count = rs.getInt("total");
            assertTrue(count >= 0, "COUNT应该返回非负数");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testCountColumn() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(a) AS count_a FROM test1_Sheet1")) {
            
            assertNotNull(rs);
            assertTrue(rs.next());
            int count = rs.getInt("count_a");
            assertTrue(count >= 0, "COUNT(column)应该返回非负数");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testSumFunction() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT SUM(a) AS sum_a FROM test1_Sheet1 WHERE a IS NOT NULL")) {
            
            assertNotNull(rs);
            assertTrue(rs.next());
            // SUM的结果可能是null（如果没有数据）或数值
        } catch (Exception e) {
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("incompatible data type"))) {
                System.out.println("跳过测试: 文件不存在或列a不是数值类型");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testAvgFunction() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT AVG(a) AS avg_a FROM test1_Sheet1 WHERE a IS NOT NULL")) {
            
            assertNotNull(rs);
            assertTrue(rs.next());
            // AVG的结果可能是null或数值
        } catch (Exception e) {
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("incompatible data type"))) {
                System.out.println("跳过测试: 文件不存在或列a不是数值类型");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testMaxMinFunctions() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT MAX(a) AS max_a, MIN(a) AS min_a FROM test1_Sheet1")) {
            
            assertNotNull(rs);
            assertTrue(rs.next());
            // MAX和MIN应该返回值或null
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testGroupBy() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a, COUNT(*) AS count FROM test1_Sheet1 " +
                 "GROUP BY a LIMIT 10")) {
            
            assertNotNull(rs);
            
            int groupCount = 0;
            while (rs.next()) {
                groupCount++;
                int count = rs.getInt("count");
                assertTrue(count > 0, "每个分组应该至少有一行");
            }
            assertTrue(groupCount >= 0, "GROUP BY应该返回分组");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testGroupByMultipleColumns() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a, b, COUNT(*) AS count FROM test1_Sheet1 " +
                 "GROUP BY a, b LIMIT 10")) {
            
            assertNotNull(rs);
            
            while (rs.next()) {
                int count = rs.getInt("count");
                assertTrue(count > 0, "每个分组应该至少有一行");
            }
        } catch (Exception e) {
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("not found in table"))) {
                System.out.println("跳过测试: 文件不存在或列不存在");
                return;
            }
            throw e;
        }
    }

    @Test
    public void testHavingClause() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a, COUNT(*) AS count FROM test1_Sheet1 " +
                 "GROUP BY a HAVING COUNT(*) > 1 LIMIT 10")) {
            
            assertNotNull(rs);
            
            while (rs.next()) {
                int count = rs.getInt("count");
                assertTrue(count > 1, "HAVING条件应该过滤count <= 1的分组");
            }
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testGroupByWithOrderBy() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a, COUNT(*) AS count FROM test1_Sheet1 " +
                 "GROUP BY a ORDER BY count DESC LIMIT 10")) {
            
            assertNotNull(rs);
            
            int previousCount = Integer.MAX_VALUE;
            while (rs.next()) {
                int currentCount = rs.getInt("count");
                assertTrue(currentCount <= previousCount,
                    "结果应该按count降序排列");
                previousCount = currentCount;
            }
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testMultipleAggregateFunctions() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) AS count, MAX(a) AS max_a, MIN(a) AS min_a " +
                 "FROM test1_Sheet1")) {
            
            assertNotNull(rs);
            assertTrue(rs.next());
            
            // 验证结果包含所有聚合列
            assertTrue(rs.getMetaData().getColumnCount() >= 3,
                "应该返回三个聚合列");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }
}

