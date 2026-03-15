let ledgers = [];

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);
    setTimeout(() => notification.remove(), 3000);
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

async function fetchLedgers() {
    const response = await fetch('/api/data-elements');
    if (!response.ok) {
        throw new Error('加载台账失败');
    }
    return response.json();
}

async function loadLedgers() {
    try {
        ledgers = await fetchLedgers();
        renderLedgers(ledgers);
    } catch (error) {
        console.error(error);
        showNotification(error.message, 'error');
        renderLedgers([]);
    }
}

function renderLedgers(data) {
    const tbody = document.getElementById('productTableBody');
    if (!tbody) {
        return;
    }

    tbody.innerHTML = '';

    if (!Array.isArray(data) || data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">暂无台账数据</td></tr>';
        return;
    }

    const sorted = [...data].sort((a, b) => new Date(b.updatedAt || b.createdAt || 0) - new Date(a.updatedAt || a.createdAt || 0));

    sorted.forEach((item) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${item.elementId || '-'}</td>
            <td class="font-medium">${item.elementName || '-'}</td>
            <td>${item.department || '-'} / ${item.ownerName || '-'}</td>
            <td>${item.source || '-'} / ${item.purpose || '-'}</td>
            <td>${item.dataLevel || '-'} / ${item.sensitiveFlag ? '敏感' : '非敏感'}</td>
            <td>
                <div>业: ${item.businessTags || '-'}</div>
                <div>风: ${item.riskTags || '-'}</div>
                <div>质: ${item.qualityTags || '-'}</div>
            </td>
            <td title="${item.lineageInfo || ''}">${item.lineageInfo || '-'}</td>
            <td>
                <button class="btn-icon" title="删除" onclick="deleteLedger('${item.elementId}')">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                </button>
                <div style="font-size:12px;color:#64748b;">${formatDateTime(item.updatedAt || item.createdAt)}</div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function getLedgerFormPayload() {
    return {
        elementId: document.getElementById('elementId').value.trim(),
        elementName: document.getElementById('elementName').value.trim(),
        source: document.getElementById('source').value.trim(),
        ownerName: document.getElementById('ownerName').value.trim(),
        department: document.getElementById('department').value.trim(),
        purpose: document.getElementById('purpose').value.trim(),
        classificationLevel: document.getElementById('classificationLevel').value,
        dataLevel: document.getElementById('dataLevel').value,
        sensitiveFlag: document.getElementById('sensitiveFlag').value === 'true',
        archiveStatus: document.getElementById('archiveStatus').value,
        metadataDefinition: document.getElementById('metadataDefinition').value.trim(),
        qualityNote: document.getElementById('qualityNote').value.trim(),
        attachmentUrls: document.getElementById('attachmentUrls').value.trim(),
        businessTags: document.getElementById('businessTags').value.trim(),
        riskTags: document.getElementById('riskTags').value.trim(),
        qualityTags: document.getElementById('qualityTags').value.trim(),
        lineageInfo: document.getElementById('lineageInfo').value.trim()
    };
}

async function createLedger(event) {
    event.preventDefault();
    const payload = getLedgerFormPayload();

    try {
        const response = await fetch('/api/data-elements', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error('保存台账失败，请检查要素ID是否重复');
        }

        showNotification('台账保存成功', 'success');
        document.getElementById('ledgerForm').reset();
        document.getElementById('dataLevel').value = 'L2';
        document.getElementById('sensitiveFlag').value = 'false';
        document.getElementById('archiveStatus').value = 'UNARCHIVED';
        await loadLedgers();
    } catch (error) {
        console.error(error);
        showNotification(error.message, 'error');
    }
}

async function deleteLedger(elementId) {
    if (!confirm(`确定删除台账 ${elementId} 吗？`)) {
        return;
    }

    try {
        const response = await fetch(`/api/data-elements/${encodeURIComponent(elementId)}`, {
            method: 'DELETE'
        });
        if (!response.ok && response.status !== 204) {
            throw new Error('删除失败');
        }
        showNotification('删除成功', 'success');
        await loadLedgers();
    } catch (error) {
        console.error(error);
        showNotification(error.message, 'error');
    }
}

async function handleValidation(event) {
    event.preventDefault();
    const payload = {
        elementName: document.getElementById('validateElementName').value.trim(),
        department: document.getElementById('validateDepartment').value.trim(),
        formatType: document.getElementById('formatType').value,
        formatValue: document.getElementById('formatValue').value.trim()
    };

    try {
        const response = await fetch('/api/data-elements/governance/validate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error('校验请求失败');
        }

        const result = await response.json();
        const text = `完整性:${result.completeness ? '通过' : '不通过'} | 唯一性:${result.uniqueness ? '通过' : '不通过'} | 格式:${result.formatValid ? '通过' : '不通过'}`;
        document.getElementById('validateResult').textContent = text;
        showNotification('格式校验完成', 'success');
    } catch (error) {
        console.error(error);
        showNotification(error.message, 'error');
    }
}

async function handleMasking(event) {
    event.preventDefault();
    const payload = {
        type: document.getElementById('maskType').value,
        value: document.getElementById('maskValue').value.trim()
    };

    try {
        const response = await fetch('/api/data-elements/governance/mask', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error('脱敏请求失败');
        }

        const result = await response.json();
        document.getElementById('maskResult').textContent = `原始值: ${result.original || '-'} → 脱敏后: ${result.masked || '-'}`;
        showNotification('脱敏完成', 'success');
    } catch (error) {
        console.error(error);
        showNotification(error.message, 'error');
    }
}

function applyFilters() {
    const keyword = document.getElementById('searchInput').value.trim().toLowerCase();
    const level = document.getElementById('levelFilter').value;
    const sensitive = document.getElementById('sensitiveFilter').value;

    const filtered = ledgers.filter((item) => {
        const matchesKeyword = !keyword ||
            (item.elementName || '').toLowerCase().includes(keyword) ||
            (item.source || '').toLowerCase().includes(keyword) ||
            (item.ownerName || '').toLowerCase().includes(keyword) ||
            (item.department || '').toLowerCase().includes(keyword);

        const matchesLevel = level === 'all' || item.dataLevel === level;
        const matchesSensitive = sensitive === 'all' || String(item.sensitiveFlag) === sensitive;

        return matchesKeyword && matchesLevel && matchesSensitive;
    });

    renderLedgers(filtered);
}

function bindEvents() {
    document.getElementById('ledgerForm').addEventListener('submit', createLedger);
    document.getElementById('validateForm').addEventListener('submit', handleValidation);
    document.getElementById('maskForm').addEventListener('submit', handleMasking);

    document.getElementById('searchInput').addEventListener('input', applyFilters);
    document.getElementById('levelFilter').addEventListener('change', applyFilters);
    document.getElementById('sensitiveFilter').addEventListener('change', applyFilters);
}

document.addEventListener('DOMContentLoaded', async () => {
    bindEvents();
    await loadLedgers();
});
