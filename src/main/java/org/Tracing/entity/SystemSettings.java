package org.Tracing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
public class SystemSettings {
    @Id
    private String id;
    
    // 基本设置
    private String systemName;
    private String companyName;
    private String contactEmail;
    private String timezone;
    
    // 通知设置
    private boolean emailNotificationEnabled;
    private boolean systemNotificationEnabled;
    private boolean smsNotificationEnabled;
    
    // 安全设置
    private boolean twoFactorAuthEnabled;
    private int sessionTimeout;
    private String passwordPolicy;
    
    // 数据设置
    private int dataRetentionDays;
    private boolean autoBackupEnabled;
    private String backupFrequency;
    
    private LocalDateTime lastModified;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isEmailNotificationEnabled() {
        return emailNotificationEnabled;
    }

    public void setEmailNotificationEnabled(boolean emailNotificationEnabled) {
        this.emailNotificationEnabled = emailNotificationEnabled;
    }

    public boolean isSystemNotificationEnabled() {
        return systemNotificationEnabled;
    }

    public void setSystemNotificationEnabled(boolean systemNotificationEnabled) {
        this.systemNotificationEnabled = systemNotificationEnabled;
    }

    public boolean isSmsNotificationEnabled() {
        return smsNotificationEnabled;
    }

    public void setSmsNotificationEnabled(boolean smsNotificationEnabled) {
        this.smsNotificationEnabled = smsNotificationEnabled;
    }

    public boolean isTwoFactorAuthEnabled() {
        return twoFactorAuthEnabled;
    }

    public void setTwoFactorAuthEnabled(boolean twoFactorAuthEnabled) {
        this.twoFactorAuthEnabled = twoFactorAuthEnabled;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(String passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public int getDataRetentionDays() {
        return dataRetentionDays;
    }

    public void setDataRetentionDays(int dataRetentionDays) {
        this.dataRetentionDays = dataRetentionDays;
    }

    public boolean isAutoBackupEnabled() {
        return autoBackupEnabled;
    }

    public void setAutoBackupEnabled(boolean autoBackupEnabled) {
        this.autoBackupEnabled = autoBackupEnabled;
    }

    public String getBackupFrequency() {
        return backupFrequency;
    }

    public void setBackupFrequency(String backupFrequency) {
        this.backupFrequency = backupFrequency;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
} 