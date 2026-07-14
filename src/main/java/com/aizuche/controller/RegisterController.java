package com.aizuche.controller;

import com.aizuche.dto.RegisterRequest;
import com.aizuche.entity.Customer;
import com.aizuche.entity.User;
import com.aizuche.service.CustomerService;
import com.aizuche.service.UserService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return Result.error("请输入用户名");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Result.error("请输入密码");
        }
        if (request.getPassword().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return Result.error("请输入真实姓名");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return Result.error("请输入手机号");
        }

        String username = request.getUsername().trim();
        String phone = request.getPhone().trim();

        // 检查用户名是否已存在
        if (userService.findByUsername(username).isPresent()) {
            return Result.error("用户名已存在，请更换");
        }

        // 检查手机号是否已被注册为客户
        if (customerService.findByPhone(phone).isPresent()) {
            return Result.error("该手机号已注册，请直接登录");
        }

        try {
            // 1. 保存到 t_user 表（角色为 CUSTOMER）
            User user = new User();
            user.setUsername(username);
            user.setPassword(request.getPassword());
            user.setRealName(request.getName().trim());
            user.setPhone(phone);
            user.setRole("CUSTOMER");
            user.setStatus(1);
            userService.save(user);

            // 2. 保存到 t_customer 表
            Customer customer = new Customer();
            customer.setName(request.getName().trim());
            customer.setPhone(phone);
            customer.setIdCard(request.getIdCard() != null ? request.getIdCard().trim() : "");
            customer.setLicense(request.getLicense() != null ? request.getLicense().trim() : "");
            customer.setRemark(request.getRemark() != null ? request.getRemark().trim() : "");
            customerService.save(customer);

            return Result.success("注册成功！请使用您的用户名和密码登录");
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }
}
