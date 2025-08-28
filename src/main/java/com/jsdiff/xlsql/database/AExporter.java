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
package com.jsdiff.xlsql.database;

import com.jsdiff.xlsql.database.export.*;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Abstract Exporter.
 * 
 * @version $Revision: 1.11 $
 * @author $author$
 */
public abstract class AExporter extends AReader {

    public static final int HSQLDB = 1;
    public static final int MYSQL = 2;
    public static final int XML = 3;
    
    private AReader reader;
    private ASqlFormatter formatter;
    private IExportHandler handler;
    private List lines = new ArrayList();
    
    /**
     * Creates a new Exporter object.
     * 
     * @param dir ( root ) directory where datasource is stored
     * 
     * @throws xlDatabaseException when a database error occurs
     */
    public AExporter(File dir) throws xlDatabaseException {
        super(dir);
    }

    public void export(int format, PrintStream ps) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create();
        export();
        //handler.write(lines);
        handler.close();
    }
    
    public void export(String schema, int format, PrintStream ps) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create();
        exportSchema(schema);
        //handler.write(lines);
        handler.close();
    }
    
    public void export(String schema, String table, int format, PrintStream ps) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create();
        exportTable(schema, table);
        //handler.write(lines);
        handler.close();
    }

    public void export(int format, File exportfile) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(exportfile);
        export();
        //handler.write(lines);
        handler.close();
    }

    public void export(String schema, int format, File exportfile) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(exportfile);
        exportSchema(schema);
        //handler.write(lines);
        handler.close();
    }

    public void export(String schema, String table, int format, File exportfile)
                throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(exportfile);
        exportTable(schema, table);
        //handler.write(lines);
        handler.close();
    }
    
    public void export(int format, java.sql.Connection jdbc) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(jdbc);
        export();
        //handler.write(lines);
        handler.close();
    }
    
    public void export(String schema, int format, java.sql.Connection jdbc) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(jdbc);
        exportSchema(schema);
        //handler.write(lines);
        handler.close();
    }
    
    public void export(String schema, String table, int format, java.sql.Connection jdbc) throws xlDatabaseException {
        reader = this;
        createFormatter(format);
        handler = xlExportHandlerFactory.create(jdbc);
        exportTable(schema, table);
        //handler.write(lines);
        handler.close();
    }
    
    private void createFormatter(int format) {
        switch (format) {
            case HSQLDB:
                this.formatter = new xlHsqldbFormatter();
                break;
            case MYSQL:
                this.formatter = new xlMysqlFormatter();
                break;
            case XML:
                this.formatter = new XmlFormatter();
                break;
            default:
                throw new IllegalArgumentException(); //desc
        }
    }

    private void export() throws xlDatabaseException {
        String[] schemas = reader.getSchemas();
        for (int i=0; i < schemas.length; i++) {
            exportSchema(schemas[i]);
        }
        //handler.write(lines);
        //lines.clear();
    }
    
    private void exportSchema(String schema) throws xlDatabaseException {
        assert (formatter != null);
        
        lines.add(formatter.wCreateSchema(schema));
        String[] tables = reader.getTables(schema);
        for (int i=0; i < tables.length; i++) {
            exportTable(schema, tables[i]);
        }
        
    }
    
    private void exportTable(String schema, String table) throws xlDatabaseException { 
        String[] columns = reader.getColumnNames(schema, table);
        String[] types = reader.getColumnTypes(schema, table);

        if (formatter.wDropTable(schema, table) != null) {
            lines.add(formatter.wDropTable(schema, table));
        }

        lines.add(formatter.wCreateTable(schema, table, columns, types));

        int R = reader.getRows(schema, table);
        int C = columns.length;
        String[][] matrix = reader.getValues(schema, table);
        String[] va = new String[C];

        if ((columns != null) && (types != null)) {
            for (int k = 0; k < R; k++) {
                for (int m = 0; m < C; m++) {
                    va[m] = matrix[m][k];
                }
                lines.add(formatter.wInsert(schema, table, columns, types, va));
            }
            lines.add(formatter.wLast());
        }
        handler.write(lines);
        lines.clear();
    }
}