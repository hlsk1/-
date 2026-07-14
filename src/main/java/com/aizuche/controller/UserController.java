package com.aizuche.controller;

import com.aizuche.dto.LoginRequest;
import com.aizuche.entity.User;
import com.aizuche.service.UserService;
import com.aizuche.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String username,
                       @RequestParam(required = false) String phone) {
        List<User> list = userService.findByConditions(username, phone);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return Result.error("用户名已存在");
        }
        userService.save(user);
        return Result.success("账号创建成功");
    }

    @PutMapping("/edit/{id}")
    public Result edit(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.update(user);
        if (updated == null) {
            return Result.error("用户不存在");
        }
        return Result.success("修改成功");
    }

    @PostMapping("/resetPassword/{id}")
    public Result resetPassword(@PathVariable Long id) {
        String tempPassword = userService.resetPassword(id);
        if (tempPassword == null) {
            return Result.error("用户不存在");
        }
        return Result.success("临时密码已生成: " + tempPassword);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        log.info(">>> 登录请求: username={}", request.getUsername());

        // 先按用户名查，找不到则按手机号查
        User user = userService.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            user = userService.findByPhone(request.getUsername()).orElse(null);
        }
        if (user == null) {
            log.warn(">>> 登录失败: 用户不存在 - input={}", request.getUsername());
            return Result.error("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn(">>> 登录失败: 账号已禁用 - username={}", request.getUsername());
            return Result.error("账号已被禁用");
        }

        // 验证密码（兼容明文密码的自动升级）
        try {
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn(">>> 登录失败: 密码不匹配 - username={}", request.getUsername());
                return Result.error("用户名或密码错误");
            }
        } catch (IllegalArgumentException e) {
            // 数据库中可能是明文密码，BCrypt无法解析，尝试直接比对
            log.warn(">>> BCrypt解析失败，尝试明文比对 - username={}", request.getUsername());
            if (!request.getPassword().equals(user.getPassword())) {
                return Result.error("用户名或密码错误");
            }
            // 明文密码匹配成功，自动升级为BCrypt加密
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userService.save(user);
            log.info(">>> 密码已自动升级为BCrypt加密 - username={}", request.getUsername());
        }

        // 更新登录信息
        String ip = httpRequest.getRemoteAddr();
        userService.updateLoginInfo(user.getId(), ip);
        user.setPassword(null);
        log.info(">>> 登录成功 - username={}, ip={}", request.getUsername(), ip);
        return Result.success(user);
    }

    @GetMapping("/logs/{id}")
    public Result getLogs(@PathVariable Long id) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 返回用户的登录信息
        java.util.Map<String, Object> logInfo = new java.util.HashMap<>();
        logInfo.put("userId", user.getId());
        logInfo.put("username", user.getUsername());
        logInfo.put("lastLoginTime", user.getLastLoginTime());
        logInfo.put("lastLoginIp", user.getLastLoginIp());
        return Result.success(logInfo);
    }
}
