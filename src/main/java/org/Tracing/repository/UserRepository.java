package org.Tracing.repository;

import org.Tracing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    
    // 统计活跃用户数（状态为active的用户）
    long countByStatus(String status);
    
    // 统计指定时间之前的活跃用户数
    long countByStatusAndRegisteredAtBefore(String status, LocalDateTime date);
} 