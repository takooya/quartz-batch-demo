package com.takooya.controller;

import cn.hutool.core.util.StrUtil;
import com.takooya.common.RequestResult;
import com.takooya.enums.ResultEnum;
import com.takooya.exception.QuartzManageException;
import com.takooya.quartz.dao.QuartzManagerBean;
import com.takooya.quartz.service.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author takooya
 */
@SuppressWarnings("rawtypes")
@Slf4j
@RestController
@RequestMapping("/quartz")
public class QuartzManagerController {

    private final QuartzService quartzService;

    public QuartzManagerController(QuartzService quartzService) {
        this.quartzService = quartzService;
    }

    @GetMapping("/getInfo")
    public RequestResult getInfo() throws SchedulerException {
        return RequestResult.success(quartzService.getInfo());
    }

    @PostMapping("/addJob")
    public RequestResult addJob(@RequestBody @Valid QuartzManagerBean param) throws SchedulerException {
        QuartzManagerBean quartzManagerBean = quartzService.addJob(param);
        return RequestResult.success(quartzManagerBean);
    }

    @PostMapping("/modifyJob")
    public RequestResult modifyJob(@RequestBody QuartzManagerBean qmb) throws SchedulerException {
        boolean haveTime = StrUtil.isNotBlank(qmb.getTime());
        boolean triggerGroupEmtpy = StrUtil.isBlank(qmb.getTriggerGroupName());
        boolean triggerEmpty = StrUtil.isBlank(qmb.getTriggerName());
        if (haveTime && (triggerGroupEmtpy || triggerEmpty)) {
            throw new QuartzManageException(ResultEnum.SHORTAGE_MODIFY_TRIGGER_PARAM);
        }
        qmb = quartzService.modifyJob(qmb);
        return RequestResult.success(qmb);
    }

    @PostMapping("removeJob")
    public RequestResult removeJob(@RequestBody QuartzManagerBean qmb) throws SchedulerException {
        if (!StrUtil.isAllNotBlank(qmb.getJobName(), qmb.getJobGroupName(), qmb.getTriggerName(), qmb.getTriggerGroupName())) {
            throw new QuartzManageException(ResultEnum.SHORTAGE_QUARTZ_REMOVE_PARAM);
        }
        qmb = quartzService.removeJob(qmb);
        return RequestResult.success(qmb);
    }

    @GetMapping("standbyJobs")
    public RequestResult standbyJobs() throws SchedulerException {
        quartzService.standbyJobs();
        return RequestResult.success(null);
    }

    @GetMapping("startJobs")
    public RequestResult startJobs() throws SchedulerException {
        quartzService.startJobs();
        return RequestResult.success(null);
    }

    @GetMapping("shutdownJobs")
    public RequestResult shutdownJobs() throws SchedulerException {
        quartzService.shutdownJobs();
        return RequestResult.success(null);
    }
}
