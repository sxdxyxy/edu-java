package com.joyfishs.dawa.course.controller;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.joyfishs.dawa.course.domain.vo.VodPlaySignVo;
import com.joyfishs.dawa.course.entity.Courseware;
import com.joyfishs.dawa.course.service.CourseCoursewareService;
import com.joyfishs.dawa.course.service.VodService;
import com.joyfishs.system.domain.R;
import com.joyfishs.utils.StringUtils;
import com.tencentcloudapi.vod.v20180717.models.FileUploadTask;
import com.tencentcloudapi.vod.v20180717.models.MediaMetaData;
import com.tencentcloudapi.vod.v20180717.models.MediaProcessTaskResult;
import com.tencentcloudapi.vod.v20180717.models.ProcedureTask;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "腾讯Vod服务")
@Slf4j
@RequiredArgsConstructor
@RestController
public class TencentVodController {
    private final CourseCoursewareService courseCoursewareService;
    private final VodService vodService;
    @ApiOperation(value = "接收事件通知回调")
    @PostMapping("/vodEventReceive")
    public R<Void> callBack(@RequestBody String jsonStr) {
        JsonObject jsonObj = JsonParser.parseString(jsonStr).getAsJsonObject();
        String eventType = jsonObj.get("EventType").getAsString();
        if (StringUtils.equals("NewFileUpload", eventType)) {
            FileUploadTask task = new Gson().fromJson(jsonObj.get("FileUploadEvent"), FileUploadTask.class);
            fileUploadEventHandle(task);
        }
        if (StringUtils.equals("ProcedureStateChanged", eventType)) {
            ProcedureTask task = new Gson().fromJson(jsonObj.get("ProcedureStateChangeEvent"), ProcedureTask.class);
            procedureEventHandle(task);
        }
        return R.ok();
    }

    @ApiOperation(value = "获取播放器播放签名")
    @GetMapping("/getPlaySign/{fileId}")
    public R<VodPlaySignVo> getPlaySign(@NotNull(message = "fileId不能为空") @PathVariable String fileId) {
        String token = vodService.getPlaySignature(fileId);
        VodPlaySignVo result = new VodPlaySignVo();
        result.setPsign(token);
        result.setFileID(fileId);
        result.setAppID(vodService.getSubAppId());
        return R.ok(result);
    }

    private void fileUploadEventHandle(FileUploadTask task){
        String coursewareId = task.getMediaBasicInfo().getSourceInfo().getSourceContext();
        Courseware courseware =  courseCoursewareService.getById(Long.valueOf(coursewareId));
        courseware.setFileId(task.getFileId());
        courseware.setVideoPath(task.getMediaBasicInfo().getMediaUrl());
        courseware.setFileExtension(task.getMediaBasicInfo().getType());
        MediaMetaData metaData = task.getMetaData();
        if(ObjectUtil.isNotNull(metaData)){
            courseware.setDuration(metaData.getDuration().longValue());
            courseware.setSizeStr(metaData.getSize().toString());
        }
        courseCoursewareService.updateById(courseware);
    }

    private void procedureEventHandle(ProcedureTask task) {
        log.info("收到转码完成通知，任务id：{},文件id：{}", task.getTaskId(), task.getFileId());
        Courseware courseware =  courseCoursewareService.findByFileId(task.getFileId());
        courseware.setTranscode(Boolean.TRUE);
        courseware.setUpdateTime(new Date());
        MediaProcessTaskResult[] taskResults = task.getMediaProcessResultSet();
        if (ArrayUtil.isNotEmpty(taskResults)) {
            for (MediaProcessTaskResult taskResult : taskResults) {
                if (StringUtils.equals("CoverBySnapshot", taskResult.getType())) {
                    log.info("设置封面图，fileId：{},url：{}", task.getFileId(), taskResult.getCoverBySnapshotTask().getOutput().getCoverUrl());
                    courseware.setCoverPath(taskResult.getCoverBySnapshotTask().getOutput().getCoverUrl());
                }
                if (StringUtils.equals("AdaptiveDynamicStreaming", taskResult.getType())) {
                    log.info("自适应码流转换完成，fileId：{},url：{}", task.getFileId(), taskResult.getAdaptiveDynamicStreamingTask().getOutput().getUrl());
                    courseware.setM3u8(taskResult.getAdaptiveDynamicStreamingTask().getOutput().getUrl());
                }
            }
        }
        courseCoursewareService.updateById(courseware);
    }
}
