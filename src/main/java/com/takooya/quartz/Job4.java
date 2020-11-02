package com.takooya.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
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
public class Job4 extends QuartzJobBean {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        Job job1;
        JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
        String jobName = mergedJobDataMap.getString("jobName");
        try {
            job1 = jobLocator.getJob(jobName);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("date", new Date())
                    .toJobParameters();
            jobLauncher.run(job1, jobParameters);
        } catch (NullPointerException | NoSuchJobException e) {
            log.info("[-Job4-].executeInternal:{}无相关batch job", jobExecutionContext.getJobDetail());
        } catch (JobInstanceAlreadyCompleteException |
                JobRestartException |
                JobParametersInvalidException |
                JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}
