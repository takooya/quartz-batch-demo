package com.takooya.controller;

import com.takooya.quartz.DynamicJob;
import com.takooya.quartz.QuartzManager;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class QuartzManagerController {

    @Autowired
    private QuartzManager quartzManager;

    @GetMapping("/getInfo")
    public Map<String, String> getInfo() throws SchedulerException {
        return quartzManager.getInfo();
    }

    @PostMapping("/addJob")
    public void addJob(@RequestBody Map<String, String> param) {
        String jobName = param.get("jobName");
        String time = param.get("time");
        quartzManager.addJob(jobName, DynamicJob.class, time);
    }

    @PostMapping("/modifyJobTime")
    public void modifyJobTime(@RequestBody Map<String, String> param) {
        String jobName = param.get("jobName");
        String time = param.get("time");
        quartzManager.modifyJobTime(jobName, time);
    }

    @GetMapping("removeJob")
    public void removeJob(@RequestParam String jobName) {
        quartzManager.removeJob(jobName);
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
