// API基础配置
const API_BASE_URL = 'http://localhost:8080/api'; // 根据实际情况修改

// API接口地址
const API_ENDPOINTS = {
    GET_ALL: '/behaviors',
    GET_BY_ID: '/behaviors',
    CREATE: '/behaviors',
    UPDATE: '/behaviors',
    DELETE: '/behaviors',
    EXPORT: '/behaviors/export',
    EXPORT_TYPE: '/behaviors/export/type',
    SEARCH: '/behaviors/search', // 条件搜索接口
    SEARCH_STATS: '/behaviors/search/stats' // 搜索统计接口
};

// 类型映射
const TYPE_MAP = {
    1: { name: '奖励', className: 'badge-reward' },
    2: { name: '扣除', className: 'badge-deduction' },
    3: { name: '重大奖励', className: 'badge-major-reward' },
    4: { name: '重大扣除', className: 'badge-major-deduction' }
};

// 执行人映射
const OWNER_MAP = {
    1: '宸宸',
    2: '爸爸妈妈'
};

// 获取所有行为规则
async function fetchAllBehaviors() {
    try {
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.GET_ALL}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        return Array.isArray(data) ? data : [];
    } catch (error) {
        console.error('获取数据失败:', error);
        showError('获取数据失败，请检查网络连接或API地址');
        return [];
    }
}

// 条件搜索行为规则
async function searchBehaviors(params) {
    try {
        const queryParams = new URLSearchParams();

        // 添加查询参数
        if (params.keyword) queryParams.append('keyword', params.keyword);
        if (params.type !== null && params.type !== undefined) queryParams.append('type', params.type);
        if (params.owner !== null && params.owner !== undefined) queryParams.append('owner', params.owner);

        queryParams.append('page', params.page || 0);
        queryParams.append('size', params.size || 10);
        queryParams.append('sortBy', params.sortBy || 'id');
        queryParams.append('direction', params.direction || 'desc');

        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.SEARCH}?${queryParams.toString()}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('条件搜索失败:', error);
        showError('搜索失败');
        throw error;
    }
}

// 获取搜索统计信息
async function getSearchStats(params) {
    try {
        const queryParams = new URLSearchParams();

        if (params.keyword) queryParams.append('keyword', params.keyword);
        if (params.type !== null && params.type !== undefined) queryParams.append('type', params.type);
        if (params.owner !== null && params.owner !== undefined) queryParams.append('owner', params.owner);

        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.SEARCH_STATS}?${queryParams.toString()}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('获取搜索统计失败:', error);
        return null;
    }
}

// 根据ID获取单条记录
async function fetchBehaviorById(id) {
    try {
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.GET_BY_ID}/${id}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('获取记录失败:', error);
        showError('获取记录失败');
        return null;
    }
}

// 创建新记录
async function createBehavior(behaviorData) {
    try {
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.CREATE}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(behaviorData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('创建记录失败:', error);
        showError('创建记录失败');
        throw error;
    }
}

// 更新记录
async function updateBehavior(id, behaviorData) {
    try {
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.UPDATE}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(behaviorData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('更新记录失败:', error);
        showError('更新记录失败');
        throw error;
    }
}

// 删除记录
async function deleteBehavior(id) {
    try {
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.DELETE}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('删除记录失败:', error);
        showError('删除记录失败');
        throw error;
    }
}

// 导出Word文档
async function exportBehaviorsToWord() {
    try {
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.EXPORT_WORD}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // 获取Blob数据并下载
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = '行为规范表.docx';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        return true;
    } catch (error) {
        console.error('导出失败:', error);
        showError('导出Word文档失败');
        return false;
    }
}

// 工具函数：显示成功消息
function showSuccess(message) {
    Swal.fire({
        icon: 'success',
        title: '成功',
        text: message,
        timer: 2000,
        showConfirmButton: false
    });
}

// 工具函数：显示错误消息
function showError(message) {
    Swal.fire({
        icon: 'error',
        title: '错误',
        text: message,
        timer: 3000
    });
}

// 工具函数：显示确认对话框
async function showConfirm(title, text) {
    const result = await Swal.fire({
        title: title,
        text: text,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        reverseButtons: true
    });

    return result.isConfirmed;
}