const app = getApp();

Page({
  data: {
    familyName: '',
    inviteCode: ''
  },

  onShow() {
    const familyId = app.globalData.currentFamilyId;
    if (familyId) {
      this.loadFamily();
    }
  },

  async loadFamily() {
    const http = require('../../utils/request');
    try {
      const families = await http.get('/api/family/my');
      if (families && families.length > 0) {
        this.setData({
          familyName: families[0].name,
          inviteCode: families[0].inviteCode
        });
      }
    } catch (e) {
      console.error(e);
    }
  },

  copyInviteCode() {
    wx.setClipboardData({
      data: this.data.inviteCode,
      success: () => wx.showToast({ title: '已复制邀请码' })
    });
  },

  handleLogout() {
    wx.removeStorageSync('token');
    wx.removeStorageSync('currentFamilyId');
    app.globalData.token = '';
    app.globalData.currentFamilyId = null;
    wx.redirectTo({ url: '/pages/login/login' });
  }
});
