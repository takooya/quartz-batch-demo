package com.takooya.mybatis.mapper;

import java.util.List;

import com.takooya.mybatis.dao.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    List<User> select(User user);

    List<User> selectAll();

    int insertOne(User user);
}
