package com.takooya.batch.tasklet;

import com.takooya.mybatis.dao.User;
import com.takooya.mybatis.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author takooya
 */
@Slf4j
@Component
public class PrintUserTasklet implements Tasklet {
    private final UserMapper userMapper;

    public PrintUserTasklet(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("[-PrintUserTasklet-].execute:userMapper={}", userMapper);
        List<User> users = userMapper.selectAll();
        log.info("[-PrintUserTasklet-].execute:users={}", users);
        return RepeatStatus.FINISHED;
    }
}
