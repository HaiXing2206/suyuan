// 等待所有资源加载完成
window.addEventListener('load', () => {
    const { createApp, ref } = Vue;
    const { ElMessage } = ElementPlus;
    const { Document, TrendCharts, Goods } = ElementPlusIconsVue;

    // 检查登录状态
    function checkLogin() {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = 'login.html';
            return false;
        }
        return true;
    }

    // 在页面加载时检查登录状态
    if (!checkLogin()) {
        throw new Error('未登录');
    }

    console.log('Vue version:', Vue.version);
    console.log('ElementPlus:', ElementPlus);
    console.log('ElementPlusIconsVue:', ElementPlusIconsVue);

    const app = createApp({
        setup() {
            console.log('App setup running');
            const activeIndex = ref('1');

            const handleSelect = (key) => {
                console.log('Menu selected:', key);
                activeIndex.value = key;
            };

            const handleLogout = () => {
                localStorage.removeItem('token');
                window.location.href = 'login.html';
            };

            return {
                activeIndex,
                handleSelect,
                handleLogout
            };
        }
    });

    // 注册Element Plus组件
    app.use(ElementPlus);

    // 注册图标组件
    app.component('Document', Document);
    app.component('TrendCharts', TrendCharts);
    app.component('Goods', Goods);

    // 检查组件是否已定义
    console.log('TraceabilityUI defined:', typeof TraceabilityUI !== 'undefined');
    console.log('DataAnalysis defined:', typeof DataAnalysis !== 'undefined');
    console.log('ProductManagement defined:', typeof ProductManagement !== 'undefined');

    // 注册自定义组件
    if (typeof TraceabilityUI !== 'undefined') {
        app.component('traceability-ui', TraceabilityUI);
    }
    if (typeof DataAnalysis !== 'undefined') {
        app.component('data-analysis', DataAnalysis);
    }
    if (typeof ProductManagement !== 'undefined') {
        app.component('product-management', ProductManagement);
    }

    // 挂载应用
    try {
        app.mount('#app');
        console.log('Vue app mounted successfully');
    } catch (error) {
        console.error('Error mounting Vue app:', error);
    }
});