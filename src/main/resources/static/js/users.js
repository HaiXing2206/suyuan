// 用户管理相关的JavaScript代码

// 加载用户列表
async function loadUsers() {
    try {
        const response = await fetch('/api/users/list');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        const users = Array.isArray(data) ? data : [];
        const tableBody = document.getElementById('userTableBody');
        if (!tableBody) {
            console.error('找不到用户表格体元素');
            return;
        }
        tableBody.innerHTML = '';

        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${getRoleName(user.role)}</td>
                <td>${user.email || '-'}</td>
                <td>${user.phone || '-'}</td>
                <td>${formatDate(user.registeredAt)}</td>
                <td>
                    <span class="status-badge ${user.status === 'active' ? 'active' : 'suspended'}">
                        ${user.status === 'active' ? '正常' : '已停用'}
                    </span>
                </td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-icon" onclick="editUser('${user.id}')" title="编辑">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                            </svg>
                        </button>
                        <button class="btn-icon" onclick="toggleUserStatus('${user.id}', '${user.status}')" title="${user.status === 'active' ? '停用' : '启用'}">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                ${user.status === 'active' ? 
                                    '<path d="M18 6 6 18"></path><path d="m6 6 12 12"></path>' :
                                    '<path d="M22 12h-4"></path><path d="M2 12h4"></path><path d="M12 2v4"></path><path d="M12 18v4"></path><path d="m4.93 4.93 2.83 2.83"></path><path d="m16.24 16.24 2.83 2.83"></path><path d="m4.93 19.07 2.83-2.83"></path><path d="m16.24 7.76 2.83-2.83"></path>'
                                }
                            </svg>
                        </button>
                    </div>
                </td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('加载用户列表失败:', error);
        showToast('加载用户列表失败', 'error');
    }
}

// 获取角色名称
function getRoleName(role) {
    const roleMap = {
        'consumer': '消费者',
        'supplier': '供应商',
        'merchant': '商家',
        'regulator': '监管者'
    };
    return roleMap[role] || role;
}

// 格式化日期
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// 显示添加用户模态框
function showAddUserModal(event) {
    if (event) {
        event.preventDefault();
        document.getElementById('modalTitle').textContent = '添加用户';
        document.getElementById('userForm').reset();
        document.getElementById('userId').value = '';
        document.getElementById('password').required = true;
        document.getElementById('userModal').style.display = 'flex';
    }
}

// 显示编辑用户模态框
async function editUser(userId) {
    try {
        const response = await fetch(`/api/users/${userId}`);
        if (!response.ok) {
            throw new Error('获取用户信息失败');
        }
        const user = await response.json();
        
        document.getElementById('modalTitle').textContent = '编辑用户';
        document.getElementById('userId').value = user.id;
        document.getElementById('username').value = user.username;
        document.getElementById('role').value = user.role || 'consumer';
        document.getElementById('email').value = user.email || '';
        document.getElementById('phone').value = user.phone || '';
        document.getElementById('status').value = user.status || 'active';
        document.getElementById('password').required = false;
        document.getElementById('password').value = ''; // 清空密码字段
        
        document.getElementById('userModal').style.display = 'flex';
    } catch (error) {
        console.error('加载用户信息失败:', error);
        showToast('加载用户信息失败', 'error');
    }
}

// 关闭用户模态框
function closeUserModal() {
    document.getElementById('userModal').style.display = 'none';
}

// 切换用户状态
async function toggleUserStatus(userId, currentStatus) {
    const newStatus = currentStatus === 'active' ? 'suspended' : 'active';
    const confirmMessage = newStatus === 'active' ? '确定要启用该用户吗？' : '确定要停用该用户吗？';
    
    if (confirm(confirmMessage)) {
        try {
            const response = await fetch(`/api/users/${userId}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ status: newStatus })
            });
            
            if (response.ok) {
                showToast(`用户已${newStatus === 'active' ? '启用' : '停用'}`, 'success');
                loadUsers();
            } else {
                throw new Error('操作失败');
            }
        } catch (error) {
            console.error('更新用户状态失败:', error);
            showToast('更新用户状态失败', 'error');
        }
    }
}

// 添加一个标志来防止重复加载
let isInitialized = false;

// 将所有DOM操作包装在DOMContentLoaded事件中
document.addEventListener('DOMContentLoaded', function() {
    if (isInitialized) return;
    isInitialized = true;

    // 处理用户表单提交
    const userForm = document.getElementById('userForm');
    if (userForm) {
        userForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const userId = document.getElementById('userId').value;
            const formData = {
                id: userId,
                username: document.getElementById('username').value,
                role: document.getElementById('role').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                status: document.getElementById('status').value
            };

            // 只有在添加新用户或提供了新密码时才包含密码字段
            const password = document.getElementById('password').value;
            if (password) {
                formData.passwordHash = password;
            }

            const url = userId ? `/api/users/${userId}` : '/api/users';
            const method = userId ? 'PUT' : 'POST';

            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });

                const responseText = await response.text();
                let errorMessage = '操作失败';

                if (!response.ok) {
                    try {
                        const errorData = JSON.parse(responseText);
                        errorMessage = errorData.message || errorMessage;
                    } catch (e) {
                        errorMessage = responseText || errorMessage;
                    }
                    throw new Error(errorMessage);
                }

                showToast('操作成功', 'success');
                closeUserModal();
                loadUsers();
            } catch (error) {
                showToast(error.message || '操作失败', 'error');
            }
        });
    }

    // 搜索用户
    const userSearch = document.getElementById('userSearch');
    if (userSearch) {
        userSearch.addEventListener('input', debounce(async function(e) {
            const searchTerm = e.target.value.trim();
            try {
                const response = await fetch(`/api/users/search?q=${encodeURIComponent(searchTerm)}`);
                const users = await response.json();
                const tableBody = document.getElementById('userTableBody');
                tableBody.innerHTML = '';

                users.forEach(user => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${getRoleName(user.role)}</td>
                        <td>${user.email || '-'}</td>
                        <td>${user.phone || '-'}</td>
                        <td>${formatDate(user.registeredAt)}</td>
                        <td>
                            <span class="status-badge ${user.status === 'active' ? 'active' : 'suspended'}">
                                ${user.status === 'active' ? '正常' : '已停用'}
                            </span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <button class="btn-icon" onclick="editUser('${user.id}')" title="编辑">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                    </svg>
                                </button>
                                <button class="btn-icon" onclick="toggleUserStatus('${user.id}', '${user.status}')" title="${user.status === 'active' ? '停用' : '启用'}">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        ${user.status === 'active' ? 
                                            '<path d="M18 6 6 18"></path><path d="m6 6 12 12"></path>' :
                                            '<path d="M22 12h-4"></path><path d="M2 12h4"></path><path d="M12 2v4"></path><path d="M12 18v4"></path><path d="m4.93 4.93 2.83 2.83"></path><path d="m16.24 16.24 2.83 2.83"></path><path d="m4.93 19.07 2.83-2.83"></path><path d="m16.24 7.76 2.83-2.83"></path>'
                                        }
                                    </svg>
                                </button>
                            </div>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });
            } catch (error) {
                console.error('搜索用户失败:', error);
                showToast('搜索用户失败', 'error');
            }
        }, 300));
    }

    // 初始加载用户列表
    loadUsers();
});

// 防抖函数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 显示提示消息
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(toast);
        }, 300);
    }, 3000);
} 