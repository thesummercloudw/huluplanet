const http = require('../../utils/request');
const app = getApp();

Page({
  data: {
    cats: [],
    familyName: '',
    timeline: [],
    sightings: [],
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

        // 并行加载猫咪、Timeline 和 猫咪出没
        const [cats, timeline, sightings] = await Promise.all([
          http.get('/api/cats'),
          http.get('/api/timeline?limit=10'),
          http.get('/api/sightings?limit=6')
        ]);
        this.setData({
          cats: cats || [],
          timeline: (timeline || []).map(item => ({
            ...item,
            timeStr: this.formatTime(item.time)
          })),
          sightings: (sightings || []).map(item => ({
            ...item,
            timeStr: this.formatTime(item.createdAt)
          }))
        });
      } else {
        this.setData({ cats: [], familyName: '', timeline: [] });
      }
    } catch (e) {
      console.error('loadData error:', e);
    } finally {
      this.setData({ loading: false });
    }
  },

  // 快捷记录入口
  goFeeding() {
    wx.navigateTo({ url: '/pages/feeding-add/feeding-add' });
  },
  goCare() {
    wx.navigateTo({ url: '/pages/care-add/care-add' });
  },
  goHealth() {
    wx.navigateTo({ url: '/pages/health-add/health-add' });
  },

  goAddCat() {
    wx.navigateTo({ url: '/pages/cat-edit/cat-edit' });
  },

  // 查看记录列表入口
  goFeedingList() {
    wx.navigateTo({ url: '/pages/feeding-list/feeding-list' });
  },
  goCareList() {
    wx.navigateTo({ url: '/pages/care-list/care-list' });
  },
  goHealthList() {
    wx.navigateTo({ url: '/pages/health-list/health-list' });
  },

  goSightingAdd() {
    wx.navigateTo({ url: '/pages/sighting-add/sighting-add' });
  },

  goCatDetail(e) {
    const catId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/cat-detail/cat-detail?catId=${catId}` });
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  },

  formatTime(timeStr) {
    if (!timeStr) return '';
    const date = new Date(timeStr);
    const now = new Date();
    const diffMs = now - date;
    const diffMin = Math.floor(diffMs / 60000);
    if (diffMin < 1) return '刚刚';
    if (diffMin < 60) return diffMin + '分钟前';
    const diffHour = Math.floor(diffMin / 60);
    if (diffHour < 24) return diffHour + '小时前';
    const diffDay = Math.floor(diffHour / 24);
    if (diffDay < 7) return diffDay + '天前';
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return month + '/' + day;
  }
});
