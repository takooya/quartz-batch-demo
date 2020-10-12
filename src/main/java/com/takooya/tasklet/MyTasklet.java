package com.takooya.tasklet;

import com.takooya.mybatis.mapper.UserMapper;
import com.takooya.mybatis.dao.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MyTasklet implements Tasklet {

    @Autowired
    private UserMapper userMapper;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<User> users = userMapper.selectAll();
        users.forEach(user1 -> log.info("[-MyTasklet-].execute:users={}", user1));
        return RepeatStatus.FINISHED;
    }
}
