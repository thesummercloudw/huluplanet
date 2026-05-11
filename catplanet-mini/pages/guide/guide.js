const http = require('../../utils/request');
const app = getApp();

Page({
  data: {
    step: 1,
    familyName: '',
    inviteCode: '',
    loading: false
  },

  onFamilyNameInput(e) {
    this.setData({ familyName: e.detail.value });
  },

  onInviteCodeInput(e) {
    this.setData({ inviteCode: e.detail.value });
  },

  async createFamily() {
    if (!this.data.familyName.trim()) {
      wx.showToast({ title: '请输入家庭名称', icon: 'none' });
      return;
    }
    this.setData({ loading: true });
    try {
      const family = await http.post('/api/family', { name: this.data.familyName });
      app.globalData.currentFamilyId = family.familyId;
      wx.setStorageSync('currentFamilyId', family.familyId);
      wx.switchTab({ url: '/pages/index/index' });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  async joinFamily() {
    if (!this.data.inviteCode.trim()) {
      wx.showToast({ title: '请输入邀请码', icon: 'none' });
      return;
    }
    this.setData({ loading: true });
    try {
      const family = await http.post('/api/family/join', { inviteCode: this.data.inviteCode });
      app.globalData.currentFamilyId = family.familyId;
      wx.setStorageSync('currentFamilyId', family.familyId);
      wx.switchTab({ url: '/pages/index/index' });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  switchToCreate() {
    this.setData({ step: 1 });
  },

  switchToJoin() {
    this.setData({ step: 2 });
  }
});
