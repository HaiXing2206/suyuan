package org.Tracing.controller;

import org.Tracing.entity.User;
import org.Tracing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            String userId = userService.getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Invalid token");
            }

            // 获取用户信息
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // 构建返回数据
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
            response.put("role", user.getRole());
            response.put("status", user.getStatus());
            response.put("registeredAt", user.getRegisteredAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取用户信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error getting user info: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> updateData) {
        try {
            // 从token中获取用户ID
            String userId = userService.getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body("Invalid token");
            }

            // 获取用户信息
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // 更新用户信息
            if (updateData.containsKey("username")) {
                String newUsername = updateData.get("username");
                // 检查新用户名是否已被使用
                User existingUser = userService.findByUsername(newUsername);
                if (existingUser != null && !existingUser.getId().equals(userId)) {
                    return ResponseEntity.badRequest().body("用户名已被使用");
                }
                user.setUsername(newUsername);
            }
            if (updateData.containsKey("email")) {
                user.setEmail(updateData.get("email"));
            }
            if (updateData.containsKey("phone")) {
                user.setPhone(updateData.get("phone"));
            }

            // 保存更新
            userService.updateUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User information updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("更新用户信息失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating user info: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取所有用户
    @GetMapping("/list")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("获取用户列表失败", e);
            return ResponseEntity.internalServerError().body("获取用户列表失败");
        }
    }

    // 获取单个用户
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return ResponseEntity.internalServerError().body("获取用户信息失败");
        }
    }

    // 创建用户
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            logger.error("创建用户失败", e);
            return ResponseEntity.internalServerError().body("创建用户失败");
        }
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            logger.error("更新用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("更新用户失败", e);
            return ResponseEntity.internalServerError().body("更新用户失败");
        }
    }

    // 更新用户状态
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable String id, @RequestBody Map<String, String> status) {
        try {
            User updatedUser = userService.updateUserStatus(id, status.get("status"));
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("更新用户状态失败", e);
            return ResponseEntity.internalServerError().body("更新用户状态失败");
        }
    }

    // 搜索用户
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String q) {
        try {
            List<User> users = userService.searchUsers(q);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("搜索用户失败", e);
            return ResponseEntity.internalServerError().body("搜索用户失败");
        }
    }

    // 获取活跃用户统计
    @GetMapping("/statistics")
    public ResponseEntity<?> getUserStatistics() {
        try {
            // 获取当前活跃用户数
            long activeUsers = userService.countByStatus("active");
            
            // 获取上月活跃用户数（使用注册时间在30天前的用户数）
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            long lastMonthActiveUsers = userService.countByStatusAndRegisteredAtBefore("active", thirtyDaysAgo);
            
            // 计算增长率
            double activeUsersGrowth = lastMonthActiveUsers == 0 ? 0 :
                ((double)(activeUsers - lastMonthActiveUsers) / lastMonthActiveUsers) * 100;
            
            // 构建返回数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("activeUsers", activeUsers);
            statistics.put("activeUsersGrowth", Math.round(activeUsersGrowth * 10) / 10.0);
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("获取用户统计数据失败", e);
            return ResponseEntity.internalServerError().body("获取用户统计数据失败");
        }
    }
} 