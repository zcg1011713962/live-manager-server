package com.hs.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.db.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    void insertBatchUsers(@Param("users") List<User> users);

    List<Long> selectUserIds(List<User> users);
}
