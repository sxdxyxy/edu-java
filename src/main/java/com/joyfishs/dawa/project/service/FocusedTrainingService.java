package com.joyfishs.dawa.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrainSign;
import com.joyfishs.dawa.project.mapper.ProjectTerminalTrainMapper;
import com.joyfishs.dawa.safety.qrcode.QrCodeStorage;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiaodai
 * @create 2022/1/4 18:37
 */

@Slf4j
@Service
public class FocusedTrainingService extends ServiceImpl<ProjectTerminalTrainMapper, ProjectTerminalTrain> {

    @Autowired
    private PersonService personService;
    @Autowired
    private ProjectTerminalTrainSignService projectTerminalTrainSignService;
    // D6: 二维码存储抽象 (dev-sts 走 Local, prod 走 OSS).
    // 出口处对每个 ProjectTerminalTrain 调 storeAndGetUrl, 把 URL 塞到 signCodeQrCode 扩展字段.
    // 前端 <img :src="row.signCodeQrCode"> 不需改, base64/URL 都能渲染.
    @Autowired
    private QrCodeStorage qrCodeStorage;

    /**
     * 通过用户的ID获取 集中培训
     * @return 集中培训
     */
    public List<ProjectTerminalTrain> findByPerson() {
        Person person = personService.getByUserId(SecurityUtil.getUserId());
        log.info("XmFocusedTrainingService - findByPerson person:{}",person);
        if (person == null || StringUtils.isNull(person.getId())) person = new Person().setId(0L); //throw new CustomException("人员信息有误！");
        //通过人员的组织Id 获取可以参加的培训
        List<ProjectTerminalTrain> terminalTrainList = baseMapper.findByOrgId(person.getId());
        log.info("XmFocusedTrainingService - findByPerson terminalTrainList:{}",terminalTrainList);

        // D6: 出口处给每个培训补 signCodeQrCode (URL 而非 base64)
        fillSignCodeQrCode(terminalTrainList);
        return terminalTrainList;
    }


    /**
     * 获取项目培训详情
     * @param projectId 项目Id
     * @return 培训项目
     */
    public ProjectTerminalTrain findTrainDetail(Long projectId) {
        log.info("XmFocusedTrainingService - findTrainDetail projectId:{}",projectId);

        //查询人员
        Person person = personService.getByUserId(SecurityUtil.getUserId());
        log.info("XmFocusedTrainingService - findByPerson xmPerson:{}", person);
        if (person == null || StringUtils.isNull(person.getId())) throw new CustomException("人员未找到！");

        //查找终端培训
        ProjectTerminalTrain projectTerminalTrain = getById(projectId);
        log.info("XmFocusedTrainingService - findTrainDetail projectTerminalTrain:{}",projectTerminalTrain);
        if (projectTerminalTrain == null || StringUtils.isNull(projectTerminalTrain.getId())) throw new CustomException("人员未找到！");

        //根据 项目ID和 人员ID 查询终端培训报名信息
        ProjectTerminalTrainSign sign = projectTerminalTrainSignService.getSign(projectTerminalTrain.getId(), person.getId());
        log.info("XmFocusedTrainingService - findTrainDetail sign:{}",sign);
        if (sign == null) projectTerminalTrain.setEnrollStatus(2);

        // D6: 详情页要展示签到码二维码
        fillSignCodeQrCode(projectTerminalTrain);

        return projectTerminalTrain;
    }

    /**
     * 批量为培训列表补 signCodeQrCode (URL), signCode 为空则跳过
     */
    private void fillSignCodeQrCode(List<ProjectTerminalTrain> list) {
        if (list == null || list.isEmpty()) return;
        for (ProjectTerminalTrain t : list) {
            fillSignCodeQrCode(t);
        }
    }

    private void fillSignCodeQrCode(ProjectTerminalTrain t) {
        if (t == null || StringUtils.isEmpty(t.getSignCode())) return;
        try {
            String url = qrCodeStorage.storeAndGetUrl(t.getSignCode(), 400, 400, "terminal-train");
            if (StringUtils.isNotEmpty(url)) {
                // signCodeQrCode 是 @TableField(exist=false) 扩展字段, 直接 set 不进 DB
                t.setSignCodeQrCode(url);
            }
        } catch (Exception e) {
            log.warn("fillSignCodeQrCode failed for train id={}", t.getId(), e);
        }
    }

}
