// 产品类型分布图表
function initProductTypeChart() {
    const ctx = document.getElementById('productTypeChart').getContext('2d');
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['农产品', '乳制品', '食用油', '其他'],
            datasets: [{
                data: [35, 25, 20, 20],
                backgroundColor: [
                    '#4CAF50',
                    '#2196F3',
                    '#FFC107',
                    '#9E9E9E'
                ],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'right',
                    labels: {
                        padding: 20,
                        font: {
                            size: 12
                        }
                    }
                }
            }
        }
    });
}

// 月度扫描趋势图表
function initMonthlyScanChart() {
    const ctx = document.getElementById('monthlyScanChart').getContext('2d');
    
    // 如果已存在图表实例，先销毁它
    if (window.monthlyScanChart instanceof Chart) {
        window.monthlyScanChart.destroy();
    }
    
    // 获取月度扫描数据
    fetch('/api/page-visits/monthly-stats')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Received monthly data:', data);
            
            if (!data || data.length === 0) {
                // 如果没有数据，显示提示信息
                const noDataMessage = document.createElement('div');
                noDataMessage.className = 'no-data-message';
                noDataMessage.textContent = '暂无月度扫描数据';
                noDataMessage.style.textAlign = 'center';
                noDataMessage.style.padding = '20px';
                noDataMessage.style.color = '#666';
                ctx.canvas.parentNode.appendChild(noDataMessage);
                return;
            }

            // 处理月份标签
            const labels = data.map(item => {
                const [year, month] = item.month.split('-');
                return `${year}年${month}月`;
            }).reverse(); // 反转数组以按时间顺序显示

            const counts = data.map(item => item.count).reverse(); // 反转数组以匹配标签顺序
            
            // 创建新图表
            window.monthlyScanChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '扫描次数',
                        data: counts,
                        borderColor: '#4CAF50',
                        backgroundColor: 'rgba(76, 175, 80, 0.1)',
                        fill: true,
                        tension: 0.4,
                        borderWidth: 2,
                        pointRadius: 4,
                        pointBackgroundColor: '#4CAF50',
                        pointBorderColor: '#fff',
                        pointHoverRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: false
                        },
                        tooltip: {
                            mode: 'index',
                            intersect: false,
                            backgroundColor: 'rgba(0, 0, 0, 0.8)',
                            padding: 10,
                            titleColor: '#fff',
                            bodyColor: '#fff',
                            borderColor: '#4CAF50',
                            borderWidth: 1
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: '扫描次数',
                                font: {
                                    size: 12
                                }
                            },
                            grid: {
                                color: 'rgba(0, 0, 0, 0.1)'
                            }
                        },
                        x: {
                            grid: {
                                display: false
                            }
                        }
                    },
                    interaction: {
                        mode: 'nearest',
                        axis: 'x',
                        intersect: false
                    }
                }
            });
        })
        .catch(error => {
            console.error('获取月度扫描数据失败:', error);
            showNotification('获取月度扫描数据失败: ' + error.message, 'error');
        });
}

// 设置增长率颜色
function setGrowthColor(elementId, value) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    // 移除现有的颜色类
    element.classList.remove('up', 'down', 'neutral');
    
    // 添加新的颜色类
    if (value > 0) {
        element.classList.add('up');
        element.textContent = `↑ ${value}%`;
    } else if (value < 0) {
        element.classList.add('down');
        element.textContent = `↓ ${Math.abs(value)}%`;
    } else {
        element.classList.add('neutral');
        element.textContent = `${value}%`;
    }
}

// 获取统计数据
async function fetchStatistics() {
    try {
        // 获取产品统计数据
        const productStatsResponse = await fetch('/api/products/statistics');
        const productStats = await productStatsResponse.json();
        
        // 获取用户统计数据
        const userStatsResponse = await fetch('/api/users/statistics');
        const userStats = await userStatsResponse.json();
        
        // 更新统计数据
        document.getElementById('qrScanCount').textContent = productStats.qrScans.toLocaleString();
        document.getElementById('avgTrackingTime').textContent = `${productStats.avgTrackingTime}天`;
        document.getElementById('activeUsers').textContent = userStats.activeUsers.toLocaleString();
        
        // 更新增长率
        document.getElementById('qrScanGrowth').textContent = `${productStats.qrScansGrowth}%`;
        document.getElementById('trackingGrowth').textContent = `${productStats.activeTrackingGrowth}%`;
        document.getElementById('userGrowth').textContent = `${userStats.activeUsersGrowth}%`;
        
        // 设置增长率颜色
        setGrowthColor('qrScanGrowth', productStats.qrScansGrowth);
        setGrowthColor('trackingGrowth', productStats.activeTrackingGrowth);
        setGrowthColor('userGrowth', userStats.activeUsersGrowth);
    } catch (error) {
        console.error('Error fetching statistics:', error);
        showNotification('获取统计数据失败', 'error');
    }
}

// 获取供应链分析数据
async function fetchSupplyChainAnalysis() {
    try {
        const response = await fetch('/api/analysis/supply-chain');
        if (!response.ok) {
            throw new Error('获取供应链分析数据失败');
        }
        const data = await response.json();
        
        // 更新供应链节点分析图表
        const supplyChainCtx = document.getElementById('supplyChainChart').getContext('2d');
        
        // 如果已存在图表实例，先销毁它
        if (window.supplyChainChart instanceof Chart) {
            window.supplyChainChart.destroy();
        }

        // 检查数据是否为空
        if (!data.labels || data.labels.length === 0 || !data.datasets || !data.datasets[0].data || data.datasets[0].data.length === 0) {
            // 显示无数据提示
            const noDataMessage = document.createElement('div');
            noDataMessage.className = 'no-data-message';
            noDataMessage.textContent = '暂无供应链节点数据';
            noDataMessage.style.textAlign = 'center';
            noDataMessage.style.padding = '20px';
            noDataMessage.style.color = '#666';
            supplyChainCtx.canvas.parentNode.appendChild(noDataMessage);
            return;
        }
        
        // 创建新图表
        window.supplyChainChart = new Chart(supplyChainCtx, {
            type: 'bar',
            data: {
                labels: data.labels,
                datasets: [{
                    label: '平均停留时间（天）',
                    data: data.datasets[0].data,
                    backgroundColor: '#2196F3',
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: '天数'
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error:', error);
        showNotification('获取供应链分析数据失败：' + error.message, 'error');
    }
}

// 获取产品类型分布数据
async function fetchProductTypeDistribution() {
    try {
        const response = await fetch('/api/analysis/product-types');
        if (!response.ok) {
            throw new Error('获取产品类型分布数据失败');
        }
        const data = await response.json();
        
        // 更新产品类型分布图表
        const productTypeCtx = document.getElementById('productTypeChart').getContext('2d');
        new Chart(productTypeCtx, {
            type: 'doughnut',
            data: {
                labels: data.labels,
                datasets: [{
                    data: data.datasets[0].data,
                    backgroundColor: [
                        '#4CAF50',
                        '#2196F3',
                        '#FFC107',
                        '#9E9E9E'
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            padding: 20,
                            font: {
                                size: 12
                            }
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error:', error);
        showNotification('获取产品类型分布数据失败：' + error.message, 'error');
    }
}

// 获取热门产品排行数据
async function fetchTopProducts() {
    try {
        const response = await fetch('/api/analysis/top-products');
        if (!response.ok) {
            throw new Error('获取热门产品排行数据失败');
        }
        const data = await response.json();
        
        // 更新热门产品排行列表
        const rankingList = document.querySelector('.ranking-list');
        rankingList.innerHTML = '';
        
        data.forEach((product, index) => {
            const rankingItem = document.createElement('div');
            rankingItem.className = 'ranking-item';
            rankingItem.innerHTML = `
                <span class="rank">${index + 1}</span>
                <div class="product-info">
                    <div class="product-name">${product.name}</div>
                    <div class="product-stats">扫描次数：${product.scanCount.toLocaleString()}</div>
                </div>
                <div class="trend ${product.growth >= 0 ? 'up' : 'down'}">
                    ${product.growth >= 0 ? '↑' : '↓'} ${Math.abs(product.growth)}%
                </div>
            `;
            rankingList.appendChild(rankingItem);
        });
    } catch (error) {
        console.error('Error:', error);
        showNotification('获取热门产品排行数据失败：' + error.message, 'error');
    }
}

// 显示通知
function showNotification(message, type = 'info') {
    // 创建通知元素
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    // 添加到页面
    document.body.appendChild(notification);
    
    // 3秒后自动移除
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// 热门产品排行图表
function initTopProductsChart() {
    const ctx = document.getElementById('productTypeChart').getContext('2d');
    
    // 获取热门产品数据
    fetch('/api/page-visits/top-products')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Received data:', data); // 添加调试日志
            
            if (!data || data.length === 0) {
                // 如果没有数据，显示提示信息
                const noDataMessage = document.createElement('div');
                noDataMessage.className = 'no-data-message';
                noDataMessage.textContent = '暂无产品访问数据';
                noDataMessage.style.textAlign = 'center';
                noDataMessage.style.padding = '20px';
                noDataMessage.style.color = '#666';
                ctx.canvas.parentNode.appendChild(noDataMessage);
                return;
            }

            const labels = data.map(item => `产品 ${item.productId || '未知'}`);
            const counts = data.map(item => item.visitCount || 0);
            
            // 销毁已存在的图表实例
            if (window.topProductsChart) {
                window.topProductsChart.destroy();
            }
            
            // 创建新图表
            window.topProductsChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '扫描次数',
                        data: counts,
                        backgroundColor: '#4CAF50',
                        borderRadius: 4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: false
                        },
                        title: {
                            display: true,
                            text: `热门产品排行 (共${data.length}个产品)`
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: '扫描次数'
                            }
                        }
                    }
                }
            });
        })
        .catch(error => {
            console.error('获取热门产品数据失败:', error);
            showNotification('获取热门产品数据失败: ' + error.message, 'error');
        });
}

// 初始化所有图表
function initCharts() {
    initTopProductsChart();
    initMonthlyScanChart();
}


function formatTaskStatus(status) {
    const mapping = {
        DRAFT: '草稿',
        CALCULATING: '计算中',
        PENDING_INITIAL_REVIEW: '待初审',
        PENDING_REVIEW: '待复审',
        PENDING_FINAL_REVIEW: '待终审',
        COMPLETED: '已完成'
    };
    return mapping[status] || status || '-';
}

async function viewTaskDetail(taskId) {
    try {
        const response = await fetch(`/api/evaluation-tasks/${taskId}`);
        if (!response.ok) {
            throw new Error('获取任务详情失败');
        }
        const task = await response.json();
        const panel = document.getElementById('taskResultPanel');
        const taskTitle = document.getElementById('selectedTaskTitle');
        const selectedTaskId = document.getElementById('selectedTaskId');
        const resultScore = document.getElementById('resultScore');
        const resultGrade = document.getElementById('resultGrade');
        const issueList = document.getElementById('issueList');

        selectedTaskId.value = task.taskId || '';
        taskTitle.textContent = `${task.taskName || '-'}（状态：${formatTaskStatus(task.status)}，指标版本：${task.indicatorVersion || '-'}）`;
        resultScore.value = task.resultScore ?? '';
        resultGrade.value = task.resultGrade || '';
        issueList.value = task.issueList || '';
        panel.style.display = 'block';
        panel.scrollIntoView({ behavior: 'smooth', block: 'start' });
    } catch (error) {
        showNotification(`获取任务详情失败：${error.message}`, 'error');
    }
}

function bindResultBackfillForm() {
    const form = document.getElementById('taskResultForm');
    if (!form) {
        return;
    }

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const taskId = document.getElementById('selectedTaskId').value;
        if (!taskId) {
            showNotification('请先选择一条任务记录', 'error');
            return;
        }

        const payload = {
            resultScore: Number(document.getElementById('resultScore').value),
            resultGrade: document.getElementById('resultGrade').value,
            issueList: document.getElementById('issueList').value
        };

        const operatorName = localStorage.getItem('username') || 'system';

        try {
            const response = await fetch(`/api/evaluation-tasks/${taskId}/results?operatorName=${encodeURIComponent(operatorName)}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!response.ok) {
                throw new Error('回填结果失败');
            }
            showNotification('任务结果回填成功', 'success');
            await loadEvaluationTasks();
            await viewTaskDetail(taskId);
        } catch (error) {
            showNotification(`回填结果失败：${error.message}`, 'error');
        }
    });
}

function bindEvaluationTaskForm() {
    const form = document.getElementById('evaluationTaskForm');
    if (!form) {
        return;
    }

    const defaultDueTime = document.getElementById('dueTime');
    if (defaultDueTime) {
        const nextDay = new Date(Date.now() + 24 * 60 * 60 * 1000);
        defaultDueTime.value = new Date(nextDay.getTime() - nextDay.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
    }

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const payload = {
            taskName: document.getElementById('taskName').value,
            elementId: document.getElementById('elementId').value,
            indicatorVersion: document.getElementById('indicatorVersion').value,
            owner: localStorage.getItem('username') || 'system',
            dueTime: document.getElementById('dueTime').value,
            dataLevel: document.getElementById('taskDataLevel').value,
            sensitiveFlag: document.getElementById('taskSensitiveFlag').value === 'true'
        };

        try {
            const response = await fetch('/api/evaluation-tasks', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!response.ok) {
                throw new Error('创建失败');
            }
            form.reset();
            showNotification('评估任务创建成功', 'success');
            await loadEvaluationTasks();
        } catch (error) {
            showNotification(`评估任务创建失败：${error.message}`, 'error');
        }
    });
}

async function triggerTaskCalculation(taskId) {
    try {
        const operatorName = localStorage.getItem('username') || 'system';
        const response = await fetch(`/api/evaluation-tasks/${taskId}/submit-calc?operatorName=${encodeURIComponent(operatorName)}`, {
            method: 'POST'
        });
        if (!response.ok) {
            throw new Error('提交计算失败');
        }
        showNotification('已提交计算并回填结果', 'success');
        await loadEvaluationTasks();
    } catch (error) {
        showNotification(`提交失败：${error.message}`, 'error');
    }
}

async function loadEvaluationTasks() {
    const tableBody = document.getElementById('evaluationTaskTableBody');
    if (!tableBody) {
        return;
    }

    try {
        const response = await fetch('/api/evaluation-tasks');
        if (!response.ok) {
            throw new Error('获取任务失败');
        }
        const tasks = await response.json();
        if (!Array.isArray(tasks) || tasks.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7">暂无评估任务，请先创建。</td></tr>';
            return;
        }

        tableBody.innerHTML = tasks.map(task => `
            <tr>
                <td>${task.taskName || '-'}</td>
                <td>${task.elementId || '-'}</td>
                <td>${task.indicatorVersion || '-'}</td>
                <td>${formatTaskStatus(task.status)}</td>
                <td>${task.resultScore || '-'} / ${task.resultGrade || '-'}</td>
                <td title="${task.issueList || ''}">${(task.issueList || '-').slice(0, 28)}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn btn-outline" onclick="triggerTaskCalculation('${task.taskId}')">提交计算</button>
                        <button class="btn btn-outline" onclick="viewTaskDetail('${task.taskId}')">审阅/回填</button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        tableBody.innerHTML = `<tr><td colspan="7">${error.message}</td></tr>`;
    }
}

// 页面加载完成后初始化图表
document.addEventListener('DOMContentLoaded', function() {
    initCharts();
    fetchStatistics();
    fetchSupplyChainAnalysis();
    fetchProductTypeDistribution();
    bindEvaluationTaskForm();
    bindResultBackfillForm();
    loadEvaluationTasks();
});
