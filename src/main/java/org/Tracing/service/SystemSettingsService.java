package org.Tracing.service;

import org.Tracing.entity.SystemSettings;
import org.Tracing.repository.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SystemSettingsService {
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsService.class);
    
    @Autowired
    private SystemSettingsRepository settingsRepository;
    
    // 获取系统设置
    public SystemSettings getSettings() {
        SystemSettings settings = settingsRepository.findFirstByOrderByIdAsc();
        if (settings == null) {
            // 如果没有设置记录，创建默认设置
            settings = createDefaultSettings();
        }
        return settings;
    }
    
    // 更新系统设置
    public SystemSettings updateSettings(Map<String, Object> settingsData) {
        SystemSettings settings = getSettings();
        
        // 更新基本设置
        if (settingsData.containsKey("systemName")) {
            settings.setSystemName((String) settingsData.get("systemName"));
        }
        if (settingsData.containsKey("companyName")) {
            settings.setCompanyName((String) settingsData.get("companyName"));
        }
        if (settingsData.containsKey("contactEmail")) {
            settings.setContactEmail((String) settingsData.get("contactEmail"));
        }
        if (settingsData.containsKey("timezone")) {
            settings.setTimezone((String) settingsData.get("timezone"));
        }
        
        // 更新通知设置
        if (settingsData.containsKey("emailNotificationEnabled")) {
            settings.setEmailNotificationEnabled((Boolean) settingsData.get("emailNotificationEnabled"));
        }
        if (settingsData.containsKey("systemNotificationEnabled")) {
            settings.setSystemNotificationEnabled((Boolean) settingsData.get("systemNotificationEnabled"));
        }
        if (settingsData.containsKey("smsNotificationEnabled")) {
            settings.setSmsNotificationEnabled((Boolean) settingsData.get("smsNotificationEnabled"));
        }
        
        // 更新安全设置
        if (settingsData.containsKey("twoFactorAuthEnabled")) {
            settings.setTwoFactorAuthEnabled((Boolean) settingsData.get("twoFactorAuthEnabled"));
        }
        if (settingsData.containsKey("sessionTimeout")) {
            settings.setSessionTimeout((Integer) settingsData.get("sessionTimeout"));
        }
        if (settingsData.containsKey("passwordPolicy")) {
            settings.setPasswordPolicy((String) settingsData.get("passwordPolicy"));
        }
        
        // 更新数据设置
        if (settingsData.containsKey("dataRetentionDays")) {
            settings.setDataRetentionDays((Integer) settingsData.get("dataRetentionDays"));
        }
        if (settingsData.containsKey("autoBackupEnabled")) {
            settings.setAutoBackupEnabled((Boolean) settingsData.get("autoBackupEnabled"));
        }
        if (settingsData.containsKey("backupFrequency")) {
            settings.setBackupFrequency((String) settingsData.get("backupFrequency"));
        }
        
        settings.setLastModified(LocalDateTime.now());
        
        try {
            return settingsRepository.save(settings);
        } catch (Exception e) {
            logger.error("保存系统设置失败", e);
            throw new RuntimeException("保存系统设置失败: " + e.getMessage());
        }
    }
    
    // 重置系统设置
    public SystemSettings resetSettings() {
        SystemSettings settings = getSettings();
        SystemSettings defaultSettings = createDefaultSettings();
        
        // 保留ID
        defaultSettings.setId(settings.getId());
        defaultSettings.setLastModified(LocalDateTime.now());
        
        try {
            return settingsRepository.save(defaultSettings);
        } catch (Exception e) {
            logger.error("重置系统设置失败", e);
            throw new RuntimeException("重置系统设置失败: " + e.getMessage());
        }
    }
    
    // 创建默认设置
    private SystemSettings createDefaultSettings() {
        SystemSettings settings = new SystemSettings();
        settings.setId(UUID.randomUUID().toString());
        settings.setSystemName("产品溯源管理系统");
        settings.setCompanyName("示例科技有限公司");
        settings.setContactEmail("contact@example.com");
        settings.setTimezone("Asia/Shanghai");
        
        settings.setEmailNotificationEnabled(true);
        settings.setSystemNotificationEnabled(true);
        settings.setSmsNotificationEnabled(false);
        
        settings.setTwoFactorAuthEnabled(true);
        settings.setSessionTimeout(30);
        settings.setPasswordPolicy("standard");
        
        settings.setDataRetentionDays(365);
        settings.setAutoBackupEnabled(true);
        settings.setBackupFrequency("weekly");
        
        settings.setLastModified(LocalDateTime.now());
        
        try {
            return settingsRepository.save(settings);
        } catch (Exception e) {
            logger.error("创建默认系统设置失败", e);
            throw new RuntimeException("创建默认系统设置失败: " + e.getMessage());
        }
    }
} 