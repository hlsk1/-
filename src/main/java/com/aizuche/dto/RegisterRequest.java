package com.aizuche.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;   // 登录用户名（手机号）
    private String password;   // 登录密码
    private String name;       // 真实姓名
    private String phone;      // 手机号
    private String idCard;     // 身份证号
    private String license;    // 驾驶证号
    private String remark;     // 备注
}
