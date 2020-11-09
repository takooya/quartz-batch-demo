package com.takooya.batch;

import com.takooya.batch.tasklet.PrintUserTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author takooya
 */
@Configuration
@Slf4j
public class PrintUserConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PrintUserTasklet printUserTasklet;

    public PrintUserConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, PrintUserTasklet printUserTasklet) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.printUserTasklet = printUserTasklet;
    }

    @Bean
    public Job printUserJobs() {
        return jobBuilderFactory.get("printUserJob")
                .start(printUserSteps())
                .build();
    }

    //  tasklet下述框架无法监控
    //  batch-web-spring-boot-autoconfigure-2.0.2.RELEASE.jar
    @Bean
    public Step printUserSteps() {
        return stepBuilderFactory.get("printUserStep")
                .tasklet(printUserTasklet)
                .build();
    }
}
