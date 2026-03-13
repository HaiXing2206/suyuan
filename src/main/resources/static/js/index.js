// 获取最近添加的产品
async function fetchRecentProducts() {
    try {
        const response = await fetch('/api/products');
        if (!response.ok) {
            throw new Error('获取产品列表失败');
        }
        const products = await response.json();
        
        // 更新产品总数
        updateProductStats(products);
        
        // 按生产日期排序，获取最近的3个产品
        const recentProducts = products
            .sort((a, b) => new Date(b.productionDate) - new Date(a.productionDate))
            .slice(0, 3);
            
        displayRecentProducts(recentProducts);
    } catch (error) {
        console.error('Error:', error);
        showNotification('获取产品数据失败：' + error.message, 'error');
    }
}

// 更新产品统计信息
function updateProductStats(products) {
    // 更新产品总数
    const totalProductsElement = document.querySelector('.stats-cards .card:nth-child(1) .stat-value');
    if (totalProductsElement) {
        totalProductsElement.textContent = products.length.toLocaleString();
    }

    // 计算活跃追踪数量（根据最近30天有更新记录的产品）
    const activeTrackingElement = document.querySelector('.stats-cards .card:nth-child(2) .stat-value');
    if (activeTrackingElement) {
        const thirtyDaysAgo = new Date();
        thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
        
        const activeProducts = products.filter(product => {
            const lastUpdate = new Date(product.updatedAt || product.productionDate);
            return lastUpdate >= thirtyDaysAgo;
        });
        
        activeTrackingElement.textContent = activeProducts.length.toLocaleString();
    }

    // 计算增长率（与上月相比）
    const growthElements = document.querySelectorAll('.stat-change');
    growthElements.forEach(element => {
        const cardType = element.closest('.card').querySelector('h3').textContent;
        let growthRate = 0;
        
        if (cardType === '产品总数') {
            growthRate = 0;
        } else if (cardType === '活跃追踪') {
            growthRate = 0;
        } else if (cardType === '二维码扫描') {
            growthRate = 0;
        }
        
        element.textContent = `较上月增长 ${growthRate}%`;
    });
}

// 显示最近添加的产品
function displayRecentProducts(products) {
    const recentProductsContainer = document.querySelector('.recent-products');
    if (!recentProductsContainer) {
        console.error('找不到最近产品容器');
        return;
    }

    recentProductsContainer.innerHTML = ''; // 清空现有内容

    if (!Array.isArray(products) || products.length === 0) {
        recentProductsContainer.innerHTML = '<div class="no-data">暂无产品数据</div>';
        return;
    }

    products.forEach(product => {
        const productItem = document.createElement('div');
        productItem.className = 'product-item';
        productItem.innerHTML = `
            <div class="product-info">
                <div class="info-label">产品名称</div>
                <div class="info-value">${product.name || ''}</div>
            </div>
            <div class="product-info">
                <div class="info-label">生产日期</div>
                <div class="info-value">${formatDate(product.productionDate) || ''}</div>
            </div>
            <div class="product-info">
                <div class="info-label">生产商</div>
                <div class="info-value">${product.manufacturer || ''}</div>
            </div>
        `;
        recentProductsContainer.appendChild(productItem);
    });
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    } catch (error) {
        console.error('日期格式化错误:', error);
        return dateString;
    }
}

// 显示通知
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 加载统计数据
    loadStatistics();
    
    // 加载最近活动
    loadRecentActivities();
    
    // 加载图表
    loadChart();

    // 获取系统名称
    fetchSystemName();

    // 加载最近添加的产品
    fetchRecentProducts();
});

// 加载最近活动
async function loadRecentActivities() {
    try {
        const response = await fetch('/api/products/recent-activities');
        if (!response.ok) {
            throw new Error('Failed to fetch recent activities');
        }
        
        const activities = await response.json();
        updateActivitiesList(activities);
    } catch (error) {
        console.error('Error loading recent activities:', error);
    }
}

// 更新活动列表
function updateActivitiesList(activities) {
    const activityList = document.querySelector('.activity-list');
    if (!activityList) return;
    
    // 清空现有内容
    activityList.innerHTML = '';
    
    // 只显示最近三条活动
    activities.slice(0, 3).forEach(activity => {
        const activityItem = document.createElement('div');
        activityItem.className = 'activity-item';
        
        // 根据活动类型选择图标颜色
        let iconColor = 'blue';
        if (activity.stage.includes('运输')) {
            iconColor = 'green';
        } else if (activity.stage.includes('检测')) {
            iconColor = 'orange';
        }
        
        activityItem.innerHTML = `
            <div class="activity-icon ${iconColor}">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <rect x="1" y="3" width="15" height="13"></rect>
                    <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
                    <circle cx="5.5" cy="18.5" r="2.5"></circle>
                    <circle cx="18.5" cy="18.5" r="2.5"></circle>
                </svg>
            </div>
            <div class="activity-content">
                <h4>${activity.stage}</h4>
                <p>${activity.productName} (批次: ${activity.batchNumber}) ${activity.details}</p>
                <p class="activity-time">${new Date(activity.timestamp).toLocaleString()}</p>
            </div>
        `;
        
        activityList.appendChild(activityItem);
    });
}

// 加载统计数据
async function loadStatistics() {
    try {
        const response = await fetch('/api/products/statistics');
        if (!response.ok) {
            throw new Error('Failed to fetch statistics');
        }
        
        const stats = await response.json();
        updateStatistics(stats);
    } catch (error) {
        console.error('Error loading statistics:', error);
        showNotification('获取统计数据失败：' + error.message, 'error');
    }
}

// 更新统计数据
function updateStatistics(stats) {
    // 更新产品总数
    const totalProducts = document.querySelector('.stats-cards .card:nth-child(1) .stat-value');
    if (totalProducts) {
        totalProducts.textContent = stats.totalProducts.toLocaleString();
    }
    
    // 更新活跃追踪
    const activeTracking = document.querySelector('.stats-cards .card:nth-child(2) .stat-value');
    if (activeTracking) {
        activeTracking.textContent = stats.activeTracking.toLocaleString();
    }

    // 更新二维码扫描次数
    const qrScans = document.querySelector('.stats-cards .card:nth-child(3) .stat-value');
    if (qrScans) {
        qrScans.textContent = stats.qrScans.toLocaleString();
    }
    
    // 更新增长率
    const growthElements = document.querySelectorAll('.stat-change');
    growthElements.forEach(element => {
        const cardType = element.closest('.card').querySelector('h3').textContent;
        let growthRate = 0;
        
        if (cardType === '产品总数') {
            growthRate = stats.totalProductsGrowth || 0;
        } else if (cardType === '活跃追踪') {
            growthRate = stats.activeTrackingGrowth || 0;
        } else if (cardType === '二维码扫描') {
            growthRate = stats.qrScansGrowth || 0;
        }
        
        element.textContent = `较上月增长 ${growthRate}%`;
    });
}

// 加载图表
function loadChart() {
    const ctx = document.getElementById('trackingChart');
    if (!ctx) return;
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
            datasets: [{
                label: '产品追踪次数',
                data: [65, 59, 80, 81, 56, 55],
                fill: false,
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// 获取系统名称
async function fetchSystemName() {
    console.log('开始获取系统名称...');
    try {
        const response = await fetch('/api/settings/system-name', {
            method: 'GET',
            headers: {
                'Accept': 'text/plain',
                'Content-Type': 'text/plain'
            }
        });
        console.log('收到响应:', response.status, response.statusText);
        
        if (response.ok) {
            const systemName = await response.text();
            console.log('获取到的系统名称:', systemName);
            document.getElementById('welcome-title').textContent = `欢迎使用${systemName}`;
        } else {
            console.error('获取系统名称失败:', response.status, response.statusText);
            document.getElementById('welcome-title').textContent = '欢迎使用产品溯源管理系统';
        }
    } catch (error) {
        console.error('获取系统名称出错:', error);
        document.getElementById('welcome-title').textContent = '欢迎使用产品溯源管理系统';
    }
} 