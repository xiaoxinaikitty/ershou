package com.xuchao.ershou.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 表结构检查工具
 */
@Component
public class TableStructureChecker implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 查询promotion_image表的结构
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = 'promotion_image' " +
                "ORDER BY ORDINAL_POSITION");

        System.out.println("======= 检查promotion_image表结构 =======");
        for (Map<String, Object> column : columns) {
            System.out.println(column.get("COLUMN_NAME") + " - " + 
                               column.get("DATA_TYPE") + 
                               (column.get("CHARACTER_MAXIMUM_LENGTH") != null ? 
                                " (" + column.get("CHARACTER_MAXIMUM_LENGTH") + ")" : ""));
        }
        System.out.println("======================================");

        // 检查image_url字段是否为text类型
        List<Map<String, Object>> imageUrlColumn = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = 'promotion_image' " +
                "AND COLUMN_NAME = 'image_url'");

        if (!imageUrlColumn.isEmpty()) {
            String dataType = (String) imageUrlColumn.get(0).get("DATA_TYPE");
            if (!"text".equalsIgnoreCase(dataType)) {
                System.out.println("警告: image_url字段不是text类型，当前类型为: " + dataType);
                System.out.println("尝试自动修改字段类型为text...");
                
                jdbcTemplate.execute(
                        "ALTER TABLE promotion_image MODIFY COLUMN image_url TEXT NOT NULL COMMENT '图片URL'");
                
                System.out.println("字段类型已修改为text");
            } else {
                System.out.println("image_url字段已经是text类型，不需要修改");
            }
        }
    }
} 