package com.takooya.controller;

import cn.hutool.core.util.StrUtil;
import com.takooya.quartz.QuartzManager;
import com.takooya.quartz.dao.QuartzManagerBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
public class QuartzManagerController {

    @Autowired
    private QuartzManager quartzManager;

    @GetMapping("/getInfo")
    public Map<String, QuartzManagerBean> getInfo()
            throws SchedulerException {
        return quartzManager.getInfo();
    }

    @PostMapping("/addJob")
    public void addJob(@RequestBody @Valid QuartzManagerBean param) {
        quartzManager.addJob(param);
    }

    @PostMapping("/modifyJobTime")
    public Map<String, String> modifyJobTime(@RequestBody Map<String, String> param) {
        String time = param.get("time");
        if (StrUtil.isBlank(time)) {
            throw new RuntimeException("cronExpression不可以为空");
        }
        String jobName = param.get("jobName");
        String triggerName = param.get("triggerName");
        String triggerGroupName = param.get("triggerGroupName");
        if (StrUtil.isAllBlank(jobName, triggerName)) {
            throw new RuntimeException("jobName triggerName不可以同时为空");
        }
        if (StrUtil.isNotBlank(jobName)) {
            quartzManager.modifyJobTime(jobName, time);
        } else {
            quartzManager.modifyJobTime(triggerName, triggerGroupName, time);
        }
        return param;
    }

    @PostMapping("removeJob")
    public void removeJob(@RequestBody QuartzManagerBean qmb) {
        if (StrUtil.isBlank(qmb.getTriggerName())) {
            quartzManager.removeJob(qmb.getJobName(), qmb.getJobGroupName(), qmb.getJobName(), qmb.getJobGroupName());
        } else {
            quartzManager.removeJob(qmb.getJobName(), qmb.getJobGroupName(), qmb.getTriggerName(), qmb.getTriggerGroupName());
        }
    }

    @GetMapping("startJobs")
    public void startJobs() {
        quartzManager.startJobs();
    }

    @GetMapping("shutdownJobs")
    public void shutdownJobs() {
        quartzManager.shutdownJobs();
    }
}
