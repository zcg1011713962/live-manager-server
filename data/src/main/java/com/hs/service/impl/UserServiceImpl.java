package com.hs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.db.entity.User;
import com.hs.db.mapper.UserMapper;
import com.hs.entity.LogicResponse;
import com.hs.entity.bo.UserBO;
import com.hs.entity.dto.UserDTO;
import com.hs.enums.ErrorCode;
import com.hs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public CompletableFuture<LogicResponse<UserBO>> searchUser(UserDTO userDTO) {
        return CompletableFuture.supplyAsync(()->{
            // 构建查询条件
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name", userDTO.getUserName())  // 如果用户名不为空，添加条件
                    .eq("pwd", userDTO.getPwd());  // 如果年龄不为空，添加条件
            User user = userMapper.selectOne(queryWrapper);
            if (user == null) {
                return LogicResponse.<UserBO>builder().status(ErrorCode.FAILED).build();
            }
            UserBO userBO = UserBO.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .email(user.getEmail())
                    .realName(user.getRealName())
                    .build();
            return LogicResponse.<UserBO>builder().status(ErrorCode.SUCCESS).data(userBO).build();
        });
    }
}
