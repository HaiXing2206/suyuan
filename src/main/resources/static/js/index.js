async function fetchJson(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`请求失败: ${url} (${response.status})`);
    }
    return response.json();
}

async function loadDashboard() {
    try {
        const [dataElements, evaluationTasks] = await Promise.all([
            fetchJson('/api/data-elements'),
            fetchJson('/api/evaluation-tasks')
        ]);

        const pendingCounts = await loadPendingApprovals();

        updateStatistics(dataElements, evaluationTasks, pendingCounts.total);
        displayRecentDataElements(dataElements);
        updateActivitiesList(buildRecentActivities(dataElements, evaluationTasks));
    } catch (error) {
        console.error('加载首页数据失败:', error);
        showNotification('首页数据加载失败：' + error.message, 'error');
    }
}

async function loadPendingApprovals() {
    const approverRoles = ['INITIAL_REVIEW', 'REVIEW', 'FINAL_REVIEW'];

    const results = await Promise.all(
        approverRoles.map(async (role) => {
            try {
                return await fetchJson(`/api/approval-flows/pending?approverRole=${role}`);
            } catch (error) {
                console.warn(`加载待审批任务失败: ${role}`, error);
                return [];
            }
        })
    );

    const taskIdSet = new Set();
    results.flat().forEach((task) => {
        if (task && task.taskId) {
            taskIdSet.add(task.taskId);
        }
    });

    return { total: taskIdSet.size };
}

function updateStatistics(dataElements, evaluationTasks, pendingApprovalTotal) {
    const totalElements = document.querySelector('.stats-cards .card:nth-child(1) .stat-value');
    const inProgressTasks = document.querySelector('.stats-cards .card:nth-child(2) .stat-value');
    const pendingItems = document.querySelector('.stats-cards .card:nth-child(3) .stat-value');

    if (totalElements) {
        totalElements.textContent = dataElements.length.toLocaleString();
    }

    if (inProgressTasks) {
        const progressStatuses = ['CREATED', 'SUBMITTED', 'IN_REVIEW', 'REVIEWING'];
        const total = evaluationTasks.filter((task) => progressStatuses.includes(task.status)).length;
        inProgressTasks.textContent = total.toLocaleString();
    }

    if (pendingItems) {
        pendingItems.textContent = pendingApprovalTotal.toLocaleString();
    }

    document.querySelectorAll('.stat-change').forEach((element) => {
        element.textContent = '较上月增长 0%';
    });
}

function displayRecentDataElements(dataElements) {
    const container = document.querySelector('.recent-products');
    if (!container) {
        return;
    }

    container.innerHTML = '';

    if (!Array.isArray(dataElements) || dataElements.length === 0) {
        container.innerHTML = '<div class="no-data">暂无台账数据</div>';
        return;
    }

    const recentItems = [...dataElements]
        .sort((a, b) => new Date(b.createdAt || b.updatedAt || 0) - new Date(a.createdAt || a.updatedAt || 0))
        .slice(0, 3);

    recentItems.forEach((item) => {
        const row = document.createElement('div');
        row.className = 'product-item';
        row.innerHTML = `
            <div class="product-info">
                <div class="info-label">要素名称</div>
                <div class="info-value">${item.elementName || '-'}</div>
            </div>
            <div class="product-info">
                <div class="info-label">所属部门</div>
                <div class="info-value">${item.department || '-'}</div>
            </div>
            <div class="product-info">
                <div class="info-label">更新时间</div>
                <div class="info-value">${formatDateTime(item.updatedAt || item.createdAt)}</div>
            </div>
        `;
        container.appendChild(row);
    });
}

function buildRecentActivities(dataElements, evaluationTasks) {
    const elementActivities = (dataElements || []).map((item) => ({
        time: item.updatedAt || item.createdAt,
        title: '台账更新',
        description: `${item.elementName || item.elementId || '未知要素'} · ${item.department || '未分配部门'}`
    }));

    const taskActivities = (evaluationTasks || []).map((task) => ({
        time: task.updatedAt || task.createdAt,
        title: `评估任务${formatTaskStatus(task.status)}`,
        description: `${task.taskName || task.taskId || '未知任务'} · 负责人 ${task.owner || '未指定'}`
    }));

    return [...elementActivities, ...taskActivities]
        .filter((item) => item.time)
        .sort((a, b) => new Date(b.time) - new Date(a.time))
        .slice(0, 5);
}

function formatTaskStatus(status) {
    const statusMap = {
        CREATED: '已创建',
        SUBMITTED: '已提交',
        IN_REVIEW: '审核中',
        APPROVED: '已通过',
        REJECTED: '已驳回',
        ARCHIVED: '已归档'
    };
    return statusMap[status] ? `（${statusMap[status]}）` : '';
}

function updateActivitiesList(activities) {
    const activityList = document.querySelector('.activity-list');
    if (!activityList) {
        return;
    }

    activityList.innerHTML = '';

    if (!activities || activities.length === 0) {
        activityList.innerHTML = '<div class="no-data">暂无活动数据</div>';
        return;
    }

    activities.forEach((activity) => {
        const activityItem = document.createElement('div');
        activityItem.className = 'activity-item';
        activityItem.innerHTML = `
            <div class="activity-icon blue">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
            </div>
            <div class="activity-content">
                <h4>${activity.title}</h4>
                <p>${activity.description}</p>
                <p class="activity-time">${formatDateTime(activity.time)}</p>
            </div>
        `;

        activityList.appendChild(activityItem);
    });
}

function formatDateTime(value) {
    if (!value) {
        return '-';
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 3000);
}

async function fetchSystemName() {
    try {
        const response = await fetch('/api/settings/system-name');
        if (response.ok) {
            const systemName = await response.text();
            document.getElementById('welcome-title').textContent = `欢迎使用${systemName}`;
        }
    } catch (error) {
        console.error('获取系统名称失败:', error);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    await Promise.all([fetchSystemName(), loadDashboard()]);
});
