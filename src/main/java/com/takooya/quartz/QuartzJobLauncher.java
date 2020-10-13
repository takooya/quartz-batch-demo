package com.takooya.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class QuartzJobLauncher extends QuartzJobBean {
    private String jobName = "myTaskletJob";
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("[-QuartzJobLauncher-].executeInternal:currentTime={}", System.currentTimeMillis());
        Job job1;
        try {
            job1 = jobLocator.getJob(jobName);
            JobParameters jobParameters = new JobParametersBuilder().addDate("date", new Date()).toJobParameters();
            jobLauncher.run(job1, jobParameters);
        } catch (NoSuchJobException |
                JobInstanceAlreadyCompleteException |
                JobRestartException |
                JobParametersInvalidException |
                JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}
