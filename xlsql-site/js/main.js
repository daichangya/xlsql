// 主JavaScript文件
document.addEventListener('DOMContentLoaded', function() {
    // 可以在这里添加交互功能
    console.log('xlSQL网站已加载');
    
    // 示例：为所有代码块添加复制按钮
    document.querySelectorAll('pre code').forEach(function(codeBlock) {
        var button = document.createElement('button');
        button.className = 'copy-button';
        button.textContent = '复制';
        
        button.addEventListener('click', function() {
            navigator.clipboard.writeText(codeBlock.textContent)
                .then(function() {
                    button.textContent = '已复制!';
                    setTimeout(function() {
                        button.textContent = '复制';
                    }, 2000);
                })
                .catch(function(err) {
                    console.error('复制失败: ', err);
                });
        });
        
        var div = document.createElement('div');
        div.className = 'code-header';
        div.appendChild(button);
        
        codeBlock.parentNode.insertBefore(div, codeBlock);
    });
});