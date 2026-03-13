package org.Tracing.repository;

import org.Tracing.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, String> {
    // 由于系统设置是单例的，我们可以添加一个方法来获取唯一的设置记录
    SystemSettings findFirstByOrderByIdAsc();
} 