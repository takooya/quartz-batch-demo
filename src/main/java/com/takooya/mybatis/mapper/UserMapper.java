package com.takooya.mybatis.mapper;

import com.takooya.mybatis.dao.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    List<User> select(User user);

    List<User> selectAll();

    int insertOne(User user);

    int updateOne(User user);
}
