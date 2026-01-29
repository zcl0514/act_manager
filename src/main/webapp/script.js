// 全局变量
let behaviors = []; // 从后端获取的所有数据
let filteredBehaviors = []; // 当前显示的数据
let currentPage = 1;
const itemsPerPage = 10;
let itemToDelete = null;
let currentSearchParams = {};
let searchTimeout = null;

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', async function() {
    await loadBehaviors();
    setupEventListeners();
});

// 加载所有行为规则
async function loadBehaviors() {
    try {
        showLoading();

        // 调用接口获取所有数据（不传递查询条件）
        behaviors = await fetchBehaviors({});
        filteredBehaviors = [...behaviors];

        updateStatistics();
        renderTable();
        updatePagination();
        hideSearchResultsInfo();

    } catch (error) {
        console.error('加载数据失败:', error);
    } finally {
        hideLoading();
    }
}

// 显示加载状态
function showLoading() {
    const tableBody = document.getElementById('tableBody');
    tableBody.innerHTML = `
        <tr>
            <td colspan="7" style="text-align: center; padding: 40px;">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">加载中...</span>
                </div>
                <p style="margin-top: 10px;">加载中...</p>
            </td>
        </tr>
    `;
}

// 隐藏加载状态
function hideLoading() {
    // 加载状态会在renderTable中被替换
}

// 设置事件监听器
function setupEventListeners() {
    // 表单提交事件
    const form = document.getElementById('behaviorForm');
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        await saveBehavior();
    });

    // 搜索框输入时自动查询（防抖）
    document.getElementById('searchInput').addEventListener('input', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            performSearch();
        }, 300);
    });

    // 下拉菜单变化时自动查询
    document.getElementById('typeFilter').addEventListener('change', function() {
        performSearch();
    });

    document.getElementById('ownerFilter').addEventListener('change', function() {
        performSearch();
    });

    // 模态框关闭事件
    const modal = document.getElementById('behaviorModal');
    window.addEventListener('click', function(e) {
        if (e.target === modal) {
            closeModal();
        }
    });

    const confirmModal = document.getElementById('confirmModal');
    window.addEventListener('click', function(e) {
        if (e.target === confirmModal) {
            closeConfirmModal();
        }
    });
}

// 执行搜索
async function performSearch() {
    try {
        const keyword = document.getElementById('searchInput').value.trim();
        const type = document.getElementById('typeFilter').value;
        const owner = document.getElementById('ownerFilter').value;

        // 保存当前查询参数
        currentSearchParams = { keyword, type, owner };

        // 调用接口进行查询
        await executeSearch(keyword, type, owner);
    } catch (error) {
        console.error('搜索失败:', error);
    }
}

// 执行搜索查询
async function executeSearch(keyword, type, owner) {
    try {
        showLoading();

        // 调用接口获取数据
        behaviors = await fetchBehaviors({ keyword, type, owner });
        filteredBehaviors = [...behaviors];

        // 显示搜索结果信息
        showSearchResultsInfo(filteredBehaviors.length);

        // 更新统计信息
        updateStatistics();

        // 渲染表格
        renderTable();

        // 重置分页
        currentPage = 1;
        updatePagination();

    } catch (error) {
        console.error('执行搜索失败:', error);
    } finally {
        hideLoading();
    }
}

// 显示搜索结果信息
function showSearchResultsInfo(count) {
    const resultsInfo = document.getElementById('searchResultsInfo');
    const resultsCount = document.getElementById('resultsCount');

    if (count > 0) {
        resultsCount.textContent = count;
        resultsInfo.style.display = 'block';
    } else {
        resultsInfo.style.display = 'none';
    }
}

// 隐藏搜索结果信息
function hideSearchResultsInfo() {
    document.getElementById('searchResultsInfo').style.display = 'none';
}

// 清除搜索框
function clearSearch() {
    document.getElementById('searchInput').value = '';
    performSearch();
}

// 清除所有筛选条件
function clearAllFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('typeFilter').value = '';
    document.getElementById('ownerFilter').value = '';

    currentSearchParams = {};
    hideSearchResultsInfo();
    loadBehaviors();
}

// 更新统计信息
function updateStatistics() {
    if (filteredBehaviors.length === 0) {
        document.getElementById('totalRewards').textContent = '0';
        document.getElementById('totalDeductions').textContent = '0';
        document.getElementById('totalOwners').textContent = '0';
        document.getElementById('totalFlowers').textContent = '0';
        return;
    }

    // 统计奖励项目
    const rewardCount = filteredBehaviors.filter(item =>
        item.type === 0 || item.type === 2
    ).length;
    document.getElementById('totalRewards').textContent = rewardCount;

    // 统计扣除项目
    const deductionCount = filteredBehaviors.filter(item =>
        item.type === 1 || item.type === 3
    ).length;
    document.getElementById('totalDeductions').textContent = deductionCount;

    // 统计执行人（去重）
    const owners = [...new Set(filteredBehaviors.map(item => item.owner))];
    document.getElementById('totalOwners').textContent = owners.length;

    // 统计总红花数（奖励为正，扣除为负）
    const totalFlowers = filteredBehaviors.reduce((sum, item) => {
        const count = parseInt(item.redFlowerCount) || 0;
        return item.type === 1 || item.type === 3 ? sum - count : sum + count;
    }, 0);
    document.getElementById('totalFlowers').textContent = totalFlowers;
}

// 渲染表格
function renderTable() {
    const tableBody = document.getElementById('tableBody');

    if (filteredBehaviors.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 40px; color: #999;">
                    <i class="fas fa-inbox fa-3x"></i>
                    <p style="margin-top: 10px;">暂无数据</p>
                </td>
            </tr>
        `;
        return;
    }

    // 计算分页数据
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageItems = filteredBehaviors.slice(startIndex, endIndex);

    let html = '';
    const keyword = currentSearchParams.keyword ? currentSearchParams.keyword.toLowerCase() : '';

    pageItems.forEach((item, index) => {
        const typeInfo = TYPE_MAP[item.type] || TYPE_MAP[0];
        const ownerName = OWNER_MAP[item.owner] || '未知';

        // 高亮显示关键词
        let roleText = item.role;
        let extraText = item.extra || '';

        if (keyword) {
            roleText = highlightKeyword(roleText, keyword);
            if (extraText) {
                extraText = highlightKeyword(extraText, keyword);
            }
        }

        html += `
            <tr>
                <td>${item.id || startIndex + index + 1}</td>
                <td style="max-width: 300px; word-wrap: break-word;">${roleText}</td>
                <td>
                    <span style="color: ${item.type === 1 || item.type === 3 ? '#F44336' : '#4CAF50'};">
                        ${item.type === 1 || item.type === 3 ? '+' : '-'}${item.redFlowerCount}
                    </span>
                </td>
                <td><span class="status-badge ${typeInfo.className}">${typeInfo.name}</span></td>
                <td>${ownerName}</td>
                <td>${extraText || '-'}</td>
                <td>
                    <div class="action-buttons">
                        <button class="action-btn edit" onclick="openEditModal(${item.id})">
                            <i class="fas fa-edit"></i> 编辑
                        </button>
                        <button class="action-btn delete" onclick="openDeleteConfirm(${item.id}, '${item.role.substring(0, 30)}...')">
                            <i class="fas fa-trash"></i> 删除
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });

    tableBody.innerHTML = html;
}

// 高亮关键词
function highlightKeyword(text, keyword) {
    if (!keyword || !text) return text;

    try {
        const regex = new RegExp(`(${keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
        return text.replace(regex, '<span class="highlight">$1</span>');
    } catch (e) {
        return text;
    }
}

// 更新分页
function updatePagination() {
    const totalPages = Math.ceil(filteredBehaviors.length / itemsPerPage);
    const pageInfo = document.getElementById('pageInfo');
    const prevButton = document.getElementById('prevPage');
    const nextButton = document.getElementById('nextPage');

    pageInfo.textContent = `第 ${currentPage} 页 / 共 ${totalPages} 页`;
    prevButton.disabled = currentPage <= 1;
    nextButton.disabled = currentPage >= totalPages;
}

// 切换页面
function changePage(direction) {
    const totalPages = Math.ceil(filteredBehaviors.length / itemsPerPage);
    const newPage = currentPage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        renderTable();
        updatePagination();
    }
}

// 打开添加模态框
function openAddModal() {
    document.getElementById('modalTitle').textContent = '添加新规则';
    document.getElementById('behaviorForm').reset();
    document.getElementById('itemId').value = '';
    document.getElementById('behaviorModal').style.display = 'flex';
}

// 打开编辑模态框
async function openEditModal(id) {
    try {
        const item = await fetchBehaviorById(id);

        if (item) {
            document.getElementById('modalTitle').textContent = '编辑规则';
            document.getElementById('itemId').value = item.id;
            document.getElementById('role').value = item.role;
            document.getElementById('redFlowerCount').value = item.redFlowerCount;
            document.getElementById('type').value = item.type;
            document.getElementById('owner').value = item.owner;
            document.getElementById('extra').value = item.extra || '';

            document.getElementById('behaviorModal').style.display = 'flex';
        }
    } catch (error) {
        console.error('打开编辑模态框失败:', error);
    }
}

// 关闭模态框
function closeModal() {
    document.getElementById('behaviorModal').style.display = 'none';
}

// 保存行为规则（添加或更新）
async function saveBehavior() {
    const id = document.getElementById('itemId').value;
    const behaviorData = {
        role: document.getElementById('role').value.trim(),
        redFlowerCount: parseInt(document.getElementById('redFlowerCount').value),
        type: parseInt(document.getElementById('type').value),
        owner: parseInt(document.getElementById('owner').value),
        extra: document.getElementById('extra').value.trim() || null
    };

    // 验证数据
    if (!behaviorData.role) {
        showError('行为内容不能为空');
        return;
    }

    try {
        if (id) {
            // 更新现有记录
            await updateBehavior(id, behaviorData);
            showSuccess('规则更新成功');
        } else {
            // 创建新记录
            await createBehavior(behaviorData);
            showSuccess('规则添加成功');
        }

        closeModal();

        // 重新加载数据（保持当前筛选条件）
        await executeSearch(
            currentSearchParams.keyword,
            currentSearchParams.type,
            currentSearchParams.owner
        );

    } catch (error) {
        console.error('保存失败:', error);
    }
}

// 打开删除确认框
function openDeleteConfirm(id, role) {
    itemToDelete = id;
    document.getElementById('confirmMessage').textContent =
        `您确定要删除规则 "${role}" 吗？此操作不可撤销。`;
    document.getElementById('confirmModal').style.display = 'flex';
}

// 关闭确认模态框
function closeConfirmModal() {
    document.getElementById('confirmModal').style.display = 'none';
    itemToDelete = null;
}

// 确认删除
async function confirmDelete() {
    if (!itemToDelete) return;

    try {
        await deleteBehavior(itemToDelete);
        showSuccess('规则删除成功');
        closeConfirmModal();

        // 重新加载数据（保持当前筛选条件）
        await executeSearch(
            currentSearchParams.keyword,
            currentSearchParams.type,
            currentSearchParams.owner
        );

    } catch (error) {
        console.error('删除失败:', error);
    }
}

// 导出Word文档
async function exportToWord() {
    try {
        showSuccess('正在生成Word文档，请稍候...');

        const success = await exportBehaviorsToWord();

        if (success) {
            showSuccess('Word文档导出成功！');
        }

    } catch (error) {
        console.error('导出失败:', error);
    }
}