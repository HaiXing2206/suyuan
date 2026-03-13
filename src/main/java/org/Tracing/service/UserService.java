package org.Tracing.service;

import org.Tracing.entity.User;
import org.Tracing.dto.LoginResponse;
import org.Tracing.util.JwtUtil;
import org.Tracing.util.PasswordUtil;
import org.Tracing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final List<String> VALID_ROLES = Arrays.asList("consumer", "supplier", "merchant", "regulator");
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public LoginResponse login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            return null;
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    public boolean register(String username, String password) {
        try {
            logger.info("开始注册用户: {}", username);
            
            // 检查用户名是否已存在
            User existingUser = findByUsername(username);
            if (existingUser != null) {
                logger.warn("用户名已存在: {}", username);
                return false;
            }

            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setUsername(username);
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setRegisteredAt(LocalDateTime.now());
            user.setStatus("active");

            logger.info("正在保存用户信息: {}", username);
            userRepository.save(user);
            logger.info("用户注册成功: {}", username);
            return true;
        } catch (Exception e) {
            logger.error("注册用户时发生错误: " + e.getMessage(), e);
            return false;
        }
    }

    // 获取所有用户
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 根据ID查找用户
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    // 创建用户
    public User createUser(User user) {
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
        }
        user.setId(UUID.randomUUID().toString());
        user.setRegisteredAt(LocalDateTime.now());
        if (user.getStatus() == null) {
            user.setStatus("active");
        }
        return userRepository.save(user);
    }

    // 更新用户
    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        User existingUser = findById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证用户名
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        // 检查用户名是否已被其他用户使用
        User userWithSameUsername = findByUsername(user.getUsername());
        if (userWithSameUsername != null && !userWithSameUsername.getId().equals(user.getId())) {
            throw new IllegalArgumentException("用户名已被使用");
        }

        // 验证角色
        if (user.getRole() == null || !VALID_ROLES.contains(user.getRole())) {
            throw new IllegalArgumentException("无效的角色值");
        }

        // 验证状态
        if (user.getStatus() == null || !Arrays.asList("active", "suspended").contains(user.getStatus())) {
            throw new IllegalArgumentException("无效的状态值");
        }

        // 更新用户信息，保留原有的一些字段
        existingUser.setUsername(user.getUsername());
        existingUser.setRole(user.getRole());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setStatus(user.getStatus());

        // 如果提供了新密码，则更新密码
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
        }

        // 确保保留注册时间
        if (existingUser.getRegisteredAt() == null) {
            existingUser.setRegisteredAt(LocalDateTime.now());
        }
        
        try {
            logger.info("正在更新用户信息: id={}, username={}, role={}, status={}", 
                existingUser.getId(), existingUser.getUsername(), existingUser.getRole(), existingUser.getStatus());
            User savedUser = userRepository.save(existingUser);
            logger.info("用户信息更新成功: id={}", existingUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("保存用户信息失败: id={}, error={}", existingUser.getId(), e.getMessage(), e);
            if (e.getCause() != null) {
                logger.error("原始错误: {}", e.getCause().getMessage());
            }
            throw new RuntimeException("保存用户信息失败: " + e.getMessage());
        }
    }

    // 更新用户状态
    public User updateUserStatus(String id, String status) {
        User user = findById(id);
        if (user == null) {
            return null;
        }

        if (!Arrays.asList("active", "suspended").contains(status)) {
            throw new IllegalArgumentException("无效的状态值");
        }

        user.setStatus(status);
        return userRepository.save(user);
    }

    // 搜索用户
    public List<User> searchUsers(String query) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
            .filter(user -> 
                user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase())) ||
                (user.getPhone() != null && user.getPhone().contains(query))
            )
            .collect(Collectors.toList());
    }

    // 根据状态统计用户数量
    public long countByStatus(String status) {
        return userRepository.countByStatus(status);
    }

    // 统计指定时间之前的活跃用户数
    public long countByStatusAndRegisteredAtBefore(String status, LocalDateTime date) {
        return userRepository.countByStatusAndRegisteredAtBefore(status, date);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public String getUserIdFromToken(String token) {
        try {
            // 移除 "Bearer " 前缀
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 使用JwtUtil解析token
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            logger.error("解析token失败: {}", e.getMessage());
            return null;
        }
    }
} 