package org.Tracing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "user_id")
    private String id;
    
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    @Column(name = "role", columnDefinition = "ENUM('consumer', 'supplier', 'merchant', 'regulator')")
    private String role;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "userhash")
    private String use_hash;
    
    @Column(name = "registered_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registeredAt;
    
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('active', 'suspended') DEFAULT 'active'")
    private String status;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return id;
    }

    public void setUserId(String userId) {
        this.id = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 