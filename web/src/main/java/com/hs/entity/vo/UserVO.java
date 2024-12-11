package com.hs.entity.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserVO {
    // 用户ID
    private Long userId;
    // 用户名
    private String userName;
    private String pwd;
    // 用户类型: 'admin'管理员
    private String userType;
    // 真实姓名
    private String realName;
    // 手机号
    private String phoneNum;
    // 邮箱
    private String email;
    // 性别
    private String gender;
    // 出生日期
    private String birthdate;
    // 用户注册时间
    private String createTime;
    // 最后登录时间
    private String lastLoginTime;
}
