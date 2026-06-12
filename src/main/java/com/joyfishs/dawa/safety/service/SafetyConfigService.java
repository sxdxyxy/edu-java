package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.entity.SafetyConfig;
import com.joyfishs.dawa.safety.mapper.SafetyConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全配置服务
 * <p>
 * 提供按 group / key 维度的读 + 类型转换 + 缓存.
 * 写入只允许 is_editable=1 的项, 防止覆盖系统内置阈值.
 * </p>
 *
 * @author safe-edu
 * @since 2026-06-10
 */
@Slf4j
@Service
public class SafetyConfigService extends ServiceImpl<SafetyConfigMapper, SafetyConfig> {

    /**
     * 按 group 查全部, 返回 Map<key, value> 方便前端一次性消费
     */
    public Map<String, String> getByGroup(String group) {
        if (group == null || group.isEmpty()) return Collections.emptyMap();
        QueryWrapper<SafetyConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("config_group", group);
        List<SafetyConfig> list = list(wrapper);
        Map<String, String> result = new HashMap<>(list.size());
        for (SafetyConfig c : list) {
            result.put(c.getConfigKey(), c.getConfigValue());
        }
        return result;
    }

    /**
     * 单 key 查, 找不到返回 null
     */
    public String getString(String key) {
        SafetyConfig c = getOne(new QueryWrapper<SafetyConfig>().eq("config_key", key));
        return c != null ? c.getConfigValue() : null;
    }

    /**
     * 按 valueType 转 int. 找不到或解析失败时返回 fallback.
     * 不抛异常, 避免脏数据让整个流程崩.
     */
    public int getInt(String key, int fallback) {
        SafetyConfig c = getOne(new QueryWrapper<SafetyConfig>().eq("config_key", key));
        if (c == null || c.getConfigValue() == null) return fallback;
        try {
            return Integer.parseInt(c.getConfigValue().trim());
        } catch (NumberFormatException e) {
            log.warn("配置值非整数: key={}, value={}, fallback={}", key, c.getConfigValue(), fallback);
            return fallback;
        }
    }

    /**
     * 按 valueType 转逗号分隔字符串数组 (用于 role.admin.excluded-match 这类)
     */
    public List<String> getStringList(String key) {
        String raw = getString(key);
        if (raw == null || raw.isEmpty()) return Collections.emptyList();
        return Arrays.asList(raw.split("\\s*,\\s*"));
    }

    /**
     * 更新单条. 拒绝 is_editable=0 的系统内置项.
     */
    public boolean updateValue(String key, String newValue) {
        SafetyConfig c = getOne(new QueryWrapper<SafetyConfig>().eq("config_key", key));
        if (c == null) {
            log.warn("更新配置键不存在: {}", key);
            return false;
        }
        if (Boolean.FALSE.equals(c.getIsEditable())) {
            log.warn("配置键为系统内置, 不允许修改: {}", key);
            return false;
        }
        c.setConfigValue(newValue);
        return updateById(c);
    }
}
