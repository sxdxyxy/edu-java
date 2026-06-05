package com.joyfishs.dawa.course.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.course.domain.bo.CoursewareBo;
import com.joyfishs.dawa.course.domain.vo.CoursewareVo;
import com.joyfishs.dawa.course.entity.Courseware;
import com.joyfishs.dawa.course.mapper.CoursewareMapper;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.service.SysUserService;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课件基本信息表 服务实现类
 * </p>
 *
 * @author xiaodai
 * @since 2021-08-24
 */
@Slf4j
@Service
public class CourseCoursewareService extends ServiceImpl<CoursewareMapper, Courseware> {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private CourseRelationCoursewareService courseRelationCoursewareService;

    /**
     * 课件新增 或 修改
     * @param bo 课件
     */
    @Transactional
    public Courseware addOrUpdate(CoursewareBo bo) throws Exception {
        String fileExtension = null;
        if (StringUtils.isNotEmpty(bo.getFileUrl())) {
            fileExtension = FileNameUtil.extName(bo.getFileUrl());
        }
        Courseware courseware = getById(bo.getId());
        //新增
        if (courseware == null || StringUtils.isNull(courseware.getId())){
            courseware = new Courseware();
            BeanUtil.copyProperties(bo, courseware);
            courseware.setIfResources(bo.isResources() ? 1 : 0);
            courseware.setSizeStr(bo.getSizeStr());
            courseware.setFileExtension(fileExtension);
            courseware.setVideoPath(bo.getFileUrl());
            courseware.setIsDelete(YesOrNoState.NO.getState());
            courseware.setCreateTime(new Date());
            courseware.setCreateBy(SecurityUtil.getUserId());
            save(courseware);
            //新增 课件 和 课程 关联关系
            courseRelationCoursewareService.saveRelation(courseware.getId(),bo.getCourseId());
        }else {//修改
            BeanUtil.copyProperties(bo, courseware);
            courseware.setVideoPath(bo.getFileUrl());
            courseware.setFileExtension(fileExtension);
            if (StringUtils.isNotEmpty(bo.getSizeStr())) {
                courseware.setSizeStr(bo.getSizeStr());
            }
            courseware.setUpdateTime(new Date());
            courseware.setUpdateBy(SecurityUtil.getUserId());
            updateById(courseware);
        }
        return courseware;
    }


    /**
     * 删除课件
     * @param id 课件ID
     * 此处不设删除关联关系
     */
    @Transactional
    public void deleteCourseware(Long id) {
        if (StringUtils.isNull(id)) {
            throw new CustomException("课件ID不能为空！");
        }

        Courseware courseware = getById(id);
        if (courseware == null || StringUtils.isNull(courseware.getId())) {
            throw new CustomException("未找到课件！");
        }

        //删除课件
        courseware.setIsDelete(YesOrNoState.YES.getState());
        courseware.setDeleteTime(new Date());
        courseware.setDeleteBy(SecurityUtil.getUserId());

        //删除课件关联表
        baseMapper.deleteCoursewarexmRelationById(courseware.getId());

        updateById(courseware);
    }


    /**
     * 通过Id 获取课件详情
     * @return 课件详情
     */
    public CoursewareVo getDetailById(Long id, Integer isEdit, Integer isDown){
        if (StringUtils.isNull(id)) {
            throw new CustomException("课件ID不能为空！");
        }

        Courseware courseware = getById(id);
        if (courseware == null || StringUtils.isNull(courseware.getId())) {
            throw new CustomException("未找到课件信息！");
        }

        if (StringUtils.isNull(isEdit)) {
            isEdit = 1;
        }
        if (0 == isEdit) {//浏览量增加
            courseware.setViewCount(courseware.getViewCount()+1);
            updateById(courseware);
        }

        if (StringUtils.isNull(isDown)) {
            isDown = 0;
        }
        if (1 == isDown) {
            courseware.setDownCount(courseware.getDownCount()+1);
            updateById(courseware);
        }
        CoursewareVo result = new CoursewareVo();
        BeanUtil.copyProperties(courseware,result);
        result.setFileUrl(courseware.getVideoPath());
        result.setThirdParty(ObjUtil.isNotNull(courseware.getThirdPartyId()));
        result.setFileUrl(courseware.getM3u8());
        // 设置发布人名称
        result.setPublishName(getUserName(courseware.getCreateBy()));
        return result;
    }

    /**
     * 通过课程Id获取课件集合
     * @param id 课程ID
     * @return 课件集合
     */
    public List<CoursewareVo> getCoursewareList(Long id) {
        //通过课程ID获取课件ID
        List<Long> coursewareIdList = courseRelationCoursewareService.getCoursewareRelationByCourseId(id);

        //如果为空的  直接返回，否则下面的in语句会报错
        if (coursewareIdList == null || coursewareIdList.size() == 0) {
            return new ArrayList<>();
        }

        //通过课件ID获取课件
        LambdaQueryWrapper<Courseware> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Courseware::getId,coursewareIdList);
        queryWrapper.eq(Courseware::getIsDelete,YesOrNoState.NO.getState());
        queryWrapper.eq(Courseware::getIfResources, YesOrNoState.NO.getState());
        List<Courseware> list = baseMapper.selectList(queryWrapper);
        List<CoursewareVo> result = list.stream().map(item -> {
            CoursewareVo vo = new CoursewareVo();
            BeanUtil.copyProperties(item, vo);
            if (vo.isVideo()) {
                vo.setTypeName("视频");
            }
            if (vo.isDoc()) {
                vo.setTypeName("文档");
            }
            return vo;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * 获取课件---资源库
     * @param type
     * @return
     */
    public List<CoursewareVo> findResourceList(Integer type) {
        List<CoursewareVo> coursewareList = baseMapper.findResourceList(type);
        for (CoursewareVo courseware : coursewareList) {
            if (courseware.isVideo()) {
                courseware.setTypeName("视频");
            }
            if (courseware.isDoc()) {
                courseware.setTypeName("文档");
            }
            
            // 如果大小为空，且有 URL，后台尝试获取一次大小并更新数据库
            if (StringUtils.isEmpty(courseware.getSizeStr()) && StringUtils.isNotEmpty(courseware.getFileUrl())) {
                try {
                    long length = getResourceLength(courseware.getFileUrl());
                    if (length > 0) {
                        String formattedSize = formatSize(length);
                        courseware.setSizeStr(formattedSize);
                        
                        // 更新数据库，避免下次重复获取
                        Courseware entity = new Courseware();
                        entity.setId(courseware.getId());
                        entity.setSizeStr(formattedSize);
                        updateById(entity);
                    }
                } catch (Exception e) {
                    log.error("获取资源文件大小时出错: " + courseware.getFileUrl(), e);
                }
            }
        }
        return coursewareList;
    }

    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new java.text.DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public Courseware findByFileId(String fileId) {
        LambdaQueryWrapper<Courseware> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Courseware::getFileId,fileId);
        return this.getOne(queryWrapper);
    }

    /**
     * 获取用户姓名
     * @return
     */
    private String getUserName(Long userId){
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUser::getName);
        queryWrapper.eq(SysUser::getId,userId);
        SysUser sysUser = sysUserService.getOne(queryWrapper);

        return sysUser.getName();
    }

    /**
     * 给定url地址获取资源的大小（以字节为单位）
     * @param urlStr
     * @return
     * @throws IOException
     */
    public long getResourceLength(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        URLConnection urlConnection=url.openConnection();
        urlConnection.connect();
        //返回响应报文头字段Content-Length的值
        return urlConnection.getContentLength();
    }
    Courseware selectByThirdPartyId(Long thirdPartyId) {
        return baseMapper.selectByThirdPartyId(thirdPartyId);
    }

    /**
     * 保存或更新三方课件
     * @param c
     * @return
     */
    public Courseware saveOrUpdateThirdParty(Courseware c,Long courseId){
        String fileExtension = null;
        if (StringUtils.isNotEmpty(c.getVideoPath())) {
            fileExtension = FileNameUtil.extName(c.getVideoPath());
        }
        c.setFileExtension(fileExtension);
        Courseware courseware = selectByThirdPartyId(c.getThirdPartyId());
        //新增
        if (ObjUtil.isNull(courseware)){
            courseware = c;
            courseware.setIfResources(0);
            courseware.setIsDelete(YesOrNoState.NO.getState());
            courseware.setCreateTime(new Date());
            courseware.setCreateBy(SecurityUtil.getUserId());
            save(courseware);
            //新增 课件 和 课程 关联关系
            courseRelationCoursewareService.saveRelation(courseware.getId(),courseId);
        }else {//修改
            BeanUtil.copyProperties(c, courseware, CopyOptions.create().setIgnoreNullValue(true));
            courseware.setUpdateTime(new Date());
            courseware.setUpdateBy(SecurityUtil.getUserId());
            updateById(courseware);
        }
        return courseware;
    }

    public void addViewCount(Long coursewareId) {
        baseMapper.addViewCount(coursewareId);
    }
}
