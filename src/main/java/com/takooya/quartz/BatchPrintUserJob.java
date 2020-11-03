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
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class BatchPrintUserJob extends QuartzJobBean {
    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;

    public BatchPrintUserJob(JobLauncher jobLauncher, JobLocator jobLocator) {
        this.jobLauncher = jobLauncher;
        this.jobLocator = jobLocator;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        Job batchDynamicJob;
        JobDataMap quartzData = jobExecutionContext.getMergedJobDataMap();
        String batchJobName = quartzData.getString("jobName");
        try {
            batchDynamicJob = jobLocator.getJob(batchJobName);
            JobParameters batchParameters = new JobParametersBuilder()
                    .addDate("date", new Date())
                    .toJobParameters();
            jobLauncher.run(batchDynamicJob, batchParameters);
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
