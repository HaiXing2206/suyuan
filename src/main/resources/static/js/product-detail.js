document.addEventListener('DOMContentLoaded', function() {
    // 获取URL参数
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id') || 'B2023-042';

    // 更新二维码链接
    const qrcodeLink = document.getElementById('qrcode-link');
    qrcodeLink.href = `qrcode.html?id=${productId}`;

    // 加载产品数据
    getProductData(productId).then(data => {
        updateProductDetails(data);
    });

    // 添加记录按钮点击事件
    const addTraceBtn = document.getElementById('add-trace-btn');
    addTraceBtn.addEventListener('click', () => {
        showAddTraceModal(productId);
    });

    // 已完成按钮点击事件
    const completeBtn = document.getElementById('complete-btn');
    completeBtn.addEventListener('click', async function() {
        if (!confirm('确定要将此产品标记为已完成吗？')) {
            return;
        }

        try {
            const response = await fetch(`/api/products/${productId}/complete`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                alert('产品已成功标记为已完成！');
                // 重定向到产品列表页面
                window.location.href = 'products.html';
            } else {
                throw new Error('操作失败');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('操作失败，请稍后重试');
        }
    });

    // 标签页切换
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');

            // 移除所有活动状态
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabPanes.forEach(pane => pane.classList.remove('active'));

            // 设置当前标签为活动状态
            this.classList.add('active');
            document.getElementById(tabId).classList.add('active');
        });
    });

    // 获取溯源记录
    getTraceRecords(productId);
});

// 显示添加记录模态框
function showAddTraceModal(productId) {
    // 创建模态框
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3>添加供应链记录</h3>
                <button class="close-btn">&times;</button>
            </div>
            <div class="modal-body">
                <form id="add-trace-form">
                    <div class="form-group">
                        <label for="action">操作类型</label>
                        <input type="text" id="action" name="action" required placeholder="例如：运输、入库、出库等">
                    </div>
                    <div class="form-group">
                        <label for="location">位置</label>
                        <input type="text" id="location" name="location" required placeholder="例如：仓库A、配送中心等">
                    </div>
                    <div class="form-group">
                        <label for="details">详细信息</label>
                        <textarea id="details" name="details" required placeholder="请输入详细信息"></textarea>
                    </div>
                    <div class="form-group">
                        <label for="operator">操作人</label>
                        <input type="text" id="operator" name="operator" required placeholder="请输入操作人姓名">
                    </div>
                    <div class="form-actions">
                        <button type="button" class="btn btn-outline" id="cancel-btn">取消</button>
                        <button type="submit" class="btn btn-primary">提交</button>
                    </div>
                </form>
            </div>
        </div>
    `;

    // 添加到页面
    document.body.appendChild(modal);

    // 关闭按钮事件
    const closeBtn = modal.querySelector('.close-btn');
    const cancelBtn = modal.querySelector('#cancel-btn');
    const closeModal = () => {
        modal.remove();
    };
    closeBtn.addEventListener('click', closeModal);
    cancelBtn.addEventListener('click', closeModal);

    // 表单提交事件
    const form = modal.querySelector('#add-trace-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const formData = {
            action: form.action.value,
            location: form.location.value,
            details: form.details.value,
            operator: form.operator.value
        };

        try {
            const response = await fetch(`/api/products/${productId}/trace`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || '添加记录失败');
            }

            const result = await response.json();
            alert('记录添加成功！');
            closeModal();
            
            // 获取并显示最新的记录
            await getTraceRecords(productId);
        } catch (error) {
            alert('添加记录失败：' + error.message);
        }
    });
}

// 获取产品数据
async function getProductData(productId) {
    try {
        const response = await fetch(`/api/products/${productId}`);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to fetch product data');
        }
        const data = await response.json();
        
        // 将区块链时间戳转换为日期
        const productionDate = new Date(Number(data.productionDate) * 1000);
        
        return {
            name: data.name,
            batch: data.batchNumber,
            spec: data.productSpec || '未指定',
            desc: data.productDescription || '暂无描述',
            date: new Date(data.productionDate).toLocaleDateString(),
            manufacturer: data.manufacturer,
            origin: data.origin,
            location: data.location || '未知',
            status: data.status || '未知',
            updateTime: new Date().toLocaleString(),
            eta: data.eta || '未知'
        };
    } catch (error) {
        console.error('Error fetching product data:', error);
        // 显示错误信息
        document.querySelector('.content').innerHTML = `
            <div class="error-message">
                <h2>无法加载产品信息</h2>
                <p>${error.message}</p>
            </div>
        `;
        return null;
    }
}

// 更新产品详情
function updateProductDetails(data) {
    if (!data) {
        return; // 错误信息已经在 getProductData 中显示
    }
    
    // 更新标题
    document.getElementById('product-name').textContent = data.name;
    document.getElementById('product-batch').textContent = `批次号: ${data.batch}`;

    // 更新基本信息
    document.getElementById('info-name').textContent = data.name;
    document.getElementById('info-spec').textContent = data.spec;
    document.getElementById('info-desc').textContent = data.desc;

    // 更新生产信息
    const dateElement = document.getElementById('info-date');
    dateElement.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
        </svg>
        ${data.date}
    `;

    const batchElement = document.getElementById('info-batch');
    batchElement.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <polygon points="12 2 2 7 12 12 22 7 12 2"></polygon>
            <polyline points="2 17 12 22 22 17"></polyline>
            <polyline points="2 12 12 17 22 12"></polyline>
        </svg>
        ${data.batch}
    `;

    const manufacturerElement = document.getElementById('info-manufacturer');
    manufacturerElement.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <path d="M2 20a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V8l-7 5V8l-7 5V4a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"></path>
        </svg>
        ${data.manufacturer}
    `;

    const originElement = document.getElementById('info-origin');
    originElement.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
            <circle cx="12" cy="10" r="3"></circle>
        </svg>
        ${data.origin}
    `;

    // 更新当前状态
    document.getElementById('info-location').textContent = data.location;
    document.getElementById('info-status').innerHTML = `<span class="badge green">${data.status}</span>`;

    const updateTimeElement = document.getElementById('info-update-time');
    updateTimeElement.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <circle cx="12" cy="12" r="10"></circle>
            <polyline points="12 6 12 12 16 14"></polyline>
        </svg>
        ${data.updateTime}
    `;

    document.getElementById('info-eta').textContent = data.eta;
}

// 获取溯源记录
async function getTraceRecords(productId) {
    try {
        const response = await fetch(`/api/products/${productId}/trace`);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to fetch trace records');
        }
        const records = await response.json();
        updateTraceRecords(records);
        updateBlockchainRecords(records); // 同时更新区块链记录
    } catch (error) {
        console.error('Error fetching trace records:', error);
        alert('获取溯源记录失败：' + error.message);
    }
}

// 获取并更新当前状态
function updateCurrentStatus(records) {
    if (!records || records.length === 0) return;
    
    // 获取最新的一条记录
    const latestRecord = records[0];
    
    // 更新状态显示
    const locationElement = document.getElementById('info-location');
    const statusElement = document.getElementById('info-status');
    const updateTimeElement = document.getElementById('info-update-time');
    
    if (locationElement) {
        locationElement.textContent = latestRecord.location;
    }
    
    if (statusElement) {
        const statusBadge = statusElement.querySelector('.badge');
        if (statusBadge) {
            statusBadge.textContent = latestRecord.stage;
        }
    }
    
    if (updateTimeElement) {
        const timeText = updateTimeElement.querySelector('span');
        if (timeText) {
            timeText.textContent = new Date(latestRecord.timestamp).toLocaleString();
        }
    }
}

// 更新溯源记录显示
function updateTraceRecords(records) {
    const timeline = document.querySelector('.timeline');
    if (!timeline) return;

    // 清空现有记录
    timeline.innerHTML = '';

    // 添加新记录
    records.forEach((record, index) => {
        const isLatest = index === 0;
        const timelineItem = document.createElement('div');
        timelineItem.className = 'timeline-item';
        timelineItem.innerHTML = `
            <div class="timeline-marker ${isLatest ? 'active' : ''}"></div>
            <div class="timeline-content">
                <div class="timeline-header">
                    <h4>${record.stage}</h4>
                    <span class="badge ${isLatest ? 'green' : 'outline'}">${isLatest ? '当前位置' : '已完成'}</span>
                </div>
                <p>${record.details}</p>
                <p class="timeline-time">${new Date(record.timestamp).toLocaleString()}</p>
            </div>
        `;
        timeline.appendChild(timelineItem);
    });
    
    // 更新当前状态
    updateCurrentStatus(records);
}

// 更新区块链记录
function updateBlockchainRecords(records) {
    const tbody = document.getElementById('blockchain-records');
    if (!tbody) return;

    // 清空现有内容
    tbody.innerHTML = '';

    // 按时间倒序排序记录
    records.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

    // 添加记录到表格
    records.forEach(record => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="hash">${record.transactionHash}</td>
            <td>${record.stage}</td>
            <td>${record.location}</td>
            <td>${record.operator}</td>
            <td>${new Date(record.timestamp).toLocaleString()}</td>
            <td>${record.details}</td>
        `;
        tbody.appendChild(tr);
    });
}