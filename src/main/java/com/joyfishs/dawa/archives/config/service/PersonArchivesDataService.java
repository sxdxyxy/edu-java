package com.joyfishs.dawa.archives.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesData;

import java.util.List;

/**
 * 人员档案数据服务接口
 */
public interface PersonArchivesDataService extends IService<PersonArchivesData> {

    /**
     * 根据人员 ID 和工种查询档案数据列表
     */
    List<PersonArchivesData> getByPersonIdAndWorkType(Long personId, Integer workType);

    /**
     * 根据配置 ID 查询档案数据列表
     */
    List<PersonArchivesData> getByConfigId(Long configId);

    /**
     * 自动从系统中提取匹配要求的人员，生成或更新其档案数据
     */
    int autoSyncDataFromSystem(Long configId, Long orgId, Integer workType);

    /**
     * 创建档案数据
     */
    PersonArchivesData createArchive(Long configId, Long personId, Long userId);


    /**
     * 获取详细的档案数据列表（含人员姓名、安全状态等）
     */
    List<PersonArchivesData> listData(PersonArchivesData query);

    /**
     * 生成档案预览 URL (支持 Word 或 PDF)
     */
    String generatePreviewUrl(Long id, String format);

    /**
     * 生成档案预览二维码
     */
    byte[] generateQrCode(Long id);
}
