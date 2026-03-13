let trackingChart = null;

function destroyChart() {
    if (trackingChart) {
        trackingChart.destroy();
        trackingChart = null;
    }
}

function initTrackingChart() {
    // 确保在初始化新图表前销毁旧图表
    destroyChart();
    
    const ctx = document.getElementById('trackingChart');
    if (!ctx) return;
    
    // 确保canvas是干净的
    ctx.getContext('2d').clearRect(0, 0, ctx.width, ctx.height);
    
    trackingChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
            datasets: [{
                label: '产品追踪次数',
                data: [1200, 1900, 3000, 5000, 4000, 6000],
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

// 在页面卸载时清理图表
window.addEventListener('beforeunload', destroyChart);

// 在页面加载完成后初始化图表
document.addEventListener('DOMContentLoaded', function() {
    // 确保DOM完全加载后再初始化图表
    setTimeout(initTrackingChart, 0);
});

// 移除侧边栏加载完成后的图表重新初始化
// window.addEventListener('sidebarLoaded', initTrackingChart);