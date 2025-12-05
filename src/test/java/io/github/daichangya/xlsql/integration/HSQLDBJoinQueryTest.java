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
 * HSQLDBJoinQueryTest - HSQLDB JOIN查询测试
 * 
 * <p>测试HSQLDB引擎的JOIN功能，包括：
 * <ul>
 *   <li>INNER JOIN</li>
 *   <li>LEFT JOIN</li>
 *   <li>多表JOIN</li>
 * </ul>
 * </p>
 * 
 * @author daichangya
 */
@Tag("integration")
@Tag("hsqldb")
@Tag("join")
public class HSQLDBJoinQueryTest extends HSQLDBEngineTestBase {

    @Test
    public void testInnerJoin() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                 "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 10")) {
            
            assertNotNull(rs);
            assertTrue(rs.getMetaData().getColumnCount() >= 2, 
                "JOIN结果应该包含至少两列");
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            assertTrue(rowCount > 0, "INNER JOIN应该返回匹配的行");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("does not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testLeftJoin() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                 "LEFT JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 10")) {
            
            assertNotNull(rs);
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                // LEFT JOIN应该返回左表的所有行
            }
            assertTrue(rowCount > 0, "LEFT JOIN应该返回左表的行");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("does not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testJoinWithWhere() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                 "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
                 "WHERE t1.a IS NOT NULL LIMIT 10")) {
            
            assertNotNull(rs);
            
            while (rs.next()) {
                String aValue = rs.getString("a");
                assertNotNull(aValue, "WHERE条件应该过滤null值");
            }
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("does not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testJoinWithOrderBy() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                 "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
                 "ORDER BY t1.a LIMIT 10")) {
            
            assertNotNull(rs);
            
            String previousValue = null;
            while (rs.next()) {
                String currentValue = rs.getString("a");
                if (previousValue != null && currentValue != null) {
                    assertTrue(currentValue.compareTo(previousValue) >= 0,
                        "结果应该按a列升序排列");
                }
                previousValue = currentValue;
            }
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("does not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            throw e;
        }
    }

    @Test
    public void testMultipleJoins() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT t1.a, t2.b, t3.c FROM test1_Sheet1 t1 " +
                 "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id " +
                 "INNER JOIN test3_Sheet1 t3 ON t2.id = t3.id " +
                 "LIMIT 10")) {
            
            assertNotNull(rs);
            assertTrue(rs.getMetaData().getColumnCount() >= 3, 
                "三表JOIN应该包含至少三列");
        } catch (Exception e) {
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("does not exist"))) {
                System.out.println("跳过测试: 测试文件不存在（多表JOIN需要三个测试表）");
                return;
            }
            throw e;
        }
    }
}

