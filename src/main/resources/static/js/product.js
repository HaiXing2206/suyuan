document.addEventListener('DOMContentLoaded', function() {
    // 搜索功能
    const searchInput = document.querySelector('.search-input input');
    const tableRows = document.querySelectorAll('.table tbody tr');

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();

        tableRows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(searchTerm)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });

    // 筛选功能
    const categorySelect = document.querySelector('.filter-selects select:first-child');
    const dateSelect = document.querySelector('.filter-selects select:last-child');

    const applyFilters = () => {
        const categoryValue = categorySelect.value;
        const dateValue = dateSelect.value;

        tableRows.forEach(row => {
            let showRow = true;

            // 这里只是示例，实际应用中需要根据数据结构进行筛选
            // 在真实应用中，可能需要从行中提取类别和日期信息

            if (categoryValue !== 'all') {
                // 假设第一列包含类别信息
                const category = row.cells[0].textContent.toLowerCase();
                if (!category.includes(categoryValue)) {
                    showRow = false;
                }
            }

            if (dateValue !== 'all') {
                // 假设第三列包含日期信息
                const date = new Date(row.cells[2].textContent);
                const today = new Date();

                if (dateValue === 'last-week') {
                    const lastWeek = new Date(today);
                    lastWeek.setDate(today.getDate() - 7);
                    if (date < lastWeek) {
                        showRow = false;
                    }
                } else if (dateValue === 'last-month') {
                    const lastMonth = new Date(today);
                    lastMonth.setMonth(today.getMonth() - 1);
                    if (date < lastMonth) {
                        showRow = false;
                    }
                } else if (dateValue === 'last-year') {
                    const lastYear = new Date(today);
                    lastYear.setFullYear(today.getFullYear() - 1);
                    if (date < lastYear) {
                        showRow = false;
                    }
                }
            }

            row.style.display = showRow ? '' : 'none';
        });
    };

    categorySelect.addEventListener('change', applyFilters);
    dateSelect.addEventListener('change', applyFilters);
});