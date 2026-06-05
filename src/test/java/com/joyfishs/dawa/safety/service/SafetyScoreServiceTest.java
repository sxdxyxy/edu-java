package com.joyfishs.dawa.safety.service;

import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.safety.entity.SafetyScoreAccount;
import com.joyfishs.dawa.safety.entity.ViolationTypeConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SafetyScoreService 单元测试
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@ExtendWith(MockitoExtension.class)
class SafetyScoreServiceTest {

    @Mock
    private com.joyfishs.dawa.safety.mapper.SafetyScoreAccountMapper safetyScoreAccountMapper;

    @Mock
    private com.joyfishs.dawa.safety.mapper.ViolationTypeConfigMapper violationTypeConfigMapper;

    @Mock
    private SafetyScoreAccountService safetyScoreAccountService;

    @Mock
    private SafetyRetrainingService safetyRetrainingService;

    @InjectMocks
    private SafetyScoreService safetyScoreService;

    @Test
    void getSafetyCodeColor_shouldReturnGreenWhenScoreHigh() {
        SafetyScoreAccount account = new SafetyScoreAccount();
        account.setPersonId(100L);
        account.setWorkType("worker");
        account.setCurrentScore(12);

        when(safetyScoreAccountService.getColorByPersonId(100L)).thenReturn("GREEN");

        String color = safetyScoreService.getSafetyCodeColor(100L);

        assertEquals("GREEN", color);
    }

    @Test
    void getCurrentScore_shouldReturnScore() {
        SafetyScoreAccount account = new SafetyScoreAccount();
        account.setPersonId(100L);
        account.setCurrentScore(10);

        when(safetyScoreAccountService.getByPersonId(100L)).thenReturn(account);

        Integer score = safetyScoreService.getCurrentScore(100L);

        assertEquals(10, score);
    }

    @Test
    void getCurrentScore_shouldReturnNullWhenNoAccount() {
        when(safetyScoreAccountService.getByPersonId(999L)).thenReturn(null);

        Integer score = safetyScoreService.getCurrentScore(999L);

        assertNull(score);
    }
}
