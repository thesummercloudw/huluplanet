const app = getApp();
const http = require('../../utils/request');

Page({
  data: {
    familyName: '',
    inviteCode: '',
    catCount: 0,
    pendingReminders: 0
  },

  onShow() {
    if (app.globalData.token) {
      this.loadData();
    }
  },

  async loadData() {
    try {
      const [families, cats, reminders] = await Promise.all([
        http.get('/api/family/my'),
        http.get('/api/cats'),
        http.get('/api/reminders?status=pending')
      ]);

      if (families && families.length > 0) {
        this.setData({
          familyName: families[0].name,
          inviteCode: families[0].inviteCode
        });
      }
      this.setData({
        catCount: (cats || []).length,
        pendingReminders: (reminders || []).length
      });
    } catch (e) {
      console.error(e);
    }
  },

  goReminders() {
    wx.navigateTo({ url: '/pages/reminders/reminders' });
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
