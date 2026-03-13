package org.Tracing.controller.logcontroller;

import org.Tracing.dto.RegisterRequest;
import org.Tracing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {
    
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        logger.info("收到注册请求: username={}", request.getUsername());
        
        Map<String, String> response = new HashMap<>();
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            logger.error("注册失败: 用户名为空");
            response.put("error", "用户名不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            logger.error("注册失败: 密码为空");
            response.put("error", "密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        boolean success = userService.register(request.getUsername(), request.getPassword());
        
        if (success) {
            logger.info("用户注册成功: {}", request.getUsername());
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } else {
            logger.error("用户注册失败: {}", request.getUsername());
            response.put("error", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 