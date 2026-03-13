// 检查用户是否已登录
function checkAuth() {
    const token = localStorage.getItem('token');
    const currentPage = window.location.href;
    
    // 如果用户未登录且不在登录页面，重定向到登录页面
    if (!token && !currentPage.includes('login.html') && !currentPage.includes('register.html')) {
        window.location.href = '/html/login.html';
        return false;
    }
    
    // 如果用户已登录且在登录页面，重定向到首页
    if (token && (currentPage.includes('login.html') || currentPage.includes('register.html'))) {
        window.location.href = '/html/index.html';
        return false;
    }
    
    return true;
}

// 获取当前用户信息
function getCurrentUser() {
    return {
        userId: localStorage.getItem('userId'),
        username: localStorage.getItem('username'),
        role: localStorage.getItem('role')
    };
}

// 检查用户角色
function hasRole(requiredRole) {
    const userRole = localStorage.getItem('role');
    return userRole === requiredRole;
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    window.location.href = 'login.html';
}

// 添加token到请求头
function addAuthHeader(headers = {}) {
    const token = localStorage.getItem('token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

// 显示当前用户名
function displayCurrentUsername() {
    const username = localStorage.getItem('username');
    const usernameElement = document.getElementById('currentUsername');
    const welcomeTitle = document.getElementById('welcome-title');
    
    if (username) {
        if (usernameElement) {
            usernameElement.textContent = `欢迎，${username}`;
        }
        if (welcomeTitle) {
            welcomeTitle.textContent = `欢迎回来，${username}`;
        }
    }
}

// 页面加载时检查登录状态并显示用户名
document.addEventListener('DOMContentLoaded', () => {
    if (checkAuth()) {
        displayCurrentUsername();
        // 如果在用户管理页面，加载用户列表
        if (window.location.href.includes('users.html')) {
            loadUsers();
        }
    }
}); 