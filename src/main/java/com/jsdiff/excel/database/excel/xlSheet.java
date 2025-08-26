
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
package com.jsdiff.excel.database.excel;

import com.jsdiff.excel.database.*;
import com.jsdiff.excel.database.sql.ASqlSelect;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.TimeZone;


/**
 * xlSheet 表示 Excel 工作簿中的单个工作表。
 *
 * @version $Revision: 1.21 $
 * @author $author$
 *
 * 由 Csongor Nyulas (csny) 修改：适配 TIME 单元格类型
 */
public class xlSheet extends AFile {
    /** Excel 文件扩展名 */
    private static final String XLS = ".xls";

    /** GMT 时区 */
    private static final TimeZone gmtZone = TimeZone.getTimeZone("GMT");

    /**
     * 创建一个新的 xlSheet 对象。
     *
     * @param file  Excel 文件
     * @param fileName 工作簿名称（不带扩展名）
     * @param sheetName Excel 中的工作表标识符
     *
     * @throws xlDatabaseException 当此对象无法实例化时抛出异常
     */
    xlSheet(File file, String fileName, String sheetName) throws xlDatabaseException {
        super(file, fileName, sheetName);
    }

    /**
     * 创建一个新的 xlSheet 对象。
     *
     * @param file Excel 文件
     * @param fileName 工作簿名称（不带扩展名）
     * @param sheetName Excel 中的工作表标识符
     * @param bdirty 脏标记
     */
    xlSheet(File file, String fileName, String sheetName, boolean bdirty) {
        super(file, fileName, sheetName, bdirty);
    }

    /**
     * 获取当前 Excel 工作表对象。
     *
     * @return JXL 库中的 Sheet 对象
     * @throws xlDatabaseException 当读取 Excel 文件失败时抛出异常
     */
    private Sheet getSheet() throws xlDatabaseException {
        Workbook wb =  null;
        try {
            if (FileType.XLSX.equals(getFileType())) {
                wb = new XSSFWorkbook(getFile());
            } else if (FileType.XLS.equals(getFileType())) {
                // 需要引入HSSFWorkbook
                wb = new HSSFWorkbook(new FileInputStream(getFile()));
            } else {
                logger.warning("xlSQL: Unsupported file format for: " + getFile().getPath());
            }
            return wb.getSheet(getSheetName());
        } catch (IOException | InvalidFormatException e) {
            throw new xlDatabaseException("xlSQL: -excel> ERR: " + e.getMessage());
        }finally {
            IOUtils.closeQuietly(wb);
        }
    }

    /**
     * 关闭工作表并刷新 SQL 引擎中的更改。
     *
     * @param subOut 更高级别的对象
     * @param select SQL 引擎
     *
     * @throws xlDatabaseException 数据库错误
     */
    public void close(Object subOut, ASqlSelect select) throws xlDatabaseException {
        Workbook wb = (Workbook) subOut;
        ResultSet rs;

        if (isChanged[xlConstants.ADD]) {
            String fileName = getFileName();
            // 添加操作：创建工作表
            Sheet sheet = wb.createSheet(fileName);
            try {
                rs = select.QueryData(fileName, fileName);
                write(sheet, rs);
            } catch (SQLException sqe) {
                throw new xlDatabaseException(sqe.getMessage());
            }
        } else if (isChanged[xlConstants.UPDATE]) {
            String fileName = getFileName();
            // 更新操作：先删除再重新创建工作表
            int sheetIndex = wb.getSheetIndex(fileName);
            if (sheetIndex != -1) {
                wb.removeSheetAt(sheetIndex);
            }
            Sheet sheet = wb.createSheet(fileName);
            try {
                rs = select.QueryData(fileName, fileName);
                write(sheet, rs);
            } catch (SQLException sqe) {
                throw new xlDatabaseException(sqe.getMessage());
            }
        } else if (isChanged[xlConstants.DELETE]) {
            // 删除操作：根据名称删除工作表
            int sheetIndex = wb.getSheetIndex(getFileName());
            if (sheetIndex != -1) {
                wb.removeSheetAt(sheetIndex);
            }
        }
    }

    /**
     * 读取 Excel 文件并验证其是否可以作为 SQL 表使用。
     *
     * @return 如果文件有效则返回 true，否则返回 false
     * @throws xlDatabaseException 当读取 Excel 文件失败时抛出异常
     */
    protected boolean readFile() throws xlDatabaseException {
        Sheet sheet = getSheet();
        boolean ret = true;
        columnCount = sheet.getRow(0).getPhysicalNumberOfCells(); // 获取列数
        rowCount = sheet.getPhysicalNumberOfRows();               // 获取行数

        // 验证工作表：列数或行数为 0 的工作表无效
        if (columnCount == 0 || rowCount == 0) {
            ret = false;
        } else {
            // 检查标题行
            Row headerRow = sheet.getRow(0);
            if (headerRow == null || headerRow.getPhysicalNumberOfCells() != columnCount) {
                ret = false;
            } else {
                columnNames = new String[columnCount];
                columnTypes = new String[columnCount];

                // 检查列名是否有效
                for (int i = 0; i < columnCount; i++) {
                    Cell cell = headerRow.getCell(i);
                    if (cell == null || cell.getCellType() != CellType.STRING) {
                        ret = false;
                        break;
                    }
                    columnNames[i] = cell.getStringCellValue();
                }

                // 检查列名是否重复
                if (ret) {
                    HashMap<String, String> index = new HashMap<>();
                    for (int n = 0; n < columnNames.length; n++) {
                        String key = columnNames[n].toUpperCase();
                        if (index.containsKey(key)) {
                            ret = false;
                            break;
                        } else {
                            index.put(key, columnNames[n]);
                        }
                    }
                }

                // 检查数据类型
                if (ret && rowCount > 1) {
                    Row dataRow = sheet.getRow(1);
                    for (int j = 0; j < columnCount; j++) {
                        Cell cell = dataRow.getCell(j);
                        if (cell == null) {
                            columnTypes[j] = "VARCHAR";
                        } else {
                            switch (cell.getCellType()) {
                                case NUMERIC:
                                    columnTypes[j] = "DOUBLE";
                                    break;
                                case STRING:
                                    columnTypes[j] = "VARCHAR(2048)";
                                    break;
                                case BOOLEAN:
                                    columnTypes[j] = "BIT";
                                    break;
                                default:
                                    columnTypes[j] = "VARCHAR";
                            }
                        }
                    }
                }
            }
        }

        if (!ret) {
            logger.info(getFileName() + " 包含非 SQL 数据：已失效");
        }
        return ret;
    }

    /**
     * 将查询结果写入到 Excel 工作表中。
     *
     * @param wsh 可写的 Excel 工作表对象
     * @param rs 查询结果集
     * @throws xlDatabaseException 当写入 Excel 文件失败时抛出异常
     */
    private void write(Sheet sheet, ResultSet rs) throws xlDatabaseException {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            Row headerRow = sheet.createRow(0);

            // 写入标题行
            for (int col = 0; col < cols; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(rsmd.getColumnName(col + 1));
            }

            // 写入数据行
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                for (int col = 0; col < cols; col++) {
                    Cell cell = row.createCell(col);
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
                            cell.setCellValue(rs.getString(col + 1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new xlDatabaseException(e.getMessage());
        }
    }



    /**
     * 将工作表作为字符串矩阵返回。
     *
     * @return 二维数组形式的表格数据
     *
     * @throws xlDatabaseException 数据库异常
     * @throws IllegalArgumentException 如果在无效数据上调用此方法
     */
    public String[][] getValues() throws xlDatabaseException {
        String[][] ret = {{ "" }};

        if (validAsSqlTable) {
            Sheet sheet = getSheet();
            ret = new String[columnCount][rowCount - 1]; // 行数减1是因为排除了标题行

            for (int i = 0; i < (rowCount - 1); i++) {
                Row row = sheet.getRow(i + 1); // i+1 跳过标题行
                for (int j = 0; j < columnCount; j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        ret[j][i] = "";
                    } else {
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
                                ret[j][i] = "";
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException(xlConstants.NOARGS);
        }

        return ret;
    }
}