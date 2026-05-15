const http = require('../../utils/request');
const app = getApp();

Page({
  data: {
    loading: false
  },

  onLoad() {
    // 已登录则直接跳首页
    if (app.globalData.token) {
      wx.switchTab({ url: '/pages/index/index' });
    }
  },

  handleLogin() {
    if (this.data.loading) return;
    this.setData({ loading: true });

    wx.login({
      success: (res) => {
        if (res.code) {
          http.post('/api/auth/wx-login', { code: res.code })
            .then((data) => {
              app.globalData.token = data.token;
              app.globalData.userId = data.userId;
              wx.setStorageSync('token', data.token);
              wx.setStorageSync('userId', String(data.userId));

              if (data.isNewUser) {
                // 新用户去引导页
                wx.redirectTo({ url: '/pages/guide/guide' });
              } else {
                wx.switchTab({ url: '/pages/index/index' });
              }
            })
            .catch((err) => {
              console.error('登录失败', err);
              const msg = (err && err.message) || '登录失败，请检查网络连接';
              wx.showToast({ title: msg, icon: 'none', duration: 2500 });
            })
            .finally(() => this.setData({ loading: false }));
        } else {
          wx.showToast({ title: '微信登录失败，请重试', icon: 'none' });
          this.setData({ loading: false });
        }
      },
      fail: (err) => {
        console.error('wx.login fail', err);
        wx.showToast({ title: '微信登录服务异常', icon: 'none' });
        this.setData({ loading: false });
      }
    });
  }
});
