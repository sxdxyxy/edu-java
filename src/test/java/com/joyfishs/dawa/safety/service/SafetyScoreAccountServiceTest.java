package com.joyfishs.dawa.safety.service;

import com.joyfishs.dawa.safety.entity.SafetyScoreAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SafetyScoreAccountService 单元测试
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SafetyScoreAccountServiceTest {

    @Mock
    private com.joyfishs.dawa.safety.mapper.SafetyScoreAccountMapper safetyScoreAccountMapper;

    private SafetyScoreAccountService safetyScoreAccountService;

    @BeforeEach
    void setUp() throws Exception {
        safetyScoreAccountService = new SafetyScoreAccountService();
        // 通过反射注入 baseMapper
        Field baseMapperField = safetyScoreAccountService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(safetyScoreAccountService, safetyScoreAccountMapper);
    }

    @Test
    void getDefaultInitialScore_forWorker_shouldReturn12() {
        int score = safetyScoreAccountService.getDefaultInitialScore("worker");
        assertEquals(12, score);
    }

    @Test
    void getDefaultInitialScore_forSpecialized_shouldReturn12() {
        int score = safetyScoreAccountService.getDefaultInitialScore("specialized");
        assertEquals(12, score);
    }

    @Test
    void getDefaultInitialScore_forSafetyAdmin_shouldReturn10() {
        int score = safetyScoreAccountService.getDefaultInitialScore("safety_admin");
        assertEquals(10, score);
    }

    @Test
    void getByPersonId_shouldReturnAccount() {
        SafetyScoreAccount account = new SafetyScoreAccount();
        account.setId(1L);
        account.setPersonId(100L);
        account.setWorkType("worker");
        account.setCurrentScore(12);

        when(safetyScoreAccountMapper.selectByPersonId(100L)).thenReturn(account);

        SafetyScoreAccount result = safetyScoreAccountService.getByPersonId(100L);

        assertNotNull(result);
        assertEquals(100L, result.getPersonId());
        verify(safetyScoreAccountMapper, times(1)).selectByPersonId(100L);
    }

    @Test
    void getByPersonId_shouldReturnNullWhenNotFound() {
        when(safetyScoreAccountMapper.selectByPersonId(999L)).thenReturn(null);

        SafetyScoreAccount result = safetyScoreAccountService.getByPersonId(999L);

        assertNull(result);
    }
}
