# 图书管理系统数据库初始化指引

本项目使用 MySQL 作为数据库并通过原生 JDBC 访问。请按照以下步骤初始化数据库表结构。

## 环境要求
- MySQL 8.0 及以上版本
- 已安装 `mysql` 客户端工具（命令行）

## 初始化步骤
- 确认数据库连接配置：在 `src/main/java/org/example/dao/DBConnection.java` 中设置正确的 `URL`、`USER`、`PASSWORD`。
- 在项目根目录执行：
  - `mysql -u root -p < sql/schema.sql`
- 或者在任意 MySQL 客户端中手动运行 `sql/schema.sql` 内容。

## 脚本说明
- `sql/schema.sql` 会创建数据库 `library_management` 并包含以下表：
  - `admins`：管理员账号信息（以 `username` 作为主键）
  - `books`：图书信息，包含 `id`、`title`、`category`、`total_copies`、`available_copies`、`remarks`、`created_time`、`updated_time`
- 同时包含基础的种子数据：一个示例管理员与一本示例图书。

## 运行程序
- 初始化完成后，直接运行项目（Maven 项目，IDE 运行主入口）。
- 程序通过 `DBConnection` 连接到 `library_management` 数据库并访问上述两张表。
