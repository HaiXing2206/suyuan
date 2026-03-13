document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('addProductForm');
    
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // 验证表单
        if (!validateForm()) {
            return;
        }
        
        // 获取表单数据
        const formData = {
            productId: document.getElementById('productId').value,
            name: document.getElementById('name').value,
            manufacturer: document.getElementById('manufacturer').value,
            batchNumber: document.getElementById('batchNumber').value,
            origin: document.getElementById('origin').value,
            spec: document.getElementById('productSpec').value,
            description: document.getElementById('productDescription').value
        };

        // 保存按钮的原始内容
        const submitButton = form.querySelector('button[type="submit"]');
        const originalText = submitButton.innerHTML;

        try {
            // 显示加载状态
            submitButton.disabled = true;
            submitButton.innerHTML = `
                <svg class="spinner" viewBox="0 0 50 50">
                    <circle class="path" cx="25" cy="25" r="20" fill="none" stroke-width="5"></circle>
                </svg>
                正在上链...
            `;

            // 发送请求到后端
            const response = await fetch('/api/products', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();

            if (!response.ok) {
                throw new Error(result.error || '网络请求失败');
            }

            // 显示成功消息
            showNotification('success', result.message || '产品已成功添加到区块链！');
            
            // 重置表单
            form.reset();
            
            // 延迟跳转到产品列表页
            setTimeout(() => {
                window.location.href = 'products.html';
            }, 2000);

        } catch (error) {
            console.error('Error:', error);
            showNotification('error', '添加产品失败：' + error.message);
        } finally {
            // 恢复按钮状态
            submitButton.disabled = false;
            submitButton.innerHTML = originalText;
        }
    });
});

// 显示通知
function showNotification(type, message) {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <svg class="icon" viewBox="0 0 24 24">
                ${type === 'success' ? `
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                ` : `
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                `}
            </svg>
            <span>${message}</span>
        </div>
    `;

    document.body.appendChild(notification);

    // 添加动画类
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);

    // 3秒后移除通知
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

// 添加表单验证
function validateForm() {
    const productId = document.getElementById('productId').value;
    const name = document.getElementById('name').value;
    const manufacturer = document.getElementById('manufacturer').value;
    const batchNumber = document.getElementById('batchNumber').value;
    const origin = document.getElementById('origin').value;
    const productSpec = document.getElementById('productSpec').value;
    const productDescription = document.getElementById('productDescription').value;

    if (!productId || !name || !manufacturer || !batchNumber || !origin || !productSpec || !productDescription) {
        showNotification('error', '请填写所有必填字段');
        return false;
    }

    // 验证产品ID格式
    if (!/^[A-Za-z0-9-_]+$/.test(productId)) {
        showNotification('error', '产品ID只能包含字母、数字、下划线和连字符');
        return false;
    }

    return true;
} 