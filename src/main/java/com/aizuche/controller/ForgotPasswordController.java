package com.aizuche.controller;

import com.aizuche.entity.User;
import com.aizuche.service.UserService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public Result forgotPassword(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return Result.error("请输入用户名");
        }

        User user = userService.findByUsername(username.trim()).orElse(null);
        if (user == null) {
            return Result.error("该用户名不存在，请检查输入或联系管理员");
        }

        // 重置密码
        String tempPassword = userService.resetPassword(user.getId());
        if (tempPassword == null) {
            return Result.error("密码重置失败，请联系管理员");
        }

        return Result.success("密码已重置为: " + tempPassword + "，请使用临时密码登录后及时修改密码");
    }
}
