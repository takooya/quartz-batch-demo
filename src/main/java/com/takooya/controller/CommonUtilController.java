package com.takooya.controller;

import com.takooya.common.RequestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author takooya
 */
@Slf4j
@RestController
@RequestMapping("util")
public class CommonUtilController {
    private final ApplicationContext applicationContext;

    public CommonUtilController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping("/getBeanInfo")
    public RequestResult getBeanInfo() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        return RequestResult.success(Arrays.asList(beanDefinitionNames));
    }

    @GetMapping("/getSubClassNames")
    public RequestResult getSubClassNames() {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(Tasklet.class);
        return RequestResult.success(Arrays.asList(beanNamesForType));
    }
}
