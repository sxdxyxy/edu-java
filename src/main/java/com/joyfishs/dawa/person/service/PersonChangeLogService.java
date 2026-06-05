package com.joyfishs.dawa.person.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.entity.PersonChangeLog;
import com.joyfishs.dawa.person.mapper.PersonChangeLogMapper;
import com.joyfishs.system.enums.YesOrNoState;

/**
 * @author ykfnb
 */
@Service
public class PersonChangeLogService extends ServiceImpl<PersonChangeLogMapper, PersonChangeLog> {

    public void executeSave(Long userId, String changeType, Long orgId, String remark, Long createBy) {
        PersonChangeLog changeLog = new PersonChangeLog();
        changeLog.setUserId(userId);
        changeLog.setChangeType(changeType);
        changeLog.setOrgId(orgId);
        changeLog.setCreateBy(createBy);
        changeLog.setCreateTime(new Date());
        changeLog.setIsDelete(YesOrNoState.NO.getState());
        changeLog.setRemark(remark); // 存放对象的json字符串

        save(changeLog);
    }

    public List<PersonChangeLog> listByUserIdAndOrgId(Long userId, Long orgId) {
        return baseMapper.queryList(userId, orgId);
    }

}
