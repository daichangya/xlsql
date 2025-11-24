
/*zthinker.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
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
package com.jsdiff.xlsql.database.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jsdiff.xlsql.database.AFile;
import com.jsdiff.xlsql.database.FileType;
import com.jsdiff.xlsql.database.xlConstants;
import com.jsdiff.xlsql.database.xlDatabaseException;
import com.jsdiff.xlsql.database.sql.ASqlSelect;


/**
 * xlSheet - Excel工作表实现类
 * 
 * <p>该类表示Excel工作簿中的单个工作表，在xlSQL中对应数据库的表（table）。
 * 它负责读取和写入工作表数据，管理列名、列类型和数据值。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>读取Excel工作表的结构和数据</li>
 *   <li>验证工作表是否可以作为SQL表使用</li>
 *   <li>将查询结果写入工作表</li>
 *   <li>支持.xls和.xlsx格式</li>
 * </ul>
 *
 * @version $Revision: 1.21 $
 * @author $author$
 *
 * 由 Csongor Nyulas (csny) 修改：适配 TIME 单元格类型
 */
public class xlSheet extends AFile {

    /** GMT时区，用于日期时间处理 */
    private static final TimeZone gmtZone = TimeZone.getTimeZone("GMT");

    /**
     * 创建xlSheet对象（读取文件）
     * 
     * <p>创建工作表对象并读取文件内容，验证是否可以作为SQL表使用。</p>
     *
     * @param file Excel文件对象
     * @param fileName 工作簿名称（不含扩展名）
     * @param sheetName Excel中的工作表名称
     * @throws xlDatabaseException 如果对象无法实例化则抛出异常
     */
    xlSheet(File file, String fileName, String sheetName) throws xlDatabaseException {
        super(file, fileName, sheetName);
    }

    /**
     * 创建xlSheet对象（不读取文件）
     * 
     * <p>创建工作表对象但不读取文件，用于新建的工作表。</p>
     *
     * @param file Excel文件对象
     * @param fileName 工作簿名称（不含扩展名）
     * @param sheetName Excel中的工作表名称
     * @param bdirty 脏数据标志，表示是否需要添加
     */
    xlSheet(File file, String fileName, String sheetName, boolean bdirty) {
        super(file, fileName, sheetName, bdirty);
    }

    /**
     * 获取当前Excel工作表对象（已废弃）
     * 
     * <p><b>注意：</b>此方法返回的Sheet对象依赖于Workbook，Workbook在使用完Sheet后需要关闭。
     * 建议使用withWorkbook()方法来确保资源正确管理。</p>
     *
     * @return POI库中的Sheet对象
     * @throws xlDatabaseException 当读取Excel文件失败时抛出异常
     * @deprecated 使用withWorkbook()方法替代，以确保资源正确管理
     */
    @Deprecated
    private Sheet getSheet() throws xlDatabaseException {
        Workbook wb = openWorkbook();
        if (wb == null) {
            throw new xlDatabaseException("xlSQL: -excel> ERR: Unable to open workbook");
        }
        return wb.getSheet(getSheetName());
    }

    /**
     * 打开Workbook并返回
     * 
     * <p>根据文件类型（.xls或.xlsx）使用相应的POI类来打开工作簿。
     * <b>注意：</b>调用者负责关闭返回的Workbook。</p>
     *
     * @return Workbook对象，如果文件格式不支持则返回null
     * @throws xlDatabaseException 当读取Excel文件失败时抛出异常
     */
    private Workbook openWorkbook() throws xlDatabaseException {
        try {
            if (FileType.XLSX.equals(getFileType())) {
                return new XSSFWorkbook(getFile());
            } else if (FileType.XLS.equals(getFileType())) {
                try (FileInputStream fis = new FileInputStream(getFile())) {
                    return new HSSFWorkbook(fis);
                }
            } else {
                logger.warning("xlSQL: Unsupported file format for: " + getFile().getPath());
                return null;
            }
        } catch (IOException | InvalidFormatException e) {
            throw new xlDatabaseException("xlSQL: -excel> ERR: " + e.getMessage());
        }
    }

    /**
     * 使用Workbook执行操作，确保资源正确关闭
     * 
     * <p>这是一个资源管理辅助方法，使用try-with-resources确保Workbook在使用后自动关闭。
     * 推荐使用此方法来访问Workbook，而不是直接调用openWorkbook()。</p>
     *
     * @param operation 要在Workbook上执行的操作（函数式接口）
     * @param <T> 操作返回类型
     * @return 操作结果
     * @throws xlDatabaseException 当操作失败时抛出异常
     */
    private <T> T withWorkbook(java.util.function.Function<Workbook, T> operation) throws xlDatabaseException {
        Workbook wb = null;
        try {
            wb = openWorkbook();
            if (wb == null) {
                throw new xlDatabaseException("xlSQL: -excel> ERR: Unable to open workbook");
            }
            return operation.apply(wb);
        } finally {
            IOUtils.closeQuietly(wb);
        }
    }

    /**
     * 关闭工作表并刷新SQL引擎中的更改
     * 
     * <p>根据变更标志执行相应的操作：</p>
     * <ul>
     *   <li>ADD：在Workbook中创建新工作表并写入查询结果</li>
     *   <li>UPDATE：删除旧工作表并创建新工作表，写入查询结果</li>
     *   <li>DELETE：从Workbook中删除工作表</li>
     * </ul>
     *
     * @param subOut 更高级别的对象（Workbook对象）
     * @param select SQL查询对象，用于获取要写入的数据
     * @throws xlDatabaseException 如果操作失败则抛出异常
     */
    public void close(Object subOut, ASqlSelect select) throws xlDatabaseException {
        Workbook wb = (Workbook) subOut;
        ResultSet rs;

        // 添加操作：创建工作表并写入数据
        if (isChanged[xlConstants.ADD]) {
            String fileName = getFileName();
            Sheet sheet = wb.createSheet(fileName);
            try {
                // 从SQL引擎查询数据并写入工作表
                rs = select.QueryData(fileName, fileName);
                write(sheet, rs);
            } catch (SQLException sqe) {
                throw new xlDatabaseException(sqe.getMessage());
            }
        } else if (isChanged[xlConstants.UPDATE]) {
            // 更新操作：先删除旧工作表，再创建新工作表并写入数据
            String fileName = getFileName();
            int sheetIndex = wb.getSheetIndex(fileName);
            if (sheetIndex != -1) {
                wb.removeSheetAt(sheetIndex);
            }
            Sheet sheet = wb.createSheet(fileName);
            try {
                // 从SQL引擎查询数据并写入工作表
                rs = select.QueryData(fileName, fileName);
                write(sheet, rs);
            } catch (SQLException sqe) {
                throw new xlDatabaseException(sqe.getMessage());
            }
        } else if (isChanged[xlConstants.DELETE]) {
            // 删除操作：从Workbook中删除工作表
            int sheetIndex = wb.getSheetIndex(getFileName());
            if (sheetIndex != -1) {
                wb.removeSheetAt(sheetIndex);
            }
        }
    }

    /**
     * 读取Excel文件并验证其是否可以作为SQL表使用
     * 
     * <p>验证规则：</p>
     * <ul>
     *   <li>工作表必须存在</li>
     *   <li>必须有至少一行和一列</li>
     *   <li>第一行必须是标题行，所有单元格必须是字符串类型</li>
     *   <li>列名不能重复（不区分大小写）</li>
     * </ul>
     *
     * @return 如果文件有效则返回true，否则返回false
     * @throws xlDatabaseException 当读取Excel文件失败时抛出异常
     */
    protected boolean readFile() throws xlDatabaseException {
        Workbook wb = null;
        try {
            wb = openWorkbook();
            if (wb == null) {
                return false;
            }
            
            Sheet sheet = wb.getSheet(getSheetName());
            if (sheet == null) {
                return false;
            }
            
            boolean ret = true;
            // 获取列数和行数
            columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
            rowCount = sheet.getPhysicalNumberOfRows();

            // 验证工作表：列数或行数为0的工作表无效
            if (columnCount == 0 || rowCount == 0) {
                ret = false;
            } else {
                // 检查标题行（第一行）
                Row headerRow = sheet.getRow(0);
                if (headerRow == null || headerRow.getPhysicalNumberOfCells() != columnCount) {
                    ret = false;
                } else {
                    columnNames = new String[columnCount];
                    String[] columnTypes = new String[columnCount];

                    // 检查列名是否有效：所有单元格必须是字符串类型
                    for (int i = 0; i < columnCount; i++) {
                        Cell cell = headerRow.getCell(i);
                        if (cell == null || cell.getCellType() != CellType.STRING) {
                            ret = false;
                            break;
                        }
                        columnNames[i] = cell.getStringCellValue();
                    }

                    // 检查列名是否重复（不区分大小写）
                    if (ret) {
                        HashMap<String, String> index = new HashMap<>();
                        for (int n = 0; n < columnNames.length; n++) {
                            String key = columnNames[n].toUpperCase();
                            if (index.containsKey(key)) {
                                // 发现重复列名
                                ret = false;
                                break;
                            } else {
                                index.put(key, columnNames[n]);
                            }
                        }
                    }

                    // 检查数据类型：从第二行（第一行数据）推断列类型
                    if (ret && rowCount >= 1) {
                        Row dataRow = sheet.getRow(1);
                        for (int j = 0; j < columnCount; j++) {
                            Cell cell = dataRow.getCell(j);
                            if (cell == null) {
                                // 空单元格默认为VARCHAR类型
                                columnTypes[j] = "VARCHAR";
                            } else {
                                // 根据单元格类型推断SQL类型
                                switch (cell.getCellType()) {
                                    case NUMERIC:
                                        columnTypes[j] = "DOUBLE";
                                        break;
                                    case STRING:
                                        columnTypes[j] = "VARCHAR";
                                        break;
                                    case BOOLEAN:
                                        columnTypes[j] = "BIT";
                                        break;
                                    default:
                                        // 其他类型默认为VARCHAR
                                        columnTypes[j] = "VARCHAR";
                                }
                            }
                        }
                    }
                    // 设置列类型数组
                    this.setColumnTypes(columnTypes);
                }
            }

            if (!ret) {
                logger.info(getFileName() + " 包含非 SQL 数据：已失效");
            }
            return ret;
        } finally {
            IOUtils.closeQuietly(wb);
        }
    }

    /**
     * 将查询结果写入到Excel工作表中
     * 
     * <p>将ResultSet中的数据写入工作表，第一行为标题行（列名），后续行为数据行。
     * 根据SQL类型将数据转换为相应的Excel单元格类型。</p>
     *
     * @param sheet 可写的Excel工作表对象
     * @param rs 查询结果集
     * @throws xlDatabaseException 当写入Excel文件失败时抛出异常
     */
    private void write(Sheet sheet, ResultSet rs) throws xlDatabaseException {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            // 创建标题行（第一行）
            Row headerRow = sheet.createRow(0);

            // 写入标题行：列名
            for (int col = 0; col < cols; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(rsmd.getColumnName(col + 1));
            }

            // 写入数据行：从第二行开始
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                for (int col = 0; col < cols; col++) {
                    Cell cell = row.createCell(col);
                    // 根据SQL类型设置单元格值
                    switch (rsmd.getColumnType(col + 1)) {
                        case java.sql.Types.NUMERIC:
                            cell.setCellValue(rs.getDouble(col + 1));
                            break;
                        case java.sql.Types.VARCHAR:
                            cell.setCellValue(rs.getString(col + 1));
                            break;
                        case java.sql.Types.DATE:
                            cell.setCellValue(rs.getDate(col + 1));
                            break;
                        case java.sql.Types.BOOLEAN:
                            cell.setCellValue(rs.getBoolean(col + 1));
                            break;
                        default:
                            // 其他类型转换为字符串
                            cell.setCellValue(rs.getString(col + 1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new xlDatabaseException(e.getMessage());
        }
    }



    /**
     * 将工作表作为字符串矩阵返回
     * 
     * <p>读取工作表的所有数据行（不包括标题行），返回二维字符串数组。
     * 第一维是列，第二维是行。</p>
     *
     * @return 二维数组形式的表格数据（String[列][行]）
     * @throws xlDatabaseException 如果读取失败则抛出异常
     * @throws IllegalArgumentException 如果工作表无效则抛出异常
     */
    public String[][] getValues() throws xlDatabaseException {
        if (!validAsSqlTable) {
            throw new IllegalArgumentException(xlConstants.NOARGS);
        }

        // 使用withWorkbook确保资源正确管理
        return withWorkbook(wb -> {
            Sheet sheet = wb.getSheet(getSheetName());
            if (sheet == null) {
                return new String[][]{{""}};
            }
            
            // 创建结果数组：列数 x (行数-1)，排除标题行
            String[][] ret = new String[columnCount][rowCount - 1];

            // 遍历所有数据行（从第二行开始）
            for (int i = 0; i < (rowCount - 1); i++) {
                Row row = sheet.getRow(i + 1); // i+1跳过标题行
                if (row == null) {
                    continue;
                }
                // 遍历所有列
                for (int j = 0; j < columnCount; j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        // 空单元格返回空字符串
                        ret[j][i] = "";
                    } else {
                        // 根据单元格类型转换为字符串
                        switch (cell.getCellType()) {
                            case NUMERIC:
                                ret[j][i] = String.valueOf(cell.getNumericCellValue());
                                break;
                            case STRING:
                                ret[j][i] = cell.getStringCellValue();
                                break;
                            case BOOLEAN:
                                ret[j][i] = String.valueOf(cell.getBooleanCellValue());
                                break;
                            default:
                                // 其他类型返回空字符串
                                ret[j][i] = "";
                        }
                    }
                }
            }
            return ret;
        });
    }
}