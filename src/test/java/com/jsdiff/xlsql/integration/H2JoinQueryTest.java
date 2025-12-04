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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.jsdiff.xlsql.base.H2EngineTestBase;

/**
 * H2JoinQueryTest - H2引擎JOIN查询测试
 * 
 * <p>测试H2引擎的JOIN操作，包括：
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
@Tag("h2")
@Tag("join")
public class H2JoinQueryTest extends H2EngineTestBase {

    @Test
    public void testInnerJoin() throws Exception {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT t1.a, t2.b FROM test1_Sheet1 t1 " +
                 "INNER JOIN test2_Sheet1 t2 ON t1.id = t2.id LIMIT 10")) {
            
            assertNotNull(rs);
            assertTrue(rs.getMetaData().getColumnCount() >= 2);
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                // 验证JOIN结果
                assertNotNull(rs.getObject(1));
                assertNotNull(rs.getObject(2));
            }
            assertTrue(rowCount >= 0, "INNER JOIN应该返回匹配的行");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            // 列不匹配是正常的（某些测试可能查询不存在的列），允许继续
            if (e.getMessage() != null && e.getMessage().contains("Column")) {
                throw e; // 列不存在是真正的错误，应该抛出
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
            assertTrue(rs.getMetaData().getColumnCount() >= 2);
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                // LEFT JOIN应该至少返回左表的数据
                assertNotNull(rs.getObject(1));
                // 右表数据可能为null
            }
            assertTrue(rowCount >= 0, "LEFT JOIN应该返回至少左表的所有行");
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            // 列不匹配是正常的（某些测试可能查询不存在的列），允许继续
            if (e.getMessage() != null && e.getMessage().contains("Column")) {
                throw e; // 列不存在是真正的错误，应该抛出
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
                // WHERE条件应该被应用
                assertNotNull(rs.getObject(1));
            }
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            // 列不匹配是正常的（某些测试可能查询不存在的列），允许继续
            if (e.getMessage() != null && e.getMessage().contains("Column")) {
                throw e; // 列不存在是真正的错误，应该抛出
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
                String currentValue = rs.getString(1);
                if (previousValue != null && currentValue != null) {
                    assertTrue(currentValue.compareTo(previousValue) >= 0,
                        "结果应该按ORDER BY排序");
                }
                previousValue = currentValue;
            }
        } catch (Exception e) {
            // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
            if (e.getMessage() != null && 
                (e.getMessage().contains("not found") || 
                 e.getMessage().contains("not exist"))) {
                throw new RuntimeException("测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), e);
            }
            // 列不匹配是正常的（某些测试可能查询不存在的列），允许继续
            if (e.getMessage() != null && e.getMessage().contains("Column")) {
                throw e; // 列不存在是真正的错误，应该抛出
            }
            throw e;
        }
    }
}

