package com.chenchen.act_manager.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class SqliteConfig {

    @Value("${sqlite.db-path}")
    private String dbPath;

    @Value("${sqlite.init-sql:}")
    private String initSql;

    @Bean
    public DataSource dataSource() {
        org.sqlite.SQLiteDataSource dataSource = new org.sqlite.SQLiteDataSource();

        // 处理数据库文件路径
        String url = "jdbc:sqlite:" + extractDbPath();
        dataSource.setUrl(url);

        // 初始化数据库表
        //initializeDatabase(url);

        return dataSource;
    }

    private String extractDbPath() {
        if (dbPath.startsWith("classpath:")) {
            // 从类路径中提取路径
            String path = dbPath.substring("classpath:".length());
            try {
                // 获取项目根目录
                String projectRoot = new File("").getAbsolutePath();
                String dbDirectory = projectRoot + path.substring(0, path.lastIndexOf("/"));
                String dbFile = projectRoot + path;

                // 创建目录（如果不存在）
                new File(dbDirectory).mkdirs();

                return dbFile;
            } catch (Exception e) {
                // 如果文件路径处理失败，使用相对路径
                return path;
            }
        }
        return dbPath;
    }

    private void initializeDatabase(String url) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // 创建用户表
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100)," +
                    "phone VARCHAR(20)," +
                    "status INTEGER DEFAULT 1," +  // 1: 正常, 0: 禁用
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "update_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

            stmt.execute(createUserTable);

            // 插入测试数据（如果表为空）
            String checkData = "SELECT COUNT(*) as count FROM users";
            ResultSet rs = stmt.executeQuery(checkData);
            if (rs.next() && rs.getInt("count") == 0) {
                String insertTestData = "INSERT INTO users (username, password, email, phone) VALUES " +
                        "('admin', '123456', 'admin@example.com', '13800138000')," +
                        "('user1', '123456', 'user1@example.com', '13800138001')," +
                        "('user2', '123456', 'user2@example.com', '13800138002')";
                stmt.execute(insertTestData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}