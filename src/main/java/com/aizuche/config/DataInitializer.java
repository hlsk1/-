package com.aizuche.config;

import com.aizuche.entity.User;
import com.aizuche.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器：每次启动时确保管理员账号存在且密码正确
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

            User admin = userRepository.findByUsername(DEFAULT_USERNAME).orElse(null);

            if (admin == null) {
                // 不存在 → 创建
                admin = new User();
                admin.setUsername(DEFAULT_USERNAME);
                admin.setPassword(encodedPassword);
                admin.setRealName("系统管理员");
                admin.setPhone("13800000000");
                admin.setRole("SUPER");
                admin.setStatus(1);
                admin.setCreateTime(LocalDateTime.now());
                admin.setUpdateTime(LocalDateTime.now());
                userRepository.save(admin);
                log.info(">>> 管理员账号已创建: {} / {}", DEFAULT_USERNAME, DEFAULT_PASSWORD);
                System.out.println(">>> 管理员账号已创建: " + DEFAULT_USERNAME + " / " + DEFAULT_PASSWORD);
            } else {
                // 已存在 → 强制重置密码，确保密码一定是 123456 的 BCrypt 值
                admin.setPassword(encodedPassword);
                admin.setStatus(1); // 确保未被禁用
                admin.setUpdateTime(LocalDateTime.now());
                userRepository.save(admin);
                log.info(">>> 管理员密码已重置: {} / {}", DEFAULT_USERNAME, DEFAULT_PASSWORD);
                System.out.println(">>> 管理员密码已重置: " + DEFAULT_USERNAME + " / " + DEFAULT_PASSWORD);
            }
        } catch (Exception e) {
            log.error(">>> 数据初始化失败", e);
            System.err.println(">>> 数据初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
