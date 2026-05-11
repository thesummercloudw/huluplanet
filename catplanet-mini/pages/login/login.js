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
              wx.setStorageSync('token', data.token);

              if (data.isNewUser) {
                // 新用户去引导页
                wx.redirectTo({ url: '/pages/guide/guide' });
              } else {
                wx.switchTab({ url: '/pages/index/index' });
              }
            })
            .catch(() => {})
            .finally(() => this.setData({ loading: false }));
        }
      },
      fail: () => this.setData({ loading: false })
    });
  }
});
