package com.joyfishs.dawa.safety.entity;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * D24: 防止 mybatis-plus.db-column-underline=false 误用导致驼峰字段作为列名使用
 * (因为全局配置 db-column-underline=false,任何缺少 @TableField 的 camelCase 字段
 *  都会被 MyBatis-Plus 当作列名使用,生成 "Unknown column 'isDelete'" 之类的错误)
 *
 * 验证 SafetyScoreAccount 实体的所有审计字段都显式映射到了正确的 snake_case 列名。
 *
 * 用 MybatisConfiguration 触发 MyBatis-Plus 内部的 TableInfo 初始化
 * (无需启动 Spring 容器或连真实数据库)。
 */
class SafetyScoreAccountMappingTest {

    @BeforeAll
    static void initTableInfo() {
        // 用 H2 内存数据源,纯为通过 Environment 校验,不实际执行 SQL
        org.h2.jdbcx.JdbcDataSource ds = new org.h2.jdbcx.JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        org.apache.ibatis.transaction.managed.ManagedTransactionFactory txFactory =
                new org.apache.ibatis.transaction.managed.ManagedTransactionFactory();
        org.apache.ibatis.mapping.Environment env =
                new org.apache.ibatis.mapping.Environment("test", txFactory, ds);
        MybatisConfiguration cfg = new MybatisConfiguration();
        cfg.setEnvironment(env);
        org.apache.ibatis.builder.MapperBuilderAssistant assistant =
                new org.apache.ibatis.builder.MapperBuilderAssistant(cfg, "");
        TableInfoHelper.initTableInfo(assistant, SafetyScoreAccount.class);
    }

    @Test
    void all_audit_fields_map_to_snake_case_columns() {
        TableInfo info = TableInfoHelper.getTableInfo(SafetyScoreAccount.class);
        assertNotNull(info, "TableInfo must be initialized");

        Map<String, String> propertyToColumn = info.getFieldList().stream()
                .collect(Collectors.toMap(TableFieldInfo::getProperty, TableFieldInfo::getColumn));

        // D24: 这些字段若没有 @TableField 注解,列名会等于字段名(camelCase),写入时 SQL 报错
        assertEquals("create_by", propertyToColumn.get("createBy"));
        assertEquals("create_time", propertyToColumn.get("createTime"));
        assertEquals("update_by", propertyToColumn.get("updateBy"));
        assertEquals("update_time", propertyToColumn.get("updateTime"));
        assertEquals("is_delete", propertyToColumn.get("isDelete"));
        assertEquals("delete_by", propertyToColumn.get("deleteBy"));
        assertEquals("delete_time", propertyToColumn.get("deleteTime"));
        assertEquals("delete_reason", propertyToColumn.get("deleteReason"));
        assertEquals("remark", propertyToColumn.get("remark"));
        assertEquals("created_at", propertyToColumn.get("createdAt"));
        assertEquals("updated_at", propertyToColumn.get("updatedAt"));
    }

    @Test
    void color_is_a_transient_field_not_a_column() {
        TableInfo info = TableInfoHelper.getTableInfo(SafetyScoreAccount.class);
        assertNotNull(info);

        // color 用 @TableField(exist = false) 标记,不应出现在字段列表中
        boolean colorMapped = info.getFieldList().stream()
                .anyMatch(f -> "color".equals(f.getProperty()));
        assertFalse(colorMapped,
                "color 是 @TableField(exist=false) 瞬时字段,不应被映射为列");
    }
}
