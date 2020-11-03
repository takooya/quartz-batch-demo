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
public class PrintUserTasklet implements Tasklet {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PrintUserTasklet printUserTasklet;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Step step;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<User> users = userMapper.selectAll();
        log.info("[-PrintUserTasklet-].execute:users={}", users);
        return RepeatStatus.FINISHED;
    }

    @Bean
    public Job printUserJob() {
        return jobBuilderFactory.get("printUserJob")
                .start(step)
                .build();
    }

    @Bean
    public Step printUserStep() {
        return stepBuilderFactory.get("printUserStep")
                .tasklet(printUserTasklet)
                .build();
    }
}
