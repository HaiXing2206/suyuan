// 获取URL参数中的产品ID
function getProductIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    if (!productId) {
        throw new Error('未找到产品ID参数');
    }
    return productId;
}

// 从API获取产品信息
async function fetchProductInfo(productId) {
    try {
        const response = await fetch(`/api/products/${productId}`);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || '获取产品信息失败');
        }
        const data = await response.json();
        console.log('获取到的产品数据:', data);
        return data;
    } catch (error) {
        console.error('获取产品信息失败:', error);
        throw error;
    }
}

// 更新页面上的产品信息
function updateProductInfo(productData) {
    if (!productData) {
        throw new Error('产品数据为空');
    }
    
    console.log('更新产品信息:', productData);
    
    document.getElementById('product-name').textContent = productData.name || '未知产品';
    document.getElementById('product-batch').textContent = `批次号: ${productData.batchNumber || '未知'}`;
    document.getElementById('info-name').textContent = productData.name || '未知产品';
    document.getElementById('info-batch').textContent = productData.batchNumber || '未知';
    document.getElementById('info-manufacturer').textContent = productData.manufacturer || '未知';
    document.getElementById('info-date').textContent = productData.productionDate ? 
        new Date(productData.productionDate).toLocaleDateString() : '未知';
    document.getElementById('info-origin').textContent = productData.origin || '未知';
}

// 生成二维码
function generateQRCode(productId) {
    if (!productId) {
        throw new Error('产品ID不能为空');
    }
    
    const qr = qrcode(0, 'M');
    qr.addData(`${window.location.origin}/trace.html?id=${productId}`);
    qr.make();
    
    const qrContainer = document.getElementById('qrcode');
    qrContainer.innerHTML = qr.createImgTag(8);
}

// 下载二维码
function downloadQRCode() {
    const qrImage = document.querySelector('#qrcode img');
    if (qrImage) {
        const link = document.createElement('a');
        link.download = 'product-qrcode.png';
        link.href = qrImage.src;
        link.click();
    }
}

// 打印标签
function printLabel() {
    window.print();
}

// 显示错误信息
function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    document.querySelector('.qrcode-container').prepend(errorDiv);
}

// 初始化页面
async function initializePage() {
    try {
        const productId = getProductIdFromUrl();
        console.log('获取到的产品ID:', productId);
        
        const productData = await fetchProductInfo(productId);
        updateProductInfo(productData);
        generateQRCode(productId);
    } catch (error) {
        console.error('页面初始化失败:', error);
        showError(error.message);
    }
}

// 添加事件监听器
document.addEventListener('DOMContentLoaded', () => {
    initializePage();
    
    // 返回按钮
    document.getElementById('back-link').addEventListener('click', (e) => {
        e.preventDefault();
        window.history.back();
    });

    // 下载二维码按钮
    document.getElementById('download-qr').addEventListener('click', downloadQRCode);

    // 打印标签按钮
    document.getElementById('print-qr').addEventListener('click', printLabel);
});