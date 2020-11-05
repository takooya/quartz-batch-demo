package com.takooya.controller;

import com.takooya.common.RequestResult;
import com.takooya.quartz.service.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("rawtypes")
@Slf4j
@RestController
@RequestMapping("/quartz")
public class QuartzOriginController {
    @Autowired
    private QuartzService quartzService;

    @GetMapping("/getCurrentlyExecutingJobs")
    public RequestResult getCurrentlyExecutingJobs() throws SchedulerException {
        return RequestResult.success(quartzService.getCurrentlyExecutingJobs());
    }
}
