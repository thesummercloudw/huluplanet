/**
 * 封装网络请求
 */
const app = getApp();

/**
 * 解析图片URL：提取文件名，通过 API 端点加载图片
 * 走 /api/public/image/ 端点，确保真机调试下也能正常加载
 * @param {string} url - 图片路径（可能是相对路径或完整URL）
 * @returns {string} 完整的图片访问地址
 */
const resolveImage = (url) => {
  if (!url) return '';
  // 已经是完整 URL（后端已通过 Jackson 序列化返回完整地址），直接使用
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  // 兜底：相对路径 /uploads/xxx 转为 API 端点
  const match = url.match(/\/uploads\/(.+)/);
  if (match) {
    return app.globalData.baseUrl + '/api/public/image/' + match[1];
  }
  // 其他相对路径
  if (url.startsWith('/')) {
    return app.globalData.baseUrl + url;
  }
  return url;
};

const request = (options) => {
  return new Promise((resolve, reject) => {
    const { url, method = 'GET', data, header = {} } = options;

    // 自动加 token
    if (app.globalData.token) {
      header['Authorization'] = `Bearer ${app.globalData.token}`;
    }
    // 自动加家庭ID
    if (app.globalData.currentFamilyId) {
      header['X-Family-Id'] = String(app.globalData.currentFamilyId);
    }

    wx.request({
      url: `${app.globalData.baseUrl}${url}`,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...header
      },
      success(res) {
        const result = res.data;
        if (result.code === 0) {
          resolve(result.data);
        } else if (result.code === 401) {
          // token 过期，跳转登录
          wx.removeStorageSync('token');
          app.globalData.token = '';
          wx.redirectTo({ url: '/pages/login/login' });
          reject(result);
        } else {
          wx.showToast({ title: result.message || '请求失败', icon: 'none' });
          reject(result);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常', icon: 'none' });
        reject(err);
      }
    });
  });
};

module.exports = {
  get: (url, data) => request({ url, method: 'GET', data }),
  post: (url, data) => request({ url, method: 'POST', data }),
  put: (url, data) => request({ url, method: 'PUT', data }),
  del: (url, data) => request({ url, method: 'DELETE', data }),
  resolveImage,
  upload: (filePath) => {
    return new Promise((resolve, reject) => {
      const header = {};
      if (app.globalData.token) {
        header['Authorization'] = `Bearer ${app.globalData.token}`;
      }
      wx.uploadFile({
        url: `${app.globalData.baseUrl}/api/upload`,
        filePath: filePath,
        name: 'file',
        header,
        success(res) {
          const result = JSON.parse(res.data);
          if (result.code === 0) {
            resolve(result.data);
          } else {
            wx.showToast({ title: result.message || '上传失败', icon: 'none' });
            reject(result);
          }
        },
        fail(err) {
          wx.showToast({ title: '上传失败', icon: 'none' });
          reject(err);
        }
      });
    });
  }
};
