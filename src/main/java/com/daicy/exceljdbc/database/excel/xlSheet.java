
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
package com.daicy.exceljdbc.database.excel;

import com.daicy.exceljdbc.database.*;
import com.daicy.exceljdbc.database.sql.ASqlSelect;
import jxl.*;
import jxl.write.*;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
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
     * @param dir 包含 Excel 文件的（相对）根目录
     * @param folder 工作簿名称（不带扩展名）
     * @param name Excel 中的工作表标识符
     *
     * @throws xlDatabaseException 当此对象无法实例化时抛出异常
     */
    xlSheet(File dir, String folder, String name) throws xlDatabaseException {
        super(dir, folder, name);
    }

    /**
     * 创建一个新的 xlSheet 对象。
     *
     * @param dir 包含 Excel 文件的（相对）根目录
     * @param folder 工作簿名称（不带扩展名）
     * @param name Excel 中的工作表标识符
     * @param bdirty 脏标记
     */
    xlSheet(File dir, String folder, String name, boolean bdirty) {
        super(dir, folder, name, bdirty);
    }

    /**
     * 获取当前 Excel 工作表对象。
     *
     * @return JXL 库中的 Sheet 对象
     * @throws xlDatabaseException 当读取 Excel 文件失败时抛出异常
     */
    private jxl.Sheet getSheet() throws xlDatabaseException {
        // 性能问题？最好先修复 JXL.ow。 :-)
        Sheet ret = null;

        try {
            File f = new File(directory.getPath() + File.separator +
                    subFolderName + XLS);
            ret = Workbook.getWorkbook(f).getSheet(fileName);
        } catch (IOException ioe) {
            throw new xlDatabaseException("xlSQL: -excel> IO 错误: " +
                    ioe.getMessage());
        } catch (jxl.read.biff.BiffException biffe) {
            throw new xlDatabaseException("xlSQL: -excel> BIFF 错误: " +
                    biffe.getMessage());
        }

        return ret;
    }

    /**
     * 关闭工作表并刷新 SQL 引擎中的更改。
     *
     * @param subOut 更高级别的对象
     * @param select SQL 引擎
     *
     * @throws xlDatabaseException 数据库错误
     */
    public void close(Object subOut, ASqlSelect select)
            throws xlDatabaseException {
        Sheet workSheet;
        ResultSet rs;
        WritableWorkbook wbOut = (WritableWorkbook) subOut;

        if (isChanged[xlConstants.ADD]) {
            // 添加操作：创建工作表
            workSheet = wbOut.createSheet(fileName, wbOut.getNumberOfSheets());

            WritableSheet wsh = (WritableSheet) workSheet;

            try {
                rs = select.QueryData(subFolderName, fileName);
            } catch (SQLException sqe) {
                throw new xlDatabaseException(sqe.getMessage());
            }

            // 创建新的空工作表
            write(wsh, rs);
        } else if (isChanged[xlConstants.UPDATE]) {
            // 更新操作：先删除再重新创建工作表
            int i;
            WritableSheet _s;

            // 根据名称查找工作表索引
            for (i = 0; i < wbOut.getNumberOfSheets(); i++) {
                _s = wbOut.getSheet(i);

                if (_s.getName().equals(fileName)) {
                    break;
                }
            }

            // 删除工作表
            wbOut.removeSheet(i);

            try {
                rs = select.QueryData(subFolderName, fileName);
            } catch (SQLException sqe) {
                throw new xlDatabaseException(sqe.getMessage());
            }

            // 创建新的空工作表
            write(wbOut.createSheet(fileName, i), rs);
        } else if (isChanged[xlConstants.DELETE]) {
            // 删除操作：根据名称删除工作表
            int i;
            WritableSheet _s;

            // 根据名称查找工作表索引
            for (i = 0; i < wbOut.getNumberOfSheets(); i++) {
                _s = wbOut.getSheet(i);

                if (_s.getName().equals(fileName)) {
                    break;
                }
            }

            // 删除工作表
            wbOut.removeSheet(i);
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
        columnCount = sheet.getColumns(); // 获取列数
        rowCount = sheet.getRows();       // 获取行数

        // 验证工作表：列数或行数为 0 的工作表无效
        for (int z = 0; z < 1; z++) {
            if ((columnCount == 0) || (rowCount == 0)) {
                ret = false;
                break;
            }

            // 检查"标题行"，包含不完整或空单元格的工作表无效
            Cell[] c = sheet.getRow(0); // 获取第一行（标题行）

            if ((c.length == 0) || (c.length != columnCount)) {
                ret = false;
                break;
            }

            // 标题行有效，将数据传输到 columnNames 数组
            columnNames = new String[c.length];
            columnTypes = new String[c.length];

            // 检查每个列的标题，确保 CREATE TABLE 在所有 SQL 方言中都被接受
            for (int i = 0; i < c.length; i++) {
                // 标题必须是 LABEL 类型，否则无效
                if (c[i].getType() != CellType.LABEL) {
                    ret = false;
                    break;
                }

                // 列名必须匹配正则表达式模式（"^[A-Za-z0-9._-]{1,30}+$"）
                // 如果至少有一列不匹配，则表无效
//                if (!c[i].getContents().matches("^[A-Za-z0-9._-]{1,30}+$")) {
//                    ret = false;
//                    break;
//                }

                // 列名有效，传输数据
                columnNames[i] = c[i].getContents();
            }

            // Bug 修复：问题 163 - 重复的列名
            if (!ret) {
                break;
            }

            // 检查所有列名是否不同
            HashMap index = new HashMap();
            String key;
            String value;

            for (int n = 0; n < columnNames.length; n++) {
                key = columnNames[n].toUpperCase();
                value = columnNames[n];

                if (index.containsKey(key)) {
                    ret = false;
                    break;
                } else {
                    index.put(key, value);
                }
            }

            if (!ret) {
                break;
            }

            // 获取数据行用于类型检测
            Cell[] t;

            if (rowCount == 1) {
                // 只有标题行，没有数据行
                t = c;
                logger.warning(fileName + " 没有数据，假设为 VARCHAR 类型");
            } else {
                // 检查第一行数据：确定数据类型
                t = sheet.getRow(1);

                // 当值的数量少于列数时
                if (t.length != c.length) {
                    logger.warning(fileName
                            + " 可能包含无效数据，继续处理...");
                    // 无法评估所有列，初始化为 VARCHAR
                    for (int k = 0; k < columnTypes.length; k++) {
                        columnTypes[k] = "VARCHAR";
                    }
                }
            }

            columnTypes = new String[c.length];

            // 检查第一行数据中各列的值，确定 SQL 数据类型
            for (int j = 0; j < t.length; j++) {
                if ((t[j].getType() == CellType.NUMBER) ||
                        (t[j].getType() == CellType.NUMBER_FORMULA)) {
                    // 数字类型
                    columnTypes[j] = "DOUBLE";
                } else if ((t[j].getType() == CellType.LABEL) ||
                        (t[j].getType() == CellType.STRING_FORMULA)) {
                    // 标签或字符串公式类型
                    columnTypes[j] = "VARCHAR(2048)";
                } else if ((t[j].getType() == CellType.DATE) ||
                        (t[j].getType() == CellType.DATE_FORMULA)) {
                    // 日期类型
                    columnTypes[j] = "DATE";
                    if (t[j] instanceof DateCell && ((DateCell)t[j]).isTime()) {
                        // 时间类型
                        columnTypes[j] = "TIME";
                    }
                } else if ((t[j].getType() == CellType.BOOLEAN) ||
                        (t[j].getType() == CellType.BOOLEAN_FORMULA)) {
                    // 布尔类型
                    columnTypes[j] = "BIT";
                } else if (t[j].getType() == CellType.EMPTY) {
                    // 空单元格，假设为 VARCHAR
                    columnTypes[j] = "VARCHAR";
                } else {
                    // 未知类型
                    ret = false;
                    break;
                }
            }
        }

        if (!ret) {
            logger.info(fileName + " 包含非 SQL 数据：已失效");
        }

        sheet = null;
        return ret;
    }

    /**
     * 将查询结果写入到 Excel 工作表中。
     *
     * @param wsh 可写的 Excel 工作表对象
     * @param rs 查询结果集
     * @throws xlDatabaseException 当写入 Excel 文件失败时抛出异常
     */
    private void write(WritableSheet wsh, ResultSet rs)
            throws xlDatabaseException {
        // 使用 JXL API 将查询传输到 Excel 工作表
        try {
            ResultSetMetaData rsmd = rs.getMetaData();

            // 写入标题行
            int col = 0;
            int row = 0;
            int cols = rsmd.getColumnCount();
            int[] type = new int[cols];

            for (col = 0; col < cols; col++) {
                Label label = new Label(col, row, rsmd.getColumnName(col + 1));
                type[col] = xlConstants.xlType(rsmd.getColumnType(col + 1));
                wsh.addCell(label);
            }

            // 写入数据行
            row++;

            while (rs.next()) {
                for (col = 0; col < cols; col++) {
                    switch (type[col]) {
                        case 1: // 数字类型
                            jxl.write.Number nm = new jxl.write.Number(col, row,
                                    rs.getDouble(col + 1));
                            wsh.addCell(nm);
                            break;

                        case 2: // 文本类型
                            Label lb = new Label(col, row, rs.getString(col + 1));
                            wsh.addCell(lb);
                            break;

                        case 3: // 日期类型
                            java.util.Date bug;
                            if (rs.getDate(col + 1) == null) {
                                bug = new java.util.Date(0);
                            } else {
                                bug = rs.getDate(col + 1);
                            }
                            DateTime dt = new DateTime(col, row, bug);
                            wsh.addCell(dt);
                            break;

                        case 4: // 布尔类型
                            jxl.write.Boolean bl = new jxl.write.Boolean(col, row,
                                    rs.getBoolean(col + 1));
                            wsh.addCell(bl);
                            break;

                        default:
                            // MySQL 'TEXT' 类型的解决方法
                            lb = new Label(col, row, rs.getString(col + 1));
                            wsh.addCell(lb);
                    }
                }
                row++;
            }
        } catch (SQLException sqe) {
            throw new xlDatabaseException(sqe.getMessage());
        } catch (jxl.write.WriteException jxw) {
            throw new xlDatabaseException(jxw.getMessage());
        }
    }

    /**
     * 根据 SQL 类型返回 Excel 单元格类型。
     *
     * @param sqlType SQL 数据类型
     * @return Excel 单元格类型（1=数字，2=文本，3=日期，4=布尔值）
     */
    private static int xlType(int sqlType) {
        int ret = 0;

        switch (sqlType) {
            // 数字类型
            case (-6):
            case (-5):
            case (-2):
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                ret = 1;
                break;

            // 文本类型
            case 1:
            case 12:
            case 70:
                ret = 2;
                break;

            // 日期/时间类型
            case 91:
            case 92:
            case 93:
                ret = 3;
                break;

            // 布尔类型
            case -7:
            case 16:
                ret = 4;
                break;

            default:
                ret = 0;
        }

        return ret;
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
            // 创建二维数组存储数据（行数减1是因为排除了标题行）
            ret = new String[columnCount][rowCount - 1];

            for (int i = 0; i < (rowCount - 1); i++) {
                // 处理第 i 行数据
                for (int j = 0; j < columnCount; j++) {
                    Cell c = sheet.getCell(j, i + 1); // i+1 跳过标题行

                    if ((c == null) || (c.getType() == CellType.EMPTY)) {
                        // 空单元格
                        ret[j][i] = "";
                    } else if ((c.getType() == CellType.NUMBER) ||
                            (c.getType() == CellType.NUMBER_FORMULA)) {
                        // 数字类型单元格
                        try {
                            Locale.setDefault(new Locale("en", "US"));
                            Double db = new Double(((NumberCell) c).getValue());
                            ret[j][i] = db.toString();
                        } catch (ClassCastException ce) {
                            ret[j][i] = "";
                        }
                    } else if ((c.getType() == CellType.DATE) ||
                            (c.getType() == CellType.DATE_FORMULA)) {
                        // 日期类型单元格
                        try {
                            DateCell dc = (DateCell) c;
                            DateFormat dateFormat = dc.getDateFormat();
                            dateFormat.setTimeZone(gmtZone);
                            java.util.Date d = dateFormat.parse(c.getContents());
                            if (!dc.isTime()) {
                                // 日期格式
                                SimpleDateFormat canonicalDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                canonicalDateFormat.setTimeZone(gmtZone);
                                ret[j][i] = canonicalDateFormat.format(d);
                            } else {
                                // 时间格式
                                SimpleDateFormat canonicalTimeFormat = new SimpleDateFormat("HH:mm:ss");
                                canonicalTimeFormat.setTimeZone(gmtZone);
                                ret[j][i] = canonicalTimeFormat.format(d);
                            }
                        } catch (ParseException pe) {
                            ret[j][i] = "";
                        }
                    } else {
                        // 其他类型单元格
                        ret[j][i] = c.getContents();
                    }
                }
            }

            sheet = null;
        } else {
            throw new IllegalArgumentException(xlConstants.NOARGS);
        }

        return ret;
    }
}