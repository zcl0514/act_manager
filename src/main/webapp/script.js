// 全局变量
let allBehaviors = [];
let filteredBehaviors = [];
let currentPage = 1;
const itemsPerPage = 10;
let itemToDelete = null;
let currentSearchParams = {};

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', async function() {
    await loadBehaviors();
    setupEventListeners();
});

// 加载所有行为规则
async function loadBehaviors() {
    try {
        // 显示加载状态
        showLoading();

        allBehaviors = await fetchAllBehaviors();
        filteredBehaviors = [...allBehaviors];

        // 更新统计信息
        updateStatistics();

        // 渲染表格
        renderTable();

        // 更新分页
        updatePagination();

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

// 更新统计信息
function updateStatistics() {
    // 统计奖励项目
    const rewardCount = allBehaviors.filter(item =>
        item.type === 0 || item.type === 2
    ).length;
    document.getElementById('totalRewards').textContent = rewardCount;

    // 统计扣除项目
    const deductionCount = allBehaviors.filter(item =>
        item.type === 1 || item.type === 3
    ).length;
    document.getElementById('totalDeductions').textContent = deductionCount;

    // 统计执行人（去重）
    const owners = [...new Set(allBehaviors.map(item => item.owner))];
    document.getElementById('totalOwners').textContent = owners.length;

    // 统计总红花数（奖励为正，扣除为负）
    const totalFlowers = allBehaviors.reduce((sum, item) => {
        const count = parseInt(item.redFlowerCount) || 0;
        return item.type === 1 || item.type === 3 ? sum - count : sum + count;
    }, 0);
    document.getElementById('totalFlowers').textContent = totalFlowers;
}

// 渲染表格（支持高亮）
function renderTable() {
    const tableBody = document.getElementById('tableBody');

    if (filteredBehaviors.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 40px; color: #999;">
                    <i class="fas fa-search fa-3x"></i>
                    <p style="margin-top: 10px;">没有找到符合条件的记录</p>
                    <button class="btn btn-sm btn-primary" onclick="resetSearch()" style="margin-top: 10px;">
                        重置查询条件
                    </button>
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
                        ${item.type === 1 || item.type === 3 ? '-' : '+'}${item.redFlowerCount}
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

// 重置查询
function resetSearch() {
    // 重置表单
    document.getElementById('searchInput').value = '';
    document.getElementById('typeFilter').value = '';
    document.getElementById('ownerFilter').value = '';

    // 重置查询参数
    currentSearchParams = {};

    // 重新加载所有数据
    loadBehaviors();
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

// 更新实时查询的分页
function updateRealTimePagination(data) {
    const totalPages = data.totalPages || 1;
    const totalItems = data.totalItems || 0;

    const pageInfo = document.getElementById('pageInfo');
    const prevButton = document.getElementById('prevPage');
    const nextButton = document.getElementById('nextPage');

    pageInfo.textContent = `第 ${currentPage} 页 / 共 ${totalPages} 页`;
    prevButton.disabled = currentPage <= 1;
    nextButton.disabled = currentPage >= totalPages;
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

// 导出当前筛选结果
async function exportFilteredData() {
    try {
        // 获取当前筛选条件
        const searchParams = {
            keyword: document.getElementById('searchInput').value.trim() || null,
            type: document.getElementById('typeFilter').value || null,
            owner: document.getElementById('ownerFilter').value || null
        };

        showSuccess('正在生成Word文档，请稍候...');

        // 如果有明确的类型筛选，使用类型导出
        if (searchParams.type && !searchParams.keyword && !searchParams.owner) {
            await exportByType(parseInt(searchParams.type));
        } else {
            // 否则导出全部数据（或者可以调用一个支持复杂条件的导出接口）
            await exportAllToWord();
        }

    } catch (error) {
        console.error('导出失败:', error);
        showError('导出失败，请检查网络连接');
    }
}

// 过滤表格数据
function filterTable() {
    const searchText = document.getElementById('searchInput').value.toLowerCase();
    const typeFilter = document.getElementById('typeFilter').value;
    const ownerFilter = document.getElementById('ownerFilter').value;

    filteredBehaviors = allBehaviors.filter(item => {
        // 搜索文本过滤
        const matchesSearch = !searchText ||
            item.role.toLowerCase().includes(searchText) ||
            (item.extra && item.extra.toLowerCase().includes(searchText));

        // 类型过滤
        const matchesType = !typeFilter || item.type.toString() === typeFilter;

        // 执行人过滤
        const matchesOwner = !ownerFilter || item.owner.toString() === ownerFilter;

        return matchesSearch && matchesType && matchesOwner;
    });

    currentPage = 1;
    renderTable();
    updatePagination();
}

// 设置事件监听器
function setupEventListeners() {
    // 表单提交事件
    const form = document.getElementById('behaviorForm');
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        await saveBehavior();
    });

    // 下拉菜单变化时自动查询
    document.getElementById('typeFilter').addEventListener('change', function() {
        performRealTimeSearch();
    });

    document.getElementById('ownerFilter').addEventListener('change', function() {
        performRealTimeSearch();
    });

    // 搜索框输入时自动查询（添加防抖）
    let searchTimeout;
    document.getElementById('searchInput').addEventListener('input', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            performRealTimeSearch();
        }, 300); // 300ms防抖
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

// 实时查询（下拉菜单变化时自动调用）
async function performRealTimeSearch() {
    try {
        // 收集查询参数
        const searchParams = {
            keyword: document.getElementById('searchInput').value.trim() || null,
            type: document.getElementById('typeFilter').value || null,
            owner: document.getElementById('ownerFilter').value || null,
            page: 0,
            size: itemsPerPage,
            sortBy: 'id',
            direction: 'desc'
        };

        // 保存当前查询参数
        currentSearchParams = searchParams;

        // 执行查询
        await executeRealTimeSearch(searchParams);

        // 更新统计信息
        updateFilteredStatistics();

    } catch (error) {
        console.error('实时查询失败:', error);
    }
}

// 更新筛选后的统计信息
function updateFilteredStatistics() {
    // 如果过滤后的数据为空，显示0
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

// 执行实时查询
async function executeRealTimeSearch(params) {
    try {
        // 构建查询参数
        const queryParams = new URLSearchParams();

        if (params.keyword) queryParams.append('keyword', params.keyword);
        if (params.type) queryParams.append('type', params.type);
        if (params.owner) queryParams.append('owner', params.owner);
        queryParams.append('page', params.page);
        queryParams.append('size', params.size);
        queryParams.append('sortBy', params.sortBy);
        queryParams.append('direction', params.direction);

        // 显示加载状态
        showLoading();

        // 调用查询接口
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.SEARCH}?${queryParams.toString()}`);

        if (!response.ok) {
            throw new Error(`查询失败: ${response.status}`);
        }

        const data = await response.json();

        // 更新数据
        filteredBehaviors = data.behaviors || [];

        // 获取查询统计信息
        await loadSearchStats(params);

        // 渲染表格
        renderTable();

        // 更新分页信息
        updateRealTimePagination(data);

    } catch (error) {
        console.error('执行查询失败:', error);
        throw error;
    } finally {
        hideLoading();
    }
}

// 加载查询统计信息
async function loadSearchStats(params) {
    try {
        const queryParams = new URLSearchParams();

        if (params.keyword) queryParams.append('keyword', params.keyword);
        if (params.type) queryParams.append('type', params.type);
        if (params.owner) queryParams.append('owner', params.owner);

        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.SEARCH_STATS}?${queryParams.toString()}`);

        if (response.ok) {
            const stats = await response.json();
            displaySearchStats(stats);
        }
    } catch (error) {
        console.error('加载统计信息失败:', error);
    }
}

// 显示查询统计信息
function displaySearchStats(stats) {
    // 这里可以添加更详细的统计信息显示
    // 例如：在搜索框下方显示"找到X条记录，总红花数：Y"
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
        await loadBehaviors(); // 重新加载数据

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
        await loadBehaviors(); // 重新加载数据

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

// 模拟数据（当后端API不可用时使用）
function getMockData() {
    return [
        {
            id: 1,
            role: "独立穿衣服、鞋子、袜子等，独立脱衣服、鞋子、袜子并将其整理整洁",
            redFlowerCount: 1,
            type: 0,
            owner: 0,
            extra: null
        },
        {
            id: 2,
            role: "独立吃饭，营养均衡并且不挑食，吃完饭后保证桌面及地面的干净",
            redFlowerCount: 1,
            type: 0,
            owner: 0,
            extra: null
        },
        {
            id: 3,
            role: "辅导宸宸学作业",
            redFlowerCount: 1,
            type: 0,
            owner: 1,
            extra: null
        },
        {
            id: 4,
            role: "遇到好朋友或者长辈，对方打招呼，不予回应",
            redFlowerCount: 3,
            type: 3,
            owner: 0,
            extra: "情节严重扣除特殊奖励"
        },
        {
            id: 5,
            role: "自己学习并独立完成洗澡",
            redFlowerCount: 3,
            type: 2,
            owner: 0,
            extra: "特殊奖励"
        }
    ];
}