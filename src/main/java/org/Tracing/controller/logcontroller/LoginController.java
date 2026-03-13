package org.Tracing.controller.logcontroller;

import com.google.gson.Gson;
import org.Tracing.dto.LoginRequest;
import org.Tracing.dto.LoginResponse;
import org.Tracing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.login(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );

            if (loginResponse != null) {
                return ResponseEntity.ok(loginResponse);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "用户名或密码错误");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 