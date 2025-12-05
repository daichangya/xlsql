/*
 * x l S Q L  
 * (c) daichangya, xlsql.jsdiff.com
 * See XLSQL-license.txt for license details
 *
 */
package io.github.daichangya.xlsql.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * xlSavepoint - xlSQL保存点实现
 * 
 * <p>该类实现了JDBC Savepoint接口，作为后端数据库保存点的包装器。
 * 所有方法都委托给后端数据库的保存点对象。</p>
 * 
 * @author daichangya
 */
public class xlSavepoint implements Savepoint {

    xlConnection xlCon;
    Savepoint dbSave;
    
    //~ Constructors �����������������������������������������������������������

    /**
     * 创建保存点实例
     * 
     * @param con 关联的xlConnection对象
     * @param save 后端数据库的Savepoint对象
     */
    public xlSavepoint(xlConnection con, Savepoint save) {
        xlCon = con;
        dbSave = save;
    }

    //~ Methods ����������������������������������������������������������������

    /**
     * 获取保存点的ID
     * 
     * <p>返回后端数据库保存点的唯一标识符。</p>
     * 
     * @return 保存点的ID
     * @throws SQLException 如果获取失败则抛出异常
     * @see Savepoint#getSavepointId
     */
    public int getSavepointId() throws SQLException {
        return dbSave.getSavepointId();
    }

    /**
     * 获取保存点的名称
     * 
     * <p>如果保存点有名称则返回名称，否则返回null。</p>
     * 
     * @return 保存点的名称，如果没有名称则返回null
     * @throws SQLException 如果获取失败则抛出异常
     * @see Savepoint#getSavepointName
     */
    public String getSavepointName() throws SQLException {
        return dbSave.getSavepointName();
    }
}