# 测试数据异常处理改进

## 概述

本次改进将所有测试中的"文件不存在，跳过测试"逻辑改为抛出异常，确保测试数据在测试前已经准备好。

## 改进内容

### 1. 测试基类自动生成测试数据

在所有测试基类中添加了`@BeforeAll`方法，自动生成测试数据文件：

- `NativeEngineTestBase`
- `H2EngineTestBase`
- `HSQLDBEngineTestBase`
- `PerformanceTestBase`

**实现逻辑**：
```java
@BeforeAll
public static void setUpTestData() throws Exception {
    String baseDir = System.getProperty("user.dir");
    
        String databaseDir = baseDir + File.separator + "database";
        
        // 确保database目录存在
        File dbDir = new File(databaseDir);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        
        // 检查测试数据文件是否存在（在database目录下）
        File test1File = new File(databaseDir, "test1.xls");
        File test2File = new File(databaseDir, "test2.xls");
        File test3File = new File(databaseDir, "test3.xls");
    
    // 如果文件不存在，自动生成
    if (!test1File.exists() || !test2File.exists() || !test3File.exists()) {
        System.out.println("测试数据文件不存在，正在生成...");
        TestDataFileGenerator.generateAllTestFiles(baseDir);
        System.out.println("测试数据文件生成完成");
    }
    
    // 验证文件确实存在，如果不存在则抛出异常
    if (!test1File.exists()) {
        throw new RuntimeException("测试数据文件生成失败: test1.xls 不存在。请检查文件权限和磁盘空间。");
    }
    // ... 其他文件验证
}
```

### 2. 异常处理改进

**之前的处理方式**（错误）：
```java
} catch (Exception e) {
    if (e.getMessage() != null && e.getMessage().contains("not found")) {
        System.out.println("跳过测试: test1.xls文件不存在");
        return;  // 静默跳过测试
    }
    throw e;
}
```

**改进后的处理方式**（正确）：
```java
} catch (Exception e) {
    // 测试数据文件应该在@BeforeAll中已生成，如果不存在则抛出异常
    if (e.getMessage() != null && e.getMessage().contains("not found")) {
        throw new RuntimeException(
            "测试数据文件不存在。这不应该发生，因为@BeforeAll应该已经生成了测试数据。错误: " + e.getMessage(), 
            e);
    }
    throw e;
}
```

### 3. 修改的文件列表

以下测试文件已更新异常处理逻辑：

#### 压力测试
- ✅ `QueryPerformanceTest.java` - 所有测试方法
- ✅ `ConcurrentQueryTest.java` - 所有测试方法
- ✅ `LargeDatasetTest.java` - 所有测试方法
- ✅ `MemoryUsageTest.java` - 所有测试方法

#### 集成测试
- ✅ `H2EngineIntegrationTest.java` - 所有测试方法
- ✅ `HSQLDBEngineIntegrationTest.java` - 所有测试方法
- ✅ `H2AggregateQueryTest.java` - 所有测试方法
- ✅ `HSQLDBAggregateQueryTest.java` - 所有测试方法
- ✅ `H2JoinQueryTest.java` - 所有测试方法
- ✅ `HSQLDBJoinQueryTest.java` - 所有测试方法
- ✅ `EngineComparisonTest.java` - 所有测试方法
- ✅ `EngineCompatibilityTest.java` - 所有测试方法
- ✅ `ComprehensiveExcelTest.java` - `testTestFileExists`方法

## 改进效果

### 之前的问题

1. **测试静默失败**：文件不存在时测试被跳过，但测试结果显示为"通过"
2. **问题难以发现**：测试数据生成失败时，测试仍然"通过"
3. **测试结果不准确**：实际测试没有执行，但统计为通过

### 改进后的效果

1. **问题立即暴露**：如果测试数据文件不存在，测试会立即失败并显示明确的错误信息
2. **自动生成数据**：测试前自动检查并生成测试数据文件
3. **测试结果准确**：所有测试都真正执行，结果更可靠

## 使用说明

### 运行测试

测试会自动处理测试数据：

```bash
# 运行所有测试（会自动生成测试数据）
mvn test

# 运行特定测试
mvn test -Dtest=QueryPerformanceTest
```

### 手动生成测试数据

如果需要手动生成测试数据：

```bash
# 方法1：使用Java命令
mvn test-compile
java -cp "target/test-classes:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
     com.jsdiff.xlsql.util.TestDataFileGenerator

# 方法2：使用Maven
mvn test-compile exec:java \
    -Dexec.mainClass="com.jsdiff.xlsql.util.TestDataFileGenerator" \
    -Dexec.classpathScope=test
```

### 错误处理

如果测试数据文件生成失败，会抛出`RuntimeException`，包含详细的错误信息：

```
java.lang.RuntimeException: 测试数据文件生成失败: test1.xls 不存在。请检查文件权限和磁盘空间。
    at com.jsdiff.xlsql.base.NativeEngineTestBase.setUpTestData(NativeEngineTestBase.java:XX)
    ...
```

**常见原因**：
1. 磁盘空间不足
2. 文件权限问题
3. 目录不存在
4. Apache POI依赖问题

## 注意事项

1. **@BeforeAll是静态方法**：确保测试数据在所有测试实例之间共享
2. **文件检查**：即使生成成功，也会验证文件确实存在
3. **异常信息**：提供详细的错误信息，帮助快速定位问题
4. **性能影响**：文件检查很快，不会显著影响测试性能

## 测试验证

运行以下测试验证改进效果：

```bash
# 删除测试数据文件
rm test1.xls test2.xls test3.xls

# 运行测试（应该自动生成文件）
mvn test -Dtest=QueryPerformanceTest#testSimpleSelectPerformance

# 验证文件已生成
ls -lh test*.xls
```

---

**最后更新**: 2025-12-04  
**作者**: daichangya

