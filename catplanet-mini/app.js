/**
 * 呼噄星球小程序
 */
App({
  globalData: {
    // 内网穿透地址（natapp），模拟器和真机通用
    baseUrl: '',
    token: '',
    userId: null,
    userInfo: null,
    currentFamilyId: null
  },

  onLaunch() {

    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      this.globalData.userId = wx.getStorageSync('userId') || null;
      this.globalData.currentFamilyId = wx.getStorageSync('currentFamilyId');
    }
  }
});
