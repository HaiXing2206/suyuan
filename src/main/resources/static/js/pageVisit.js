// 记录页面访问
function recordPageVisit(pageName) {
    const url = window.location.href;
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    
    fetch('/api/page-visits/record', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            pageName: pageName,
            url: url,
            productId: productId
        })
    })
    .then(response => response.json())
    .then(data => {
        if (!data.success) {
            console.error('记录页面访问失败:', data.error);
        }
    })
    .catch(error => {
        console.error('记录页面访问失败:', error);
    });
} 