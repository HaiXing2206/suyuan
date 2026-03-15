const governanceCharts = {};

function destroyChart(key) {
    if (governanceCharts[key] instanceof Chart) {
        governanceCharts[key].destroy();
        governanceCharts[key] = null;
    }
}

function aggregateCount(items, keyGetter) {
    return items.reduce((acc, item) => {
        const key = keyGetter(item) || '未填写';
        acc[key] = (acc[key] || 0) + 1;
        return acc;
    }, {});
}

function calculateMonthlyTrend(items) {
    const monthlyMap = items.reduce((acc, item) => {
        if (!item.createdAt) {
            return acc;
        }
        const date = new Date(item.createdAt);
        if (Number.isNaN(date.getTime())) {
            return acc;
        }
        const month = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
        acc[month] = (acc[month] || 0) + 1;
        return acc;
    }, {});

    return Object.keys(monthlyMap)
        .sort()
        .map(month => ({
            month,
            count: monthlyMap[month]
        }));
}

function renderClassificationChart(items) {
    const summary = aggregateCount(items, item => item.classificationLevel);
    const labels = Object.keys(summary);
    const values = Object.values(summary);
    const ctx = document.getElementById('classificationChart').getContext('2d');

    destroyChart('classification');
    governanceCharts.classification = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: ['#4CAF50', '#2196F3', '#F59E0B', '#8B5CF6', '#94A3B8'],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'right'
                }
            }
        }
    });
}

function renderLevelChart(items) {
    const summary = aggregateCount(items, item => item.dataLevel);
    const preferredOrder = ['L1', 'L2', 'L3'];
    const labels = preferredOrder.filter(level => summary[level]).concat(
        Object.keys(summary).filter(level => !preferredOrder.includes(level))
    );
    const values = labels.map(label => summary[label]);
    const ctx = document.getElementById('levelChart').getContext('2d');

    destroyChart('level');
    governanceCharts.level = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: '要素数量',
                data: values,
                backgroundColor: '#2563EB',
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
        }
    });
}

function renderDepartmentChart(items) {
    const summary = aggregateCount(items, item => item.department);
    const sorted = Object.entries(summary)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 5);

    const ctx = document.getElementById('departmentChart').getContext('2d');
    destroyChart('department');
    governanceCharts.department = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: sorted.map(([name]) => name),
            datasets: [{
                label: '要素数量',
                data: sorted.map(([, count]) => count),
                backgroundColor: '#0EA5E9',
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            indexAxis: 'y',
            plugins: { legend: { display: false } },
            scales: { x: { beginAtZero: true, ticks: { precision: 0 } } }
        }
    });
}

function renderMonthlyTrendChart(items) {
    const trend = calculateMonthlyTrend(items);
    const ctx = document.getElementById('monthlyLedgerChart').getContext('2d');

    destroyChart('monthly');
    governanceCharts.monthly = new Chart(ctx, {
        type: 'line',
        data: {
            labels: trend.map(item => item.month),
            datasets: [{
                label: '新增要素',
                data: trend.map(item => item.count),
                borderColor: '#16A34A',
                backgroundColor: 'rgba(22, 163, 74, 0.12)',
                fill: true,
                tension: 0.3,
                borderWidth: 2,
                pointRadius: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
        }
    });
}

function renderGovernanceStats(items) {
    const total = items.length;
    const sensitiveCount = items.filter(item => item.sensitiveFlag).length;
    const archiveCount = items.filter(item => item.archiveStatus === 'ARCHIVED').length;
    const thirtyDaysAgo = Date.now() - 30 * 24 * 60 * 60 * 1000;
    const recentIncrement = items.filter(item => {
        if (!item.createdAt) {
            return false;
        }
        const time = new Date(item.createdAt).getTime();
        return !Number.isNaN(time) && time >= thirtyDaysAgo;
    }).length;

    const sensitiveRatio = total === 0 ? 0 : (sensitiveCount / total) * 100;
    const archiveRatio = total === 0 ? 0 : (archiveCount / total) * 100;

    document.getElementById('ledgerTotalCount').textContent = total.toLocaleString();
    document.getElementById('sensitiveRatio').textContent = `${sensitiveRatio.toFixed(1)}%`;
    document.getElementById('archiveRatio').textContent = `${archiveRatio.toFixed(1)}%`;
    document.getElementById('recentIncrement').textContent = recentIncrement.toLocaleString();

    document.getElementById('sensitiveCountText').textContent = `敏感 ${sensitiveCount} 条`;
    document.getElementById('archiveCountText').textContent = `已归档 ${archiveCount} 条`;
}

async function loadGovernanceDashboard() {
    try {
        const response = await fetch('/api/data-elements');
        if (!response.ok) {
            throw new Error('获取台账数据失败');
        }
        const items = await response.json();

        renderGovernanceStats(items);
        renderClassificationChart(items);
        renderLevelChart(items);
        renderDepartmentChart(items);
        renderMonthlyTrendChart(items);
    } catch (error) {
        console.error('加载治理看板失败:', error);
        showNotification(`加载治理看板失败：${error.message}`, 'error');
    }
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
    const hasGovernanceDashboard = !!document.getElementById('monthlyLedgerChart');
    const hasTaskPage = !!document.getElementById('evaluationTaskForm');

    if (hasGovernanceDashboard) {
        loadGovernanceDashboard();
    }

    if (hasTaskPage) {
        bindEvaluationTaskForm();
        bindResultBackfillForm();
        loadEvaluationTasks();
    }
});
