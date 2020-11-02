package com.takooya.batch.tasklet;

import com.takooya.mybatis.dao.User;
import com.takooya.mybatis.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MyTasklet implements Tasklet {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MyTasklet myTasklet;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Step step;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<User> users = userMapper.selectAll();
        log.info("[-MyTasklet-].execute:users={}", users);
        return RepeatStatus.FINISHED;
    }

    @Bean
    public Job myTaskletJob() {
        return jobBuilderFactory.get("myTaskletJob")
                .start(step)
                .build();
    }

    @Bean
    public Step myTaskletStep() {
        return stepBuilderFactory.get("myTaskletStep")
                .tasklet(myTasklet)
                .build();
    }
}
