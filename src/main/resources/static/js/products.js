// 产品数据管理
let products = [];

// 初始化页面
document.addEventListener('DOMContentLoaded', function() {
    // 加载产品列表
    loadProducts();
    
    // 设置搜索和筛选功能
    setupSearchAndFilter();
});

// 加载产品列表
async function loadProducts() {
    try {
        const response = await fetch('/api/products');
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || '获取产品列表失败');
        }
        
        if (!Array.isArray(data)) {
            throw new Error('返回数据格式错误');
        }
        
        displayProducts(data);
    } catch (error) {
        console.error('Error:', error);
        showNotification(error.message || '加载产品列表失败，请稍后重试', 'error');
        // 显示空数据提示
        const tbody = document.getElementById('productTableBody');
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center">
                    <div class="empty-state">
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <circle cx="12" cy="12" r="10"></circle>
                            <line x1="12" y1="8" x2="12" y2="12"></line>
                            <line x1="12" y1="16" x2="12.01" y2="16"></line>
                        </svg>
                        <p>暂无产品数据</p>
                    </div>
                </td>
            </tr>
        `;
    }
}

// 显示产品列表
function displayProducts(products) {
    const tbody = document.getElementById('productTableBody');
    tbody.innerHTML = ''; // 清空现有内容

    if (!Array.isArray(products) || products.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center">
                    <div class="empty-state">
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <circle cx="12" cy="12" r="10"></circle>
                            <line x1="12" y1="8" x2="12" y2="12"></line>
                            <line x1="12" y1="16" x2="12.01" y2="16"></line>
                        </svg>
                        <p>暂无产品数据</p>
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    // 按生产日期排序（从新到旧）
    products.sort((a, b) => {
        const dateA = new Date(a.productionDate || 0);
        const dateB = new Date(b.productionDate || 0);
        return dateB - dateA;
    });

    products.forEach(product => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${product.productId || '-'}</td>
            <td class="font-medium">${product.name || '-'}</td>
            <td>${product.manufacturer || '-'}</td>
            <td>${product.batchNumber || '-'}</td>
            <td>${formatDate(product.productionDate) || '-'}</td>
            <td>${product.origin || '-'}</td>
            <td>
                <div class="action-buttons">
                    <a href="product-detail.html?id=${product.productId}" class="btn-icon" title="查看详情">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                            <circle cx="12" cy="12" r="3"></circle>
                        </svg>
                    </a>
                    <a href="qrcode.html?id=${product.productId}" class="btn-icon" title="查看二维码">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                            <rect x="7" y="7" width="3" height="3"></rect>
                            <rect x="14" y="7" width="3" height="3"></rect>
                            <rect x="7" y="14" width="3" height="3"></rect>
                            <rect x="14" y="14" width="3" height="3"></rect>
                        </svg>
                    </a>
                    <button class="btn-icon" title="查看历史记录" onclick="viewHistory('${product.productId}')">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <circle cx="12" cy="12" r="10"></circle>
                            <polyline points="12 6 12 12 16 14"></polyline>
                        </svg>
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

// 设置搜索和筛选功能
function setupSearchAndFilter() {
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const statusFilter = document.getElementById('statusFilter');

    // 搜索功能
    searchInput.addEventListener('input', debounce(function() {
        filterProducts();
    }, 300));

    // 筛选功能
    categoryFilter.addEventListener('change', filterProducts);
    statusFilter.addEventListener('change', filterProducts);
}

// 防抖函数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 筛选产品
async function filterProducts() {
    try {
        const response = await fetch('/api/products');
        if (!response.ok) {
            throw new Error('获取产品列表失败');
        }
        const products = await response.json();
        
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const categoryValue = document.getElementById('categoryFilter').value;
        const statusValue = document.getElementById('statusFilter').value;

        const filteredProducts = products.filter(product => {
            const matchesSearch = 
                product.name.toLowerCase().includes(searchTerm) ||
                product.batchNumber.toLowerCase().includes(searchTerm) ||
                product.manufacturer.toLowerCase().includes(searchTerm);
            
            const matchesCategory = categoryValue === 'all' || product.category === categoryValue;
            const matchesStatus = statusValue === 'all' || product.status === statusValue;

            return matchesSearch && matchesCategory && matchesStatus;
        });

        displayProducts(filteredProducts);
    } catch (error) {
        console.error('Error:', error);
        alert('筛选产品失败，请稍后重试');
    }
}

// 查看历史记录
function viewHistory(productId) {
    // TODO: 实现查看历史记录的功能
    alert('查看历史记录功能开发中...');
}

// 设置事件监听器
function setupEventListeners() {
    // 搜索框输入事件
    const searchInput = document.getElementById('searchInput');
    searchInput.addEventListener('input', debounce(searchProducts, 300));

    // 表单提交事件
    const productForm = document.getElementById('productForm');
    productForm.addEventListener('submit', handleProductSubmit);
}

// 搜索产品
function searchProducts() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const filteredProducts = products.filter(product => 
        product.name.toLowerCase().includes(searchTerm) ||
        product.category.toLowerCase().includes(searchTerm)
    );
    renderProducts(filteredProducts);
}

// 渲染产品列表
function renderProducts(products) {
    const tbody = document.getElementById('productTableBody');
    tbody.innerHTML = products.map(product => `
        <tr>
            <td>${product.name}</td>
            <td>${product.category}</td>
            <td>￥${product.price.toFixed(2)}</td>
            <td>${product.stock}</td>
            <td>
                <span class="badge ${product.status === 'active' ? 'green' : 'gray'}">
                    ${product.status === 'active' ? '在售' : '下架'}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn-icon" onclick="editProduct(${product.id})" title="编辑">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                        </svg>
                    </button>
                    <button class="btn-icon" onclick="deleteProduct(${product.id})" title="删除">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <polyline points="3 6 5 6 21 6"></polyline>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        </svg>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// 显示添加产品模态框
function showAddProductModal() {
    document.getElementById('modalTitle').textContent = '添加产品';
    document.getElementById('productForm').reset();
    document.getElementById('productModal').style.display = 'flex';
}

// 显示编辑产品模态框
function editProduct(id) {
    const product = products.find(p => p.id === id);
    if (!product) return;

    document.getElementById('modalTitle').textContent = '编辑产品';
    document.getElementById('productName').value = product.name;
    document.getElementById('productCategory').value = product.category;
    document.getElementById('productPrice').value = product.price;
    document.getElementById('productStock').value = product.stock;
    document.getElementById('productStatus').value = product.status;
    
    document.getElementById('productForm').dataset.productId = id;
    document.getElementById('productModal').style.display = 'flex';
}

// 关闭模态框
function closeModal() {
    document.getElementById('productModal').style.display = 'none';
    document.getElementById('productForm').reset();
    delete document.getElementById('productForm').dataset.productId;
}

// 处理产品表单提交
async function handleProductSubmit(event) {
    event.preventDefault();
    
    const form = event.target;
    const productId = form.dataset.productId;
    const formData = new FormData(form);
    const productData = Object.fromEntries(formData.entries());
    
    try {
        const url = productId ? `/api/products/${productId}` : '/api/products';
        const method = productId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(productData),
        });
        
        if (!response.ok) throw new Error('Failed to save product');
        
        showNotification('产品保存成功', 'success');
        closeModal();
        loadProducts();
    } catch (error) {
        console.error('Error saving product:', error);
        showNotification('保存产品失败', 'error');
    }
}

// 删除产品
async function deleteProduct(id) {
    if (!confirm('确定要删除这个产品吗？')) return;
    
    try {
        const response = await fetch(`/api/products/${id}`, {
            method: 'DELETE',
        });
        
        if (!response.ok) throw new Error('Failed to delete product');
        
        showNotification('产品删除成功', 'success');
        loadProducts();
    } catch (error) {
        console.error('Error deleting product:', error);
        showNotification('删除产品失败', 'error');
    }
}

// 显示通知
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    // 添加样式
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.padding = '12px 24px';
    notification.style.borderRadius = '4px';
    notification.style.zIndex = '1000';
    
    // 根据类型设置不同的背景色
    switch(type) {
        case 'error':
            notification.style.backgroundColor = '#ff4d4f';
            notification.style.color = 'white';
            break;
        case 'success':
            notification.style.backgroundColor = '#52c41a';
            notification.style.color = 'white';
            break;
        default:
            notification.style.backgroundColor = '#1890ff';
            notification.style.color = 'white';
    }
    
    document.body.appendChild(notification);
    
    // 3秒后自动消失
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transition = 'opacity 0.5s ease';
        setTimeout(() => notification.remove(), 500);
    }, 3000);
}

// 获取产品列表
async function fetchProducts() {
    try {
        const response = await fetch('/api/products');
        if (!response.ok) {
            throw new Error('获取产品列表失败');
        }
        const products = await response.json();
        displayProducts(products);
    } catch (error) {
        console.error('Error:', error);
        showNotification('获取产品列表失败：' + error.message, 'error');
    }
}

// 搜索功能
function setupSearch() {
    const searchInput = document.getElementById('searchInput');
    if (!searchInput) {
        console.error('找不到搜索输入框');
        return;
    }

    searchInput.addEventListener('input', async (e) => {
        const searchTerm = e.target.value.toLowerCase();
        try {
            const response = await fetch('/api/products');
            if (!response.ok) {
                throw new Error('获取产品列表失败');
            }
            const products = await response.json();
            const filteredProducts = products.filter(product => 
                (product.name && product.name.toLowerCase().includes(searchTerm)) ||
                (product.batchNumber && product.batchNumber.toLowerCase().includes(searchTerm)) ||
                (product.manufacturer && product.manufacturer.toLowerCase().includes(searchTerm))
            );
            displayProducts(filteredProducts);
        } catch (error) {
            console.error('搜索错误:', error);
            showNotification('搜索失败：' + error.message, 'error');
        }
    });
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    fetchProducts();
    setupSearch();
}); 