/**
 * 封装网络请求
 */
const app = getApp();

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
