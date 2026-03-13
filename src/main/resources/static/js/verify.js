document.addEventListener('DOMContentLoaded', function() {
    // 获取URL参数
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id') || 'B2023-042';

    // 加载产品数据
    // 在实际应用中，这里应该从API获取数据
    const productData = getProductData(productId);

    // 更新页面内容
    updateProductInfo(productData);
});

// 获取产品数据（模拟）
function getProductData(productId) {
    // 在实际应用中，这里应该从API获取数据
    const products = {
        'B2023-042': {
            name: '有机大米',
            batch: 'B2023-042',
            manufacturer: '绿色农场',
            date: '2023-04-15',
            origin: '黑龙江省五常市',
            location: '配送中心',
            status: '运输中',
            trackingHistory: [
                {
                    location: '配送中心',
                    status: '当前位置',
                    description: '产品已到达配送中心，等待分发至零售商',
                    time: '2023-04-18 14:23'
                },
                {
                    location: '运输中',
                    status: '已完成',
                    description: '产品从加工厂运输至配送中心',
                    time: '2023-04-17 08:45 - 2023-04-18 14:23'
                },
                {
                    location: '加工厂',
                    status: '已完成',
                    description: '产品在加工厂完成精加工和包装',
                    time: '2023-04-16 10:30 - 2023-04-17 08:45'
                },
                {
                    location: '收割',
                    status: '已完成',
                    description: '产品在原产地收割并初步加工',
                    time: '2023-04-15 07:00 - 2023-04-16 09:15'
                }
            ]
        },
        'B2023-039': {
            name: '纯牛奶',
            batch: 'B2023-039',
            manufacturer: '健康牧场',
            date: '2023-04-12',
            origin: '内蒙古呼伦贝尔',
            location: '加工厂',
            status: '包装完成',
            trackingHistory: [
                {
                    location: '加工厂',
                    status: '当前位置',
                    description: '产品在加工厂完成包装',
                    time: '2023-04-17 09:45'
                },
                {
                    location: '质检中心',
                    status: '已完成',
                    description: '产品通过质量检测',
                    time: '2023-04-16 14:30 - 2023-04-17 08:00'
                },
                {
                    location: '原料处理',
                    status: '已完成',
                    description: '原料经过处理和加工',
                    time: '2023-04-13 10:15 - 2023-04-16 09:30'
                },
                {
                    location: '牧场',
                    status: '已完成',
                    description: '原料收集和初步处理',
                    time: '2023-04-12 07:30 - 2023-04-13 09:00'
                }
            ]
        },
        'B2023-036': {
            name: '橄榄油',
            batch: 'B2023-036',
            manufacturer: '地中海农场',
            date: '2023-04-10',
            origin: '西班牙安达卢西亚',
            location: '质检中心',
            status: '质检完成',
            trackingHistory: [
                {
                    location: '质检中心',
                    status: '当前位置',
                    description: '产品已通过质量检测',
                    time: '2023-04-16 11:30'
                },
                {
                    location: '加工厂',
                    status: '已完成',
                    description: '橄榄经过压榨和过滤',
                    time: '2023-04-12 09:00 - 2023-04-15 16:45'
                },
                {
                    location: '原料处理',
                    status: '已完成',
                    description: '橄榄收获和初步处理',
                    time: '2023-04-10 08:30 - 2023-04-12 08:00'
                }
            ]
        }
    };

    return products[productId] || products['B2023-042'];
}

// 更新产品信息
function updateProductInfo(data) {
    // 更新标题
    document.getElementById('product-name').textContent = data.name;
    document.getElementById('product-batch').textContent = `批次号: ${data.batch}`;

    // 更新基本信息
    document.getElementById('info-batch').textContent = data.batch;
    document.getElementById('info-date').textContent = data.date;
    document.getElementById('info-manufacturer').textContent = data.manufacturer;
    document.getElementById('info-origin').textContent = data.origin;

    // 更新当前状态
    document.getElementById('info-location').textContent = data.location;
    document.getElementById('info-status').innerHTML = `<span class="badge green">${data.status}</span>`;

    // 更新追踪历史
    // 在实际应用中，这里应该动态生成时间线
    // 这里为了简化，我们假设HTML中已经有了默认的时间线
}