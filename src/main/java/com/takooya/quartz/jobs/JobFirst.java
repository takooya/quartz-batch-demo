package com.takooya.quartz.jobs;

import com.takooya.quartz.QuartzJobLauncher;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Slf4j
@Component
public class JobFirst {
    private String jobName = "job1";
    private String cronExpression = "0/15 * * * * ?";
    private String triggerName = "cronTrigger1";

    @Bean
    public JobDetail job1() {
        JobDetailImpl jobDetailImpl = new JobDetailImpl();
        jobDetailImpl.setJobClass(QuartzJobLauncher.class);
        jobDetailImpl.setName(jobName);
        jobDetailImpl.setDurability(true);
        JobDataMap timeout = new JobDataMap();
        timeout.put("timeout", 10);
        jobDetailImpl.setJobDataMap(timeout);
        return jobDetailImpl;
    }

    @Bean
    public CronTrigger cronTrigger1() throws ParseException {
        CronTriggerImpl cronTrigger = new CronTriggerImpl();
        cronTrigger.setJobName(jobName);
        cronTrigger.setCronExpression(cronExpression);
        cronTrigger.setName(triggerName);
        return cronTrigger;
    }
}