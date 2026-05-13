/**
 * 呼噜星球小程序
 */
App({
  globalData: {
    // 内网穿透地址（natapp），模拟器和真机通用
    baseUrl: 'http://127.0.0.1:8080',
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
