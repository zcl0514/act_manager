// API基础配置
const API_BASE_URL = 'http://localhost:8080/api'; // 根据实际情况修改

// API接口地址
const API_ENDPOINTS = {
    BEHAVIORS: '/behaviors', // 统一使用这个接口
    EXPORT_WORD: '/behaviors/export'
};

// 类型映射
let TYPE_MAP;
TYPE_MAP = {
    1: {name: '奖励', className: 'badge-reward'},
    2: {name: '扣除', className: 'badge-deduction'},
    3: {name: '重大奖励', className: 'badge-major-reward'},
    4: {name: '重大扣除', className: 'badge-major-deduction'}
};

// 执行人映射
const OWNER_MAP = {
    1: '宸宸',
    2: '爸爸妈妈'
};

/**
 * 获取行为规则
 * @param {Object} params - 查询参数 {keyword, type, owner}
 */
async function fetchBehaviors(params = {}) {
    try {
        // 构建查询参数
        const queryParams = new URLSearchParams();

        // 添加查询条件
        if (params.keyword) queryParams.append('keyword', params.keyword);
        if (params.type !== '' && params.type !== null && params.type !== undefined)
            queryParams.append('type', params.type);
        if (params.owner !== '' && params.owner !== null && params.owner !== undefined)
            queryParams.append('owner', params.owner);

        // 设置大size让后端返回所有数据，前端进行分页
        queryParams.append('size', '10000');

        const url = `${API_BASE_URL}${API_ENDPOINTS.BEHAVIORS}?${queryParams.toString()}`;

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        // 处理返回数据格式
        // 如果后端返回的是分页格式，提取content
        if (data && data.content) {
            return data.content || [];
        }
        // 如果后端返回的是数组格式，直接返回
        else if (Array.isArray(data)) {
            return data;
        }
        // 其他格式，返回空数组
        else {
            return [];
        }
    } catch (error) {
        console.error('获取数据失败:', error);
        showError('获取数据失败，请检查网络连接');
        return [];
    }
}

// 根据ID获取单条记录
async function fetchBehaviorById(id) {
    try {
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.BEHAVIORS}/${id}`);
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
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.BEHAVIORS}`, {
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
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.BEHAVIORS}/${id}`, {
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
        const response = await fetch(`${API_BASE_URL}${API_ENDPOINTS.BEHAVIORS}/${id}`, {
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
        const now = new Date();
        const date = now.toLocaleString().split(",")[0];
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = '行为规范表' + date + '.docx';
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