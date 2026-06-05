package com.joyfishs.dawa.safety.service;

import com.joyfishs.dawa.safety.entity.ViolationTypeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ViolationTypeConfigService 单元测试
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ViolationTypeConfigServiceTest {

    @Mock
    private com.joyfishs.dawa.safety.mapper.ViolationTypeConfigMapper violationTypeConfigMapper;

    private ViolationTypeConfigService violationTypeConfigService;
    private ViolationTypeConfig sampleConfig;

    @BeforeEach
    void setUp() throws Exception {
        violationTypeConfigService = new ViolationTypeConfigService();
        Field baseMapperField = violationTypeConfigService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(violationTypeConfigService, violationTypeConfigMapper);

        sampleConfig = new ViolationTypeConfig();
        sampleConfig.setId(1L);
        sampleConfig.setViolationCode("NO_HELMET");
        sampleConfig.setViolationName("未佩戴安全帽");
        sampleConfig.setViolationLevel("minor");
        sampleConfig.setDeductScore(2);
        sampleConfig.setTriggerTraining(true);
        sampleConfig.setTrainingHours(2);
        sampleConfig.setStatus("enabled");
    }

    @Test
    void getAllEnabled_shouldReturnAllEnabledConfigs() {
        ViolationTypeConfig config2 = new ViolationTypeConfig();
        config2.setId(2L);
        config2.setViolationCode("NO_CERT");
        config2.setViolationName("未持证上岗");
        config2.setViolationLevel("major");
        config2.setDeductScore(6);
        config2.setStatus("enabled");

        when(violationTypeConfigMapper.selectAllEnabled()).thenReturn(Arrays.asList(sampleConfig, config2));

        List<ViolationTypeConfig> result = violationTypeConfigService.getAllEnabled();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("NO_HELMET", result.get(0).getViolationCode());
    }

    @Test
    void getByViolationCode_shouldReturnCorrectConfig() {
        when(violationTypeConfigMapper.selectByViolationCode("NO_HELMET")).thenReturn(sampleConfig);

        ViolationTypeConfig result = violationTypeConfigService.getByViolationCode("NO_HELMET");

        assertNotNull(result);
        assertEquals("NO_HELMET", result.getViolationCode());
        assertEquals(2, result.getDeductScore());
    }

    @Test
    void getByViolationCode_shouldReturnNullWhenNotFound() {
        when(violationTypeConfigMapper.selectByViolationCode("INVALID")).thenReturn(null);

        ViolationTypeConfig result = violationTypeConfigService.getByViolationCode("INVALID");

        assertNull(result);
    }

    @Test
    void getDeductScore_shouldReturnCorrectScore() {
        when(violationTypeConfigMapper.selectByViolationCode("NO_HELMET")).thenReturn(sampleConfig);

        Integer result = violationTypeConfigService.getDeductScore("NO_HELMET");

        assertEquals(2, result);
    }

    @Test
    void getDeductScore_shouldReturnZeroWhenNotFound() {
        when(violationTypeConfigMapper.selectByViolationCode("INVALID")).thenReturn(null);

        Integer result = violationTypeConfigService.getDeductScore("INVALID");

        assertEquals(0, result);
    }
}
