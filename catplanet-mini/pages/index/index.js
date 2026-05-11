const http = require('../../utils/request');
const app = getApp();

Page({
  data: {
    cats: [],
    familyName: '',
    loading: true
  },

  onShow() {
    if (!app.globalData.token) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
    this.loadData();
  },

  async loadData() {
    this.setData({ loading: true });
    try {
      // 加载家庭列表
      const families = await http.get('/api/family/my');
      if (families && families.length > 0) {
        const family = families[0];
        app.globalData.currentFamilyId = family.familyId;
        wx.setStorageSync('currentFamilyId', family.familyId);
        this.setData({ familyName: family.name });

        // 加载猫咪列表
        const cats = await http.get('/api/cats');
        this.setData({ cats: cats || [] });
      } else {
        this.setData({ cats: [], familyName: '' });
      }
    } catch (e) {
      console.error('loadData error:', e);
    } finally {
      this.setData({ loading: false });
    }
  },

  goAddCat() {
    wx.navigateTo({ url: '/pages/cat-edit/cat-edit' });
  },

  goCatDetail(e) {
    const catId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/cat-detail/cat-detail?catId=${catId}` });
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
