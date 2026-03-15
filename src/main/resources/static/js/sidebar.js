// 加载侧边栏
async function loadSidebar() {
    try {
        const response = await fetch('/html/components/sidebar.html');
        const html = await response.text();
        document.querySelector('.sidebar-container').innerHTML = html;
        
        // 设置当前页面的激活状态
        const currentPage = window.location.pathname.split('/').pop();
        const navItems = document.querySelectorAll('.sidebar nav li');
        
        navItems.forEach(item => {
            const link = item.querySelector('a');
            const href = link.getAttribute('href');
            
            if (href === currentPage || 
                (currentPage === '' && href === 'index.html') ||
                (currentPage === 'index.html' && href === 'index.html')) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });

        // 加载系统名称
        await fetchSystemName();

        // 初始化侧边栏显隐开关
        initSidebarToggle();

        // 触发侧边栏加载完成事件
        window.dispatchEvent(new Event('sidebarLoaded'));
    } catch (error) {
        console.error('Error loading sidebar:', error);
    }
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
            document.getElementById('system-name').textContent = systemName;
        } else {
            console.error('获取系统名称失败:', response.status, response.statusText);
            document.getElementById('system-name').textContent = '数据要素评估与审计平台';
        }
    } catch (error) {
        console.error('获取系统名称出错:', error);
        document.getElementById('system-name').textContent = '数据要素评估与审计平台';
    }
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', loadSidebar);

function createToggleIcon(isCollapsed) {
    return isCollapsed
        ? '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon"><line x1="3" y1="12" x2="21" y2="12"></line><polyline points="9 18 15 12 9 6"></polyline></svg>'
        : '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon"><line x1="3" y1="12" x2="21" y2="12"></line><polyline points="15 18 9 12 15 6"></polyline></svg>';
}

function applySidebarState(isCollapsed) {
    document.body.classList.toggle('sidebar-collapsed', isCollapsed);
    localStorage.setItem('sidebarCollapsed', String(isCollapsed));

    const toggleBtn = document.getElementById('sidebarToggleBtn');
    if (toggleBtn) {
        toggleBtn.innerHTML = createToggleIcon(isCollapsed);
        toggleBtn.setAttribute('aria-label', isCollapsed ? '展开侧边栏' : '隐藏侧边栏');
        toggleBtn.setAttribute('title', isCollapsed ? '展开侧边栏' : '隐藏侧边栏');
    }
}

function initSidebarToggle() {
    const header = document.querySelector('.header .search-bar') || document.querySelector('.header');
    if (!header || document.getElementById('sidebarToggleBtn')) {
        return;
    }

    const toggleBtn = document.createElement('button');
    toggleBtn.id = 'sidebarToggleBtn';
    toggleBtn.type = 'button';
    toggleBtn.className = 'sidebar-toggle-btn';

    const savedState = localStorage.getItem('sidebarCollapsed') === 'true';
    applySidebarState(savedState);

    toggleBtn.addEventListener('click', () => {
        const isCollapsed = !document.body.classList.contains('sidebar-collapsed');
        applySidebarState(isCollapsed);
    });

    header.prepend(toggleBtn);
    applySidebarState(savedState);
}


// 侧边栏菜单项
const menuItems = [
    {
        title: '首页',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>',
        link: '/'
    },
    {
        title: '数据要素台账',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path><line x1="7" y1="7" x2="7.01" y2="7"></line></svg>',
        link: '/products'
    },
    {
        title: '用户审核',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>',
        link: '/users'
    },
    {
        title: '数据治理',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 21H3"></path><path d="M3 10h18"></path><path d="M3 7h18"></path><path d="M3 14h18"></path><path d="M3 17h18"></path></svg>',
        link: '/analysis'
    },
    {
        title: '评估任务',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"></path><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path></svg>',
        link: '/tasks'
    },
    {
        title: '报告中心与归档',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><line x1="10" y1="9" x2="8" y2="9"></line></svg>',
        link: '/reports'
    },
    {
        title: '系统设置',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"></path><circle cx="12" cy="12" r="3"></circle></svg>',
        link: '/settings'
    }
]; 