/**
 * 呼噜星球小程序
 */
App({
  globalData: {
    baseUrl: 'http://localhost:8080',
    token: '',
    userInfo: null,
    currentFamilyId: null
  },

  onLaunch() {
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      this.globalData.currentFamilyId = wx.getStorageSync('currentFamilyId');
    }
  }
});
