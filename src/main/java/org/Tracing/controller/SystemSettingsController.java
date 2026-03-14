package org.Tracing.controller;

import org.Tracing.entity.SystemSettings;
import org.Tracing.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SystemSettingsController {
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsController.class);
    
    @Autowired
    private SystemSettingsService settingsService;
    
    // 获取系统设置
    @GetMapping
    public ResponseEntity<?> getSettings() {
        try {
            SystemSettings settings = settingsService.getSettings();
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            logger.error("获取系统设置失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "获取系统设置失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 更新系统设置
    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, Object> settingsData) {
        try {
            SystemSettings updatedSettings = settingsService.updateSettings(settingsData);
            return ResponseEntity.ok(updatedSettings);
        } catch (Exception e) {
            logger.error("更新系统设置失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "更新系统设置失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 重置系统设置
    @PostMapping("/reset")
    public ResponseEntity<?> resetSettings() {
        try {
            SystemSettings resetSettings = settingsService.resetSettings();
            return ResponseEntity.ok(resetSettings);
        } catch (Exception e) {
            logger.error("重置系统设置失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "重置系统设置失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/system-name")
    public ResponseEntity<String> getSystemName() {
        try {
            logger.info("开始获取系统名称");
            SystemSettings settings = settingsService.getSettings();
            String systemName = settings.getSystemName();
            logger.info("成功获取系统名称: {}", systemName);
            return ResponseEntity.ok(systemName);
        } catch (Exception e) {
            logger.error("获取系统名称失败", e);
            return ResponseEntity.ok("数据要素评估与审计平台");
        }
    }
} 