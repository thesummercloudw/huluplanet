const app = getApp();
const http = require('../../utils/request');

Page({
  data: {
    familyId: null,
    familyName: '',
    inviteCode: '',
    catCount: 0,
    pendingReminders: 0,
    nickname: '',
    avatar: ''
  },

  onShow() {
    if (app.globalData.token) {
      this.loadData();
    }
  },

  async loadData() {
    try {
      const [families, cats, reminders, profile] = await Promise.all([
        http.get('/api/family/my'),
        http.get('/api/cats'),
        http.get('/api/reminders?status=pending'),
        http.get('/api/user/profile')
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
      if (profile) {
        this.setData({
          nickname: profile.nickname || '',
          avatar: profile.avatar || ''
        });
      }
    } catch (e) {
      console.error(e);
    }
  },

  changeAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempPath = res.tempFiles[0].tempFilePath;
        wx.showLoading({ title: '上传中...' });
        try {
          const uploadRes = await http.upload(tempPath);
          await http.put('/api/user/profile', { avatar: uploadRes.url });
          // 重新加载用户信息获取序列化后的URL
          const profile = await http.get('/api/user/profile');
          this.setData({ avatar: profile.avatar || '' });
          wx.showToast({ title: '头像已更新' });
        } catch (e) {
          console.error(e);
          wx.showToast({ title: '上传失败', icon: 'none' });
        } finally {
          wx.hideLoading();
        }
      }
    });
  },

  editNickname() {
    const that = this;
    wx.showModal({
      title: '修改昵称',
      editable: true,
      placeholderText: '请输入新昵称',
      content: this.data.nickname,
      success: async (res) => {
        if (res.confirm && res.content && res.content.trim()) {
          const newNickname = res.content.trim();
          if (newNickname === that.data.nickname) return;
          if (newNickname.length > 20) {
            wx.showToast({ title: '昵称不能超过20字', icon: 'none' });
            return;
          }
          try {
            await http.put('/api/user/profile', { nickname: newNickname });
            that.setData({ nickname: newNickname });
            wx.showToast({ title: '修改成功' });
          } catch (e) {
            console.error(e);
            wx.showToast({ title: '修改失败', icon: 'none' });
          }
        }
      }
    });
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
    wx.removeStorageSync('userId');
    wx.removeStorageSync('currentFamilyId');
    app.globalData.token = '';
    app.globalData.userId = null;
    app.globalData.currentFamilyId = null;
    wx.redirectTo({ url: '/pages/login/login' });
  }
});
