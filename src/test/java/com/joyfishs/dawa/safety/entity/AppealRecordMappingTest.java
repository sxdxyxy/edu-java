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
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * D24: 防止 mybatis-plus.db-column-underline=false 误用导致驼峰字段作为列名使用
 *
 * 验证 AppealRecord 实体的所有字段都显式映射到了正确的 snake_case 列名。
 * V8 创建的 t_safety_appeal_record 有 14 列;V20260612 又补了 created_at / create_by /
 * update_by / is_delete 并将 update_time rename 为 updated_at。实体必须与之匹配。
 */
class AppealRecordMappingTest {

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
        TableInfoHelper.initTableInfo(assistant, AppealRecord.class);
    }

    @Test
    void all_camel_case_fields_map_to_snake_case_columns() {
        TableInfo info = TableInfoHelper.getTableInfo(AppealRecord.class);
        assertNotNull(info, "TableInfo must be initialized");

        Map<String, String> propertyToColumn = info.getFieldList().stream()
                .collect(Collectors.toMap(TableFieldInfo::getProperty, TableFieldInfo::getColumn));

        // V8 原有字段
        assertEquals("violation_record_id", propertyToColumn.get("violationRecordId"));
        assertEquals("person_id", propertyToColumn.get("personId"));
        assertEquals("appeal_reason", propertyToColumn.get("appealReason"));
        assertEquals("appeal_evidence", propertyToColumn.get("appealEvidence"));
        assertEquals("appeal_time", propertyToColumn.get("appealTime"));
        assertEquals("reviewer_id", propertyToColumn.get("reviewerId"));
        assertEquals("review_result", propertyToColumn.get("reviewResult"));
        assertEquals("review_comment", propertyToColumn.get("reviewComment"));
        assertEquals("review_time", propertyToColumn.get("reviewTime"));
        assertEquals("score_restored", propertyToColumn.get("scoreRestored"));
        assertEquals("score_restored_at", propertyToColumn.get("scoreRestoredAt"));

        // V20260612 补的列 (rename + 3 audit)
        assertEquals("created_at", propertyToColumn.get("createdAt"));
        assertEquals("updated_at", propertyToColumn.get("updatedAt"));
        assertEquals("create_by", propertyToColumn.get("createBy"));
        assertEquals("update_by", propertyToColumn.get("updateBy"));
        assertEquals("is_delete", propertyToColumn.get("isDelete"));
    }
}
