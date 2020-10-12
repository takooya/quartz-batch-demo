import com.takooya.DemoApplication;
import com.takooya.mybatis.mapper.UserMapper;
import com.takooya.mybatis.dao.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class Test {
    @Autowired
    private UserMapper userMapper;

    @Autowired(required = false)
    public DataSourceTransactionManager transactionManager;

    @org.junit.Test
    public void getUser() {
        log.info("[-Test-].getUser:={}", userMapper);
        List<User> select = userMapper.select(new User(0));
        log.info("[-Test-].getUser:={}", select);
        log.info("[-Test-].dataSourceTransactionManager:={}", transactionManager);
    }
}
