const app = getApp();
const http = require('../../utils/request');

Page({
  data: {
    familyId: null,
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
          familyId: families[0].familyId,
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

  editFamilyName() {
    const that = this;
    wx.showModal({
      title: '修改家庭名称',
      editable: true,
      placeholderText: '请输入新的家庭名称',
      content: this.data.familyName,
      success: async (res) => {
        if (res.confirm && res.content && res.content.trim()) {
          const newName = res.content.trim();
          if (newName === that.data.familyName) return;
          try {
            await http.put(`/api/family/${that.data.familyId}/name`, { name: newName });
            that.setData({ familyName: newName });
            wx.showToast({ title: '修改成功' });
          } catch (e) {
            console.error(e);
            wx.showToast({ title: '修改失败', icon: 'none' });
          }
        }
      }
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
