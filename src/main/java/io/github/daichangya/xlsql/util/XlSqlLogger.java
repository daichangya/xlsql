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
package io.github.daichangya.xlsql.util;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

/**
 * XlSqlLogger - XLSQL JDBC 驱动统一日志工具类
 * 
 * <p>该类提供了统一的日志接口，专门为 JDBC 驱动在第三方服务中使用而设计。
 * 主要特性：</p>
 * <ul>
 *   <li>可配置的日志级别（通过系统属性或环境变量）</li>
 *   <li>自动过滤敏感信息（密码、URL 中的凭证等）</li>
 *   <li>性能优化（避免不必要的字符串拼接）</li>
 *   <li>分类日志（连接、SQL、错误、性能）</li>
 *   <li>结构化日志格式</li>
 * </ul>
 * 
 * <p>配置方式：</p>
 * <ul>
 *   <li>系统属性：-Dxlsql.log.level=INFO</li>
 *   <li>环境变量：XLSQL_LOG_LEVEL=WARNING</li>
 *   <li>默认级别：WARNING（只记录警告和错误）</li>
 * </ul>
 * 
 * <p>支持的日志级别：OFF, SEVERE, WARNING, INFO, FINE, ALL</p>
 * 
 * @author daichangya
 */
public class XlSqlLogger {
    
    /** 日志前缀 */
    private static final String LOG_PREFIX = "[XLSQL]";
    
    /** 系统属性键：日志级别 */
    private static final String PROP_LOG_LEVEL = "xlsql.log.level";
    
    /** 环境变量键：日志级别 */
    private static final String ENV_LOG_LEVEL = "XLSQL_LOG_LEVEL";
    
    /** 系统属性键：日志文件路径 */
    private static final String PROP_LOG_FILE = "xlsql.log.file";
    
    /** 环境变量键：日志文件路径 */
    private static final String ENV_LOG_FILE = "XLSQL_LOG_FILE";
    
    /** 默认日志级别：INFO（记录连接、SQL执行等重要信息，适合开发和调试） */
    private static final Level DEFAULT_LEVEL = Level.INFO;
    
    /** 默认日志文件路径 */
    private static final String DEFAULT_LOG_FILE = 
        System.getProperty("user.home") + File.separator + ".xlsql" + File.separator + "xlsql.log";
    
    /** 根日志记录器 */
    private static final Logger ROOT_LOGGER = Logger.getLogger("io.github.daichangya.xlsql");
    
    /** 当前配置的日志级别 */
    private static volatile Level configuredLevel = null;
    
    /** 文件处理器（用于文件日志输出） */
    private static FileHandler fileHandler = null;
    
    /** 控制台处理器（用于控制台日志输出） */
    private static ConsoleHandler consoleHandler = null;
    
    /** 密码过滤模式 */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "(?i)(password|pwd|pass)=[^;&\\s]+", 
        Pattern.CASE_INSENSITIVE
    );
    
    /** URL 密码过滤模式 */
    private static final Pattern URL_PASSWORD_PATTERN = Pattern.compile(
        "://[^:]+:[^@]+@", 
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 静态初始化：配置日志级别、控制台和文件输出
     */
    static {
        configureLogLevel();
        configureConsoleHandler();
        configureFileHandler();
    }
    
    /**
     * 配置日志级别
     * 优先级：系统属性 > 环境变量 > 默认值
     */
    private static void configureLogLevel() {
        String levelStr = System.getProperty(PROP_LOG_LEVEL);
        if (levelStr == null || levelStr.isEmpty()) {
            levelStr = System.getenv(ENV_LOG_LEVEL);
        }
        
        if (levelStr != null && !levelStr.isEmpty()) {
            try {
                configuredLevel = Level.parse(levelStr.toUpperCase());
                ROOT_LOGGER.setLevel(configuredLevel);
                // 设置所有子日志记录器的级别
                setLoggerLevel(ROOT_LOGGER, configuredLevel);
            } catch (IllegalArgumentException e) {
                // 无效的日志级别，使用默认值
                configuredLevel = DEFAULT_LEVEL;
                ROOT_LOGGER.setLevel(DEFAULT_LEVEL);
            }
        } else {
            configuredLevel = DEFAULT_LEVEL;
            ROOT_LOGGER.setLevel(DEFAULT_LEVEL);
        }
    }
    
    /**
     * 设置日志记录器及其子记录器的级别
     */
    private static void setLoggerLevel(Logger logger, Level level) {
        logger.setLevel(level);
        // 递归设置子记录器（Java 8 兼容）
        java.util.Enumeration<String> loggerNames = 
            java.util.logging.LogManager.getLogManager().getLoggerNames();
        while (loggerNames.hasMoreElements()) {
            String name = loggerNames.nextElement();
            if (name.startsWith(logger.getName() + ".")) {
                Logger childLogger = Logger.getLogger(name);
                if (childLogger.getLevel() == null || 
                    childLogger.getLevel().intValue() > level.intValue()) {
                    childLogger.setLevel(level);
                }
            }
        }
    }
    
    /**
     * 配置控制台处理器（将日志输出到控制台）
     */
    private static void configureConsoleHandler() {
        // 检查是否禁用控制台日志
        String disableConsoleLog = System.getProperty("xlsql.log.console.disable");
        if ("true".equalsIgnoreCase(disableConsoleLog)) {
            return;
        }
        
        try {
            // 检查是否已经有控制台处理器
            boolean hasConsoleHandler = false;
            for (java.util.logging.Handler handler : ROOT_LOGGER.getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    hasConsoleHandler = true;
                    break;
                }
            }
            
            // 如果没有控制台处理器，添加一个
            if (!hasConsoleHandler) {
                consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new SimpleFormatter());
                consoleHandler.setLevel(Level.ALL); // 控制台记录所有级别的日志
                ROOT_LOGGER.addHandler(consoleHandler);
            }
        } catch (SecurityException e) {
            // 安全权限不足，忽略
        }
    }
    
    /**
     * 配置文件处理器（将日志输出到文件）
     */
    private static void configureFileHandler() {
        // 检查是否禁用文件日志
        String disableFileLog = System.getProperty("xlsql.log.file.disable");
        if ("true".equalsIgnoreCase(disableFileLog)) {
            return;
        }
        
        try {
            // 获取日志文件路径
            String logFilePath = getLogFilePathInternal();
            
            // 确保日志目录存在
            File logFile = new File(logFilePath);
            File logDir = logFile.getParentFile();
            if (logDir != null && !logDir.exists()) {
                logDir.mkdirs();
            }
            
            // 创建文件处理器
            fileHandler = new FileHandler(logFilePath, true); // append = true
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL); // 文件记录所有级别的日志
            
            // 添加到根日志记录器
            ROOT_LOGGER.addHandler(fileHandler);
            
        } catch (IOException e) {
            // 文件日志初始化失败，只输出到控制台
            ROOT_LOGGER.warning("Failed to initialize file logging: " + e.getMessage());
        } catch (SecurityException e) {
            // 安全权限不足，只输出到控制台
            ROOT_LOGGER.warning("Security exception when initializing file logging: " + e.getMessage());
        }
    }
    
    /**
     * 获取日志文件路径（私有方法，内部使用）
     * 优先级：系统属性 > 环境变量 > 默认路径
     * 
     * @return 日志文件路径
     */
    private static String getLogFilePathInternal() {
        // 首先检查系统属性
        String logPath = System.getProperty(PROP_LOG_FILE);
        if (logPath != null && !logPath.isEmpty()) {
            return logPath;
        }
        
        // 然后检查环境变量
        logPath = System.getenv(ENV_LOG_FILE);
        if (logPath != null && !logPath.isEmpty()) {
            return logPath;
        }
        
        // 使用默认路径：~/.xlsql/xlsql.log
        return DEFAULT_LOG_FILE;
    }
    
    /**
     * 获取指定类的日志记录器
     * 
     * @param clazz 类对象
     * @return 日志记录器
     */
    private static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        if (logger.getLevel() == null) {
            logger.setLevel(configuredLevel);
        }
        return logger;
    }
    
    /**
     * 过滤敏感信息
     * 
     * @param message 原始消息
     * @return 过滤后的消息
     */
    private static String filterSensitiveInfo(String message) {
        if (message == null) {
            return null;
        }
        
        // 过滤密码属性
        String filtered = PASSWORD_PATTERN.matcher(message).replaceAll("$1=***");
        
        // 过滤 URL 中的密码
        filtered = URL_PASSWORD_PATTERN.matcher(filtered).replaceAll("://***:***@");
        
        return filtered;
    }
    
    /**
     * 格式化日志消息
     * 
     * @param category 日志分类（CONNECTION, SQL, ERROR, PERFORMANCE）
     * @param message 消息内容
     * @return 格式化后的消息
     */
    private static String formatMessage(String category, String message) {
        return String.format("%s [%s] %s", LOG_PREFIX, category, filterSensitiveInfo(message));
    }
    
    /**
     * 检查是否应该记录指定级别的日志
     * 
     * @param logger 日志记录器
     * @param level 日志级别
     * @return 是否应该记录
     */
    private static boolean isLoggable(Logger logger, Level level) {
        return logger.isLoggable(level);
    }
    
    // ==================== 连接日志 ====================
    
    /**
     * 记录连接建立日志
     * 
     * @param clazz 调用类
     * @param message 消息
     */
    public static void logConnection(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.INFO)) {
            logger.info(formatMessage("CONNECTION", message));
        }
    }
    
    /**
     * 记录连接建立日志（使用 Supplier 延迟求值）
     * 
     * @param clazz 调用类
     * @param messageSupplier 消息提供者
     */
    public static void logConnection(Class<?> clazz, Supplier<String> messageSupplier) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.INFO)) {
            logger.info(formatMessage("CONNECTION", messageSupplier.get()));
        }
    }
    
    /**
     * 记录连接错误日志
     * 
     * @param clazz 调用类
     * @param message 消息
     * @param throwable 异常对象
     */
    public static void logConnectionError(Class<?> clazz, String message, Throwable throwable) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.SEVERE)) {
            logger.log(Level.SEVERE, formatMessage("CONNECTION", message), throwable);
        }
    }
    
    /**
     * 记录连接关闭日志
     * 
     * @param clazz 调用类
     * @param message 消息
     */
    public static void logConnectionClose(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.FINE)) {
            logger.fine(formatMessage("CONNECTION", message));
        }
    }
    
    // ==================== SQL 执行日志 ====================
    
    /**
     * 记录 SQL 执行日志
     * 
     * @param clazz 调用类
     * @param sql SQL 语句
     * @param executionTime 执行时间（毫秒）
     * @param rowCount 结果行数（-1 表示未知）
     */
    public static void logSql(Class<?> clazz, String sql, long executionTime, int rowCount) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.INFO)) {
            String message = String.format("SQL: %s | Time: %dms | Rows: %s", 
                filterSensitiveInfo(sql), 
                executionTime,
                rowCount >= 0 ? String.valueOf(rowCount) : "N/A");
            logger.info(formatMessage("SQL", message));
        }
    }
    
    /**
     * 记录 SQL 执行日志（简化版本，不包含执行时间和行数）
     * 
     * @param clazz 调用类
     * @param sql SQL 语句
     */
    public static void logSql(Class<?> clazz, String sql) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.FINE)) {
            logger.fine(formatMessage("SQL", "SQL: " + filterSensitiveInfo(sql)));
        }
    }
    
    /**
     * 记录 SQL 解析日志
     * 
     * @param clazz 调用类
     * @param sql SQL 语句
     */
    public static void logSqlParse(Class<?> clazz, String sql) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.INFO)) {
            logger.info(formatMessage("SQL", "Parsing SQL: " + filterSensitiveInfo(sql)));
        }
    }
    
    /**
     * 记录 SQL 解析错误日志
     * 
     * @param clazz 调用类
     * @param sql SQL 语句
     * @param throwable 异常对象
     */
    public static void logSqlParseError(Class<?> clazz, String sql, Throwable throwable) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.SEVERE)) {
            String message = "SQL parsing failed: " + filterSensitiveInfo(sql);
            logger.log(Level.SEVERE, formatMessage("SQL", message), throwable);
        }
    }
    
    // ==================== 错误日志 ====================
    
    /**
     * 记录错误日志
     * 
     * @param clazz 调用类
     * @param message 消息
     * @param throwable 异常对象
     */
    public static void logError(Class<?> clazz, String message, Throwable throwable) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.SEVERE)) {
            logger.log(Level.SEVERE, formatMessage("ERROR", message), throwable);
        }
    }
    
    /**
     * 记录错误日志（无异常）
     * 
     * @param clazz 调用类
     * @param message 消息
     */
    public static void logError(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.SEVERE)) {
            logger.severe(formatMessage("ERROR", message));
        }
    }
    
    /**
     * 记录警告日志
     * 
     * @param clazz 调用类
     * @param message 消息
     */
    public static void logWarning(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.WARNING)) {
            logger.warning(formatMessage("WARNING", message));
        }
    }
    
    /**
     * 记录警告日志（使用 Supplier 延迟求值）
     * 
     * @param clazz 调用类
     * @param messageSupplier 消息提供者
     */
    public static void logWarning(Class<?> clazz, Supplier<String> messageSupplier) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.WARNING)) {
            logger.warning(formatMessage("WARNING", messageSupplier.get()));
        }
    }
    
    // ==================== 性能日志 ====================
    
    /**
     * 记录性能日志（慢查询警告）
     * 
     * @param clazz 调用类
     * @param message 消息
     * @param executionTime 执行时间（毫秒）
     */
    public static void logPerformance(Class<?> clazz, String message, long executionTime) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.WARNING)) {
            String perfMessage = String.format("%s | Time: %dms", message, executionTime);
            logger.warning(formatMessage("PERFORMANCE", perfMessage));
        }
    }
    
    /**
     * 记录性能日志（一般信息）
     * 
     * @param clazz 调用类
     * @param message 消息
     */
    public static void logPerformance(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.FINE)) {
            logger.fine(formatMessage("PERFORMANCE", message));
        }
    }
    
    // ==================== 通用日志方法 ====================
    
    /**
     * 记录信息日志
     * 
     * @param clazz 调用类
     * @param message 消息
     */
    public static void logInfo(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.INFO)) {
            logger.info(formatMessage("INFO", filterSensitiveInfo(message)));
        }
    }
    
    /**
     * 记录调试日志
     * 
     * @param clazz 调用类
     * @param message 消息
     */
    public static void logDebug(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.FINE)) {
            logger.fine(formatMessage("DEBUG", filterSensitiveInfo(message)));
        }
    }
    
    /**
     * 记录调试日志（使用 Supplier 延迟求值）
     * 
     * @param clazz 调用类
     * @param messageSupplier 消息提供者
     */
    public static void logDebug(Class<?> clazz, Supplier<String> messageSupplier) {
        Logger logger = getLogger(clazz);
        if (isLoggable(logger, Level.FINE)) {
            logger.fine(formatMessage("DEBUG", messageSupplier.get()));
        }
    }
    
    /**
     * 获取当前配置的日志级别
     * 
     * @return 日志级别
     */
    public static Level getConfiguredLevel() {
        return configuredLevel != null ? configuredLevel : DEFAULT_LEVEL;
    }
    
    /**
     * 检查是否启用了指定级别的日志
     * 
     * @param level 日志级别
     * @return 是否启用
     */
    public static boolean isLoggable(Level level) {
        return level.intValue() >= getConfiguredLevel().intValue();
    }
    
    /**
     * 获取当前日志文件路径（公共方法）
     * 
     * @return 日志文件路径，如果未配置文件日志则返回 null
     */
    public static String getLogFilePath() {
        if (fileHandler != null) {
            // 调用私有方法获取日志文件路径
            return getLogFilePathInternal();
        }
        return null;
    }
    
    /**
     * 关闭文件日志处理器（清理资源）
     */
    public static void closeFileHandler() {
        if (fileHandler != null) {
            try {
                fileHandler.close();
                ROOT_LOGGER.removeHandler(fileHandler);
                fileHandler = null;
            } catch (Exception e) {
                // 忽略异常
            }
        }
    }
}

