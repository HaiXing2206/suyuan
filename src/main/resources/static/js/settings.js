// 页面加载时获取系统设置
document.addEventListener('DOMContentLoaded', function() {
    loadSettings();
});

// 加载系统设置
async function loadSettings() {
    try {
        console.log('开始加载系统设置...');
        const response = await fetch('/api/settings');
        console.log('获取设置响应:', response);
        
        if (!response.ok) {
            throw new Error('获取设置失败: ' + response.status);
        }
        
        const settings = await response.json();
        console.log('获取到的设置:', settings);
        
        // 填充基本设置
        document.getElementById('systemName').value = settings.systemName;
        document.getElementById('companyName').value = settings.companyName;
        document.getElementById('contactEmail').value = settings.contactEmail;
        document.getElementById('timezone').value = settings.timezone;
        
        // 填充通知设置
        document.querySelector('input[type="checkbox"][data-setting="emailNotification"]').checked = settings.emailNotificationEnabled;
        document.querySelector('input[type="checkbox"][data-setting="systemNotification"]').checked = settings.systemNotificationEnabled;
        document.querySelector('input[type="checkbox"][data-setting="smsNotification"]').checked = settings.smsNotificationEnabled;
        
        // 填充安全设置
        document.querySelector('input[type="checkbox"][data-setting="twoFactorAuth"]').checked = settings.twoFactorAuthEnabled;
        document.getElementById('sessionTimeout').value = settings.sessionTimeout;
        document.getElementById('passwordPolicy').value = settings.passwordPolicy;
        
        // 填充数据设置
        document.getElementById('dataRetention').value = settings.dataRetentionDays;
        document.querySelector('input[type="checkbox"][data-setting="autoBackup"]').checked = settings.autoBackupEnabled;
        document.getElementById('backupFrequency').value = settings.backupFrequency;
        
        console.log('设置加载完成');
    } catch (error) {
        console.error('加载设置失败:', error);
        alert('加载设置失败: ' + error.message);
    }
}

// 保存设置
async function saveSettings() {
    try {
        console.log('开始保存设置...');
        
        // 收集所有设置值
        const settingsData = {
            // 基本设置
            systemName: document.getElementById('systemName').value,
            companyName: document.getElementById('companyName').value,
            contactEmail: document.getElementById('contactEmail').value,
            timezone: document.getElementById('timezone').value,
            
            // 通知设置
            emailNotificationEnabled: document.querySelector('input[type="checkbox"][data-setting="emailNotification"]').checked,
            systemNotificationEnabled: document.querySelector('input[type="checkbox"][data-setting="systemNotification"]').checked,
            smsNotificationEnabled: document.querySelector('input[type="checkbox"][data-setting="smsNotification"]').checked,
            
            // 安全设置
            twoFactorAuthEnabled: document.querySelector('input[type="checkbox"][data-setting="twoFactorAuth"]').checked,
            sessionTimeout: parseInt(document.getElementById('sessionTimeout').value),
            passwordPolicy: document.getElementById('passwordPolicy').value,
            
            // 数据设置
            dataRetentionDays: parseInt(document.getElementById('dataRetention').value),
            autoBackupEnabled: document.querySelector('input[type="checkbox"][data-setting="autoBackup"]').checked,
            backupFrequency: document.getElementById('backupFrequency').value
        };
        
        console.log('准备发送的设置数据:', settingsData);
        
        const response = await fetch('/api/settings', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(settingsData)
        });
        
        console.log('保存设置响应:', response);
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error('保存设置失败: ' + (errorData.error || response.status));
        }
        
        const result = await response.json();
        console.log('保存设置成功:', result);
        
        alert('设置已保存');
    } catch (error) {
        console.error('保存设置失败:', error);
        alert('保存设置失败: ' + error.message);
    }
}

// 重置设置
async function resetSettings() {
    if (confirm('确定要重置所有设置吗？')) {
        try {
            console.log('开始重置设置...');
            
            const response = await fetch('/api/settings/reset', {
                method: 'POST'
            });
            
            console.log('重置设置响应:', response);
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error('重置设置失败: ' + (errorData.error || response.status));
            }
            
            // 重新加载设置
            await loadSettings();
            console.log('设置重置完成');
            alert('设置已重置');
        } catch (error) {
            console.error('重置设置失败:', error);
            alert('重置设置失败: ' + error.message);
        }
    }
} 