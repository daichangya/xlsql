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
package com.jsdiff.xlsql.ui;

import java.io.*;
import java.sql.*;
/**
 * Print time.
 * 
 * @author daichangya
 */
public class CmdSql implements IStateCommand {
    private XlUi xldba;

    /**
     * Creates a new instance of this class.
     * @param dba object
     */
    public CmdSql(final XlUi dba) {
        xldba = dba;
    }
    
    /**
     * enter sql session
     */
    public final int execute() {
        System.out.println("Connected to SQL engine...\n");

        try {
            Statement s = xldba.con.createStatement();
            BufferedReader in = new BufferedReader(
                                        new InputStreamReader(System.in));

            while (true) {
                System.out.print("xlsql> -" + xldba.instance.getEngine()
                                 + " engine#");
                System.out.flush();

                String sql = in.readLine().replaceFirst("(;+)$","");

                if ((sql == null) || sql.equals("quit")) {
                    xldba.con.close();
                    break;
                }

                if (sql.length() == 0) {
                    System.out.println("");
                    continue;
                }

                try {
                    // We don't know if this is a query or some kind of
                    // update, so we use execute() instead of executeQuery()
                    // or executeUpdate() If the return value is true, it was
                    // a query, else an update.
                    boolean status = s.execute(sql);

                    // Some complex SQL queries can return more than one set
                    // of results, so loop until there are no more results
                    do {
                        if (status) { // it was a query and returns a ResultSet

                            ResultSet rs = s.getResultSet(); // Get results
                            printResultsTable(rs, System.out); // Display them
                        } else {
                            // If the SQL command that was executed was some
                            // kind of update rather than a query, then it
                            // doesn't return a ResultSet.  Instead, we just
                            // print the number of rows that were affected.
                            int numUpdates = s.getUpdateCount();
                            System.out.println("Ok. " + numUpdates
                                               + " rows affected.");
                        }


                        // Now go see if there are even more results, and
                        // continue the results display loop if there are.
                        status = s.getMoreResults();
                    } while (status || (s.getUpdateCount() != -1));
                }
                // If a SQLException is thrown, display an error message.
                // Note that SQLExceptions can have a general message and a
                // DB-specific message returned by getSQLState()
                 catch (SQLException e) {
                    System.err.println("SQLException: " + e.getMessage() + ":"
                                       + e.getSQLState());
                }
                // Each time through this loop, check to see if there were any
                // warnings.  Note that there can be a whole chain of warnings.
                 finally { // print out any warnings that occurred

                    SQLWarning w;

                    for (w = xldba.con.getWarnings();
                         w != null;
                         w = w.getNextWarning()) {
                        System.err.println("WARNING: " + w.getMessage() + ":"
                                           + w.getSQLState());
                    }
                    System.out.println("");
                }
            }
        }
        // Handle exceptions that occur during argument parsing, database
        // connection setup, etc.  For SQLExceptions, print the details.
         catch (SQLException sqe) {
            System.err.println("SQL State: " + sqe.getSQLState());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } finally {
            System.out.println("");
        }
        return xldba.CONNECTED;
    }

    /**
     * <p>
     * Copyright (c) 2025 David Flanagan.  All rights reserved. This code is
     * from the book Java Examples in a Nutshell, 3nd Edition. It is provided
     * AS-IS, WITHOUT ANY WARRANTY either expressed or implied. You may study,
     * use, and modify it for any non-commercial purpose, including teaching
     * and use in open-source projects. You may distribute it non-commercially
     * as long as you retain this notice. For a commercial use license, or to
     * purchase the book,  please visit
     * http://www.davidflanagan.com/javaexamples3.
     * </p>
     * 
     * @param rs
     * @param output
     * 
     * @throws SQLException
     */
    static void printResultsTable(ResultSet rs, OutputStream output)
                           throws SQLException {
        // Set up the output stream
        PrintWriter out = new PrintWriter(output);

        // Get some "meta data" (column names, etc.) about the results
        ResultSetMetaData metadata = rs.getMetaData();

        // Variables to hold important data about the table to be displayed
        int numcols = metadata.getColumnCount(); // how many columns
        String[] labels = new String[numcols]; // the column labels
        int[] colwidths = new int[numcols]; // the width of each
        int[] colpos = new int[numcols]; // start position of each
        int linewidth; // total width of table


        // Figure out how wide the columns are, where each one begins, 
        // how wide each row of the table will be, etc.
        linewidth = 1; // for the initial '|'.

        for (int i = 0; i < numcols; i++) { // for each column
            colpos[i] = linewidth; // save its position
            labels[i] = metadata.getColumnLabel(i + 1); // get its label 

            // Get the column width.  If the db doesn't report one, guess
            // 30 characters.  Then check the length of the label, and use
            // it if it is larger than the column width
            int size = metadata.getColumnDisplaySize(i + 1);

            if (size == -1) {
                size = 30; // Some drivers return -1...
            }

            if (size > 500) {
                size = 30; // Don't allow unreasonable sizes
            }

            int labelsize = labels[i].length();

            if (labelsize > size) {
                size = labelsize;
            }

            colwidths[i] = size + 1; // save the column the size  
            linewidth += (colwidths[i] + 2); // increment total size
        }

        // Create a horizontal divider line we use in the table.
        // Also create a blank line that is the initial value of each 
        // line of the table
        StringBuffer divider = new StringBuffer(linewidth);
        StringBuffer blankline = new StringBuffer(linewidth);

        for (int i = 0; i < linewidth; i++) {
            divider.insert(i, '-');
            blankline.insert(i, " ");
        }

        // Put special marks in the divider line at the column positions
        for (int i = 0; i < numcols; i++) {
            divider.setCharAt(colpos[i] - 1, '+');
        }

        divider.setCharAt(linewidth - 1, '+');


        // Begin the table output with a divider line
        out.println(divider);

        // The next line of the table contains the column labels.
        // Begin with a blank line, and put the column names and column
        // divider characters "|" into it.  overwrite() is defined below.
        StringBuffer line = new StringBuffer(blankline.toString());
        line.setCharAt(0, '|');

        for (int i = 0; i < numcols; i++) {
            int pos = colpos[i] + 1 + ((colwidths[i] - labels[i].length()) / 2);
            overwrite(line, pos, labels[i]);
            overwrite(line, colpos[i] + colwidths[i], " |");
        }


        // Then output the line of column labels and another divider
        out.println(line);
        out.println(divider);

        // Now, output the table data.  Loop through the ResultSet, using
        // the next() method to get the rows one at a time. Obtain the 
        // value of each column with getObject(), and output it, much as 
        // we did for the column labels above.
        while (rs.next()) {
            line = new StringBuffer(blankline.toString());
            line.setCharAt(0, '|');

            for (int i = 0; i < numcols; i++) {
                Object value = rs.getObject(i + 1);

                if (value != null) {
                    overwrite(line, colpos[i] + 1, value.toString().trim());
                }

                overwrite(line, colpos[i] + colwidths[i], " |");
            }

            out.println(line);
        }


        // Finally, end the table with one last divider line.
        out.println(divider);
        out.flush();
    }

    /**
     * <p>
     * Copyright (c) 2025 David Flanagan.  All rights reserved. This code is
     * from the book Java Examples in a Nutshell, 3nd Edition. It is provided
     * AS-IS, WITHOUT ANY WARRANTY either expressed or implied. You may study,
     * use, and modify it for any non-commercial purpose, including teaching
     * and use in open-source projects. You may distribute it non-commercially
     * as long as you retain this notice. For a commercial use license, or to
     * purchase the book,  please visit
     * http://www.davidflanagan.com/javaexamples3.
     * </p>
     * 
     * @param b
     * @param pos
     * @param s
     */
    static private void overwrite(StringBuffer b, int pos, String s) {
        int slen = s.length(); // string length
        int blen = b.length(); // buffer length

        if ((pos + slen) > blen) {
            slen = blen - pos; // does it fit?
        }

        for (int i = 0; i < slen; i++) // copy string into buffer
        {
            b.setCharAt(pos + i, s.charAt(i));
        }
    }
}

