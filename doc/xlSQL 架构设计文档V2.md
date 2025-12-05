# XLSQL 架构设计文档

## 1. 整体架构概述

XLSQL 是一个将Excel文件作为数据库处理的JDBC驱动实现，其核心架构分为以下几个层次：

1. **JDBC驱动层**：实现标准JDBC接口，提供SQL查询能力
2. **数据库抽象层**：定义数据库、表、行等抽象概念
3. **Excel处理层**：实际处理Excel文件的读写操作
4. **SQL转换层**：将SQL语句转换为对Excel数据的操作

## 2. 核心接口与抽象类

### 2.1 数据库抽象接口 (`ADatabase`)

`ADatabase` 是所有数据库实现的基类，定义了数据库的基本操作：

```java
public abstract class ADatabase {
    // 数据库基本操作
    public abstract void readSubFolders(File dir) throws xlDatabaseException;
    public abstract ASubFolder subFolderFactory(File dir, String subfolder);
    public abstract AFile fileFactory(File dir, String subfolder, String file);
    
    // 获取子文件夹和文件
    public abstract Map<String, ASubFolder> getSubfolders();
    public abstract Map<String, AFile> getFiles();
}
```

### 2.2 Excel存储接口 (`IExcelStore`)

```java
public interface IExcelStore {
    Map<String, ASubFolder> getStore();
    void addStore(String name, ASubFolder folder);
}
```

### 2.3 Excel读取接口 (`IExcelReader`)

```java
public interface IExcelReader {
    void readWorkbooks(File dir) throws xlDatabaseException;
}
```

## 3. 关键类关系

### 3.1 数据库实现类 (`xlDatabase`)

`xlDatabase` 是 `ADatabase` 的具体实现，同时也是 `IExcelStore` 的实现：

```java
public class xlDatabase extends ADatabase implements IExcelReader, IExcelStore {
    // 实现ADatabase和IExcelStore的方法
    // 使用IExcelReader来读取Excel文件
}
```

### 3.2 Excel读取器实现

系统提供了两种Excel读取器实现：

1. **读取器 (`ModernExcelReader`)**:
   ```java
   public class ModernExcelReader implements IExcelReader {
       private final IExcelStore store;
       // 使用Java 8 Stream API实现并行读取
   }
   ```

### 3.3 JDBC驱动核心类 (`xlDriver`)

```java
public class xlDriver implements Driver {
    // 实现JDBC Driver接口
    // 负责创建数据库连接
}
```

### 3.4 连接实现类 (`xlConnection`)

```java
public abstract class xlConnection implements Connection, Constants {
    protected ADatabase datastore;
    protected Connection dbCon;
    // 实现JDBC Connection接口
}
```

有多个具体实现：
- `xlConnectionHSQLDB` (HSQLDB后端)
- `xlConnectionMySQL` (MySQL后端)

## 4. 类关系图

```
┌─────────────┐       ┌──────────────┐
│  xlDriver   │──────▶│ xlConnection │
└─────────────┘       └──────────────┘
                            ▲
                            │
                    ┌───────┴───────┐
                    │               │
           ┌─────────────────┐ ┌─────────────────┐
           │ xlConnectionHSQLDB │ │ xlConnectionMySQL │
           └─────────────────┘ └─────────────────┘
                    ▲
                    │
           ┌────────┴─────────┐
           │    xlDatabase    │
           └────────┬─────────┘
                    │
           ┌────────▼─────────┐       ┌────────────────┐
           │   IExcelStore    │◀──────│  IExcelReader  │
           └──────────────────┘       └───────┬───────┘
                                              │
                                     ┌────────┴─────────┐
                                     │   jxlReader      │
                                     └────────┬─────────┘
                                     ┌────────▼─────────┐
                                     │ ModernExcelReader│
                                     └──────────────────┘
```

## 5. 数据流分析

1. **连接建立流程**:
   - 用户通过 `DriverManager.getConnection()` 获取连接
   - `xlDriver` 创建 `xlConnection` 实例
   - `xlConnection` 初始化 `xlDatabase` 并配置后端数据库连接

2. **查询执行流程**:
   - 用户通过 `Connection.createStatement()` 创建语句
   - 语句执行时，`xlDatabase` 通过 `IExcelReader` 读取Excel数据
   - 数据通过后端数据库(HSQLDB/MySQL)处理并返回结果

3. **Excel读取流程**:
   - `xlDatabase` 使用 `IExcelReader` 实现(如 `ModernExcelReader`)
   - 读取器遍历Excel文件，创建 `xlWorkbook` 和 `xlSheet` 对象
   - 数据存储在 `IExcelStore` 实现中

## 6. 设计模式应用

1. **工厂模式**:
   - `xlDatabaseFactory` 创建数据库实例
   - `xlConnection.factory()` 创建连接实例

2. **适配器模式**:
   - `xlDatabase` 适配Excel文件到数据库接口
   - `xlConnection` 适配后端数据库到JDBC接口

3. **策略模式**:
   - 可插拔的 `IExcelReader` 实现
   - 不同的SQL方言处理策略

4. **组合模式**:
   - `ADatabase`/`ASubFolder`/`AFile` 层次结构

## 7. 扩展点

1. **添加新的Excel读取器**:
   - 实现 `IExcelReader` 接口
   - 在 `xlDatabase` 中使用新实现

2. **支持新的后端数据库**:
   - 继承 `xlConnection` 实现新后端
   - 更新 `xlConnection.factory()` 方法

3. **添加新的文件格式支持**:
   - 扩展 `AFile` 和对应工厂方法
   - 实现新的文件处理逻辑

这个架构设计提供了良好的扩展性和灵活性，同时保持了核心功能的稳定性。通过清晰的接口定义和抽象层次，各组件可以独立演进而不会影响整体系统。