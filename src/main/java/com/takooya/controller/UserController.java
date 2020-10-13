package com.takooya.controller;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.takooya.mybatis.mapper.UserMapper;
import com.takooya.mybatis.dao.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    public ApplicationContext applicationContext;

    @GetMapping("/addUser")
    public int addUser(String name, int age) {
        User user = new User();
        user.setAge(age);
        user.setName(name);
        return userMapper.insertOne(user);
    }

    @GetMapping("/getBeanInfo")
    public String getBeanInfo() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        log.info("[-UserController-].getBeanInfo:={}", beanDefinitionNames);
        return String.join(",", beanDefinitionNames);
    }

    @GetMapping("/getSubClassNames")
    public String getSubClassNames() {
        Map<String, Tasklet> beansOfType = applicationContext.getBeansOfType(Tasklet.class);
        beansOfType.forEach((s, tasklet) -> {
            log.info("[-UserController-].accept:key={}", s);
            log.info("[-UserController-].accept:tasklet={}", tasklet.getClass());
            Tasklet bean = applicationContext.getBean(tasklet.getClass());
        });
        log.info("[-UserController-].getSubClassNames:beansOfType={}", beansOfType);
        String[] beanNamesForType = applicationContext.getBeanNamesForType(Tasklet.class);
        log.info("[-UserController-].getSubClassNames:beanNamesForType={}", beanNamesForType);
        return String.join(",", beanNamesForType);
    }
}
