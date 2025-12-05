// Main JavaScript for XLSQL Site

document.addEventListener('DOMContentLoaded', function() {
    // Update current year in footer
    const yearSpan = document.getElementById('current-year');
    if (yearSpan) {
        yearSpan.textContent = new Date().getFullYear();
    }

    // Syntax highlighting (optional, if we decide to add a library later)
    // For now, we just ensure code blocks look good
    
    // Add copy button to code blocks
    document.querySelectorAll('pre').forEach(block => {
        // Create container for relative positioning
        const container = document.createElement('div');
        container.style.position = 'relative';
        
        // Insert container before block and move block into it
        block.parentNode.insertBefore(container, block);
        container.appendChild(block);
        
        // Create button
        const button = document.createElement('button');
        button.className = 'btn btn-sm btn-outline-secondary position-absolute top-0 end-0 m-2';
        button.style.zIndex = '10';
        button.textContent = 'Copy';
        
        // Add click handler
        button.addEventListener('click', () => {
            const code = block.innerText;
            navigator.clipboard.writeText(code).then(() => {
                button.textContent = 'Copied!';
                button.classList.remove('btn-outline-secondary');
                button.classList.add('btn-success');
                setTimeout(() => {
                    button.textContent = 'Copy';
                    button.classList.remove('btn-success');
                    button.classList.add('btn-outline-secondary');
                }, 2000);
            });
        });
        
        container.appendChild(button);
    });
});

