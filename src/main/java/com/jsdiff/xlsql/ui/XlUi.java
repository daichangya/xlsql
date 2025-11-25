/*jsdiff.com

 Copyright (C) 2025 jsdiff
   jsdiff Information Sciences
   http://excel.jsdiff.com
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

import com.jsdiff.xlsql.database.AExporter;
import com.jsdiff.xlsql.database.xlException;
import com.jsdiff.xlsql.database.xlInstance;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;


/**
 * Main xlSQL commandline interface.
 * 
 * @version $Revision: 1.4 $
 * @author $author$
 */
public class XlUi {
    private static final String PROMPT = "xlsql>";
    static final int IDLE = 1;
    static final int CONNECTED = 2;
    static final int READ = 3;
    static final int OPEN = 4;

    //
    private int state = IDLE;
    private String command;

    // jsdiff... make private:
    Options options;
    private AExporter exporter;
    xlInstance instance;
    Connection con;
    CommandLine commandline;
    //
    

    /**
     * Creates a new XlUi object.
     */
    public XlUi() {
        setState(IDLE);
    }

    /**
     * Parse and execute.
     * @throws xlException when parse exception
     */
    public final void doIt() throws xlException {
        try {
            CommandLineParser parser = new BasicParser();
            String[] arguments = getCommand().split(" ");
            commandline = parser.parse(options, arguments);

            XlUiParser cmdlineparser = new XlUiParser();
            IStateCommand cmd = cmdlineparser.parseC(this);

            if (cmd != null) {
                int newstate = cmd.execute();
                state = (newstate > 0) ? newstate : state;
                setState(state);
            } else {
                throw new xlException("无法解析命令，请输入 -h 查看帮助信息");
            }
        } catch (MissingArgumentException e) {
            String option = e.getOption().getOpt();
            if (option == null) {
                option = e.getOption().getLongOpt();
            }

            if ("c".equals(option)) {
                throw new xlException("选项 -c 需要指定配置名称，例如: -c myconfig");
            } else if ("open".equals(option)) {
                throw new xlException("选项 -open 需要指定数据库路径，例如: -open /path/to/excel/files");
            } else if ("read".equals(option)) {
                throw new xlException("选项 -read 需要指定数据库路径，例如: -read /path/to/excel/files");
            } else if ("engine".equals(option)) {
                throw new xlException("选项 -engine 需要指定操作和引擎名称，例如: -engine SET hsqldb");
            } else if ("set".equals(option)) {
                throw new xlException("选项 -set 需要指定参数和值，例如: -set database /path/to/files");
            } else if ("show".equals(option)) {
                throw new xlException("选项 -show 需要指定要显示的参数，例如: -show ALL 或 -show engine");
            } else if ("export".equals(option)) {
                throw new xlException("选项 -export 需要完整的导出参数，例如: -export ALL AS hsqldb TO out");
            } else if ("dir2xl".equals(option)) {
                throw new xlException("选项 -dir2xl 需要完整的目录导入参数，例如: -dir2xl all in /path as list to schema.table");
            } else if ("script".equals(option)) {
                throw new xlException("选项 -script 需要指定脚本文件路径，例如: -script /path/to/script.sql");
            } else {
                throw new xlException("选项 -" + option + " 需要额外参数，请输入 -h 查看帮助信息");
            }
        } catch (UnrecognizedOptionException e) {
            throw new xlException("未知选项: " + e.getOption() + "，请输入 -h 查看帮助信息");
        } catch (ParseException pe) {
            throw new xlException("命令解析错误: " + pe.getMessage() + "，请输入 -h 查看帮助信息");
        }
    }

    /**
     * Getter for property command.
     * 
     * @return Value of property command.
     */
    public final String getCommand() {
        return command;
    }

    /**
     * Getter for property state.
     * @return Value of property state.
     */
    public final int getState() {
        return state;
    }

    /**
     * Commandline loop.
     * @throws xlException
     */
    private void go() {
        // Note: System.in should not be closed as it's a shared system resource
        // Using InputStreamReader without try-with-resources is intentional here
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\n*** caution: development release ***");
        System.out.println("Excel JDBC Driver");
        System.out.println("Copyright 2025 by jsdiff Information Sciences");
        System.out.println("http://excel.jsdiff.com\n");

        while (true) {
            switch (state) {
                case IDLE:
                    System.out.print(PROMPT + " -idle#");

                    break;

                case CONNECTED:
                    System.out.print(PROMPT + " -connected#");

                    break;

                case READ:
                    System.out.print(PROMPT + " -open read only#");

                    break;

                case OPEN:
                    System.out.print(PROMPT + " -open#");

                    break;

                default:
                    break;
            }

            try {
                System.out.flush();

                // add - in front of first option as required by commons
                String line = "-" + in.readLine();

                if ((line == null) || line.equals("-quit")
                        || line.equals("-exit") || line.equals("-bye")
                        || line.equals("-q")) {
                    break;
                }

                if (line.length() == 0) { // Ignore blank lines
                    throw new xlException("");
                }

                setCommand(line);


                // do parsing and execution
                doIt();
            } catch (xlException pe) {
                // 只打印错误消息，不打印完整堆栈跟踪（除非是调试模式）
                String message = pe.getMessage();
                if (message != null && !message.isEmpty()) {
                    System.err.println("错误: " + message);
                } else {
                    System.err.println("发生错误，请输入 -h 查看帮助信息");
                }
                System.err.println("提示: 输入 -h 查看帮助信息\n");
                continue;
            } catch (IOException io) {
                System.err.println("输入/输出错误: " + io.getMessage());
                break;
            } catch (Exception e) {
                System.err.println("未预期的错误: " + e.getMessage());
                e.printStackTrace(); // 对于未预期的异常，打印堆栈跟踪
                System.err.println("提示: 输入 -h 查看帮助信息\n");
                continue;
            }
        }
    }

    /**
     * Main method.
     * @param args (Not used.)
     */
    public static void main(final String[] args) {
        try {
            XlUi xldba = new XlUi();
            xldba.go();
        } catch (Exception e) {
            System.err.println("程序异常终止: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Setter for property command.
     * 
     * @param newcommand New value of property command.
     */
    public final void setCommand(final String newcommand) {
        command = newcommand;
    }

    /**
     * Setter for property state.
     * @param newstate New value of property state.
     */
    public final void setState(final int newstate) {
        state = newstate;

        Option option;

        if (state == IDLE) {
            options = new Options();
            // 修改 -c 选项为可选参数
            Option connectOption = new Option("c", true, "[ config ]");
            connectOption.setOptionalArg(true); // 设置参数为可选
            options.addOption(connectOption);
            options.addOption("h", false, "help");
            options.addOption("quit", false, "end xldba session");
            options.addOption("t", false, "display time");
        } else if (state == CONNECTED) {
            options = new Options();
            options.addOption("d", false, "disconnect");
            option = new Option("engine", true, 
                                "[ SET | ADD | REMOVE ] [ name ]");
            option.setArgs(2);
            options.addOption(option);
            options.addOption("h", false, "help");
            options.addOption("o", false, "open database");
            options.addOption("open", true, "[ path ]");
            options.addOption("ping", false, "ping engine");
            options.addOption("quit", false, "end xldba session");
            options.addOption("read", true, "[ path ]");
            options.addOption("r", false, "open database for read");
            option = new Option("set", true, "[ parameter ] [ value ]");
            option.setArgs(2);
            options.addOption(option);
            options.addOption("show", true, "[ ALL | parameter ]");
            options.addOption("t", false, "display time");
        } else if (state == READ) {
            options = new Options();
            options.addOption("cat", false, "catalog");
            options.addOption("close", false, "close");
            option = new Option("export", true, 
                 "[ ALL | (sch.)tab ] AS [ hsqldb | mysql | xml ] TO [ path | engine | out ]");
            option.setArgs(5);
            options.addOption(option);
            options.addOption("h", false, "help");
            options.addOption("quit", false, "end xldba session");
            options.addOption("t", false, "display time");
        } else if (state == OPEN) {
            options = new Options();
            options.addOption("close", false, "close");
            option = new Option("dir2xl", true, 
                 "[ all | files | subdirs ] in [ path ] as [ list | tree ] to "
                 + " [ (schema.)table ]");
            option.setArgs(7);
            options.addOption(option);
            options.addOption("h", false, "help");
            options.addOption("quit", false, "end xldba session");
            options.addOption("sql", false, "interactive SQL session");
            options.addOption("script", true, "[ file ] run SQL script");
            options.addOption("t", false, "display time");
        }
    }

    public AExporter getExporter() {
        return exporter;
    }

    public void setExporter(AExporter exporter) {
        this.exporter = exporter;
    }
}

