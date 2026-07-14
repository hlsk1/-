package com.aizuche.service;

import com.aizuche.entity.User;
import com.aizuche.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByConditions(String username, String phone) {
        return userRepository.findByConditions(username, phone);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Transactional
    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public User update(User user) {
        User existing = userRepository.findById(user.getId()).orElse(null);
        if (existing != null) {
            existing.setRealName(user.getRealName());
            existing.setPhone(user.getPhone());
            existing.setEmail(user.getEmail());
            existing.setRole(user.getRole());
            existing.setStatus(user.getStatus());
            existing.setUpdateTime(LocalDateTime.now());
            return userRepository.save(existing);
        }
        return null;
    }

    @Transactional
    public String resetPassword(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            String tempPassword = generateTempPassword();
            user.setPassword(passwordEncoder.encode(tempPassword));
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);
            return tempPassword;
        }
        return null;
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void updateLoginInfo(Long id, String ip) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(ip);
            userRepository.save(user);
        }
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
}