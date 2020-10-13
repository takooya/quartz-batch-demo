package com.takooya.batch.tasklet;

import com.takooya.mybatis.dao.User;
import com.takooya.mybatis.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.job.builder.JobBuilderException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilderException;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@Component
public class MyTasklet implements Tasklet {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MyTasklet myTasklet;

    @Autowired
    private UserMapper userMapper;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<User> users = userMapper.selectAll();
        users.forEach(user1 -> log.info("[-MyTasklet-].execute:users={}", user1));
        return RepeatStatus.FINISHED;
    }

    @Autowired
    @Qualifier("myTaskletStep")
    private Step step;

    @Bean("myTaskletJob")
    public Job myTaskletJob() {
        SimpleJob simpleJob = new SimpleJob();
        simpleJob.setJobRepository(jobRepository);
        simpleJob.setName("myTaskletJob");
        simpleJob.addStep(step);
        try {
            simpleJob.afterPropertiesSet();
        } catch (Exception e) {
            throw new JobBuilderException(e);
        }
        return simpleJob;
    }

    @Bean("myTaskletStep")
    public Step myTaskletStep() {
        TaskletStep taskletStep = new TaskletStep();
        taskletStep.setJobRepository(jobRepository);
        taskletStep.setTasklet(myTasklet);
        taskletStep.setTransactionManager(transactionManager);
        taskletStep.setName("myTaskletStep");
        try {
            taskletStep.afterPropertiesSet();
        } catch (Exception e) {
            throw new StepBuilderException(e);
        }
        return taskletStep;
    }
}
