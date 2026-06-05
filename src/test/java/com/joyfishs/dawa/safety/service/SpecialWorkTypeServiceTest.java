package com.joyfishs.dawa.safety.service;

import com.joyfishs.dawa.safety.entity.SpecialWorkType;
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
 * SpecialWorkTypeService 单元测试
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SpecialWorkTypeServiceTest {

    @Mock
    private com.joyfishs.dawa.safety.mapper.SpecialWorkTypeMapper specialWorkTypeMapper;

    private SpecialWorkTypeService specialWorkTypeService;
    private SpecialWorkType sampleType;

    @BeforeEach
    void setUp() throws Exception {
        specialWorkTypeService = new SpecialWorkTypeService();
        Field baseMapperField = specialWorkTypeService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(specialWorkTypeService, specialWorkTypeMapper);

        sampleType = new SpecialWorkType();
        sampleType.setId(1L);
        sampleType.setWorkTypeCode("ELEVATOR");
        sampleType.setWorkTypeName("电梯作业");
        sampleType.setDangerLevel("high");
        sampleType.setDefaultScore(15);
        sampleType.setStatus("enabled");
    }

    @Test
    void getAllEnabled_shouldReturnAllEnabledTypes() {
        SpecialWorkType type2 = new SpecialWorkType();
        type2.setId(2L);
        type2.setWorkTypeCode("CRANE");
        type2.setWorkTypeName("起重作业");
        type2.setDangerLevel("medium");
        type2.setDefaultScore(12);
        type2.setStatus("enabled");

        when(specialWorkTypeMapper.selectAllEnabled()).thenReturn(Arrays.asList(sampleType, type2));

        List<SpecialWorkType> result = specialWorkTypeService.getAllEnabled();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ELEVATOR", result.get(0).getWorkTypeCode());
    }

    @Test
    void getByWorkTypeCode_shouldReturnCorrectType() {
        when(specialWorkTypeMapper.selectByWorkTypeCode("ELEVATOR")).thenReturn(sampleType);

        SpecialWorkType result = specialWorkTypeService.getByWorkTypeCode("ELEVATOR");

        assertNotNull(result);
        assertEquals("ELEVATOR", result.getWorkTypeCode());
        assertEquals("电梯作业", result.getWorkTypeName());
    }

    @Test
    void getByWorkTypeCode_shouldReturnNullWhenNotFound() {
        when(specialWorkTypeMapper.selectByWorkTypeCode("INVALID")).thenReturn(null);

        SpecialWorkType result = specialWorkTypeService.getByWorkTypeCode("INVALID");

        assertNull(result);
    }
}
