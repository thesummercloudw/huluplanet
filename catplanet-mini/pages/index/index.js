const http = require('../../utils/request');
const { resolveImage, resolveThumb } = require('../../utils/request');
const app = getApp();

Page({
  data: {
    cats: [],
    familyName: '',
    timeline: [],
    sightings: [],

    loading: true,
    timelineExpanded: false
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

        // 并行加载猫咪、Timeline、猫咪出没
        const [cats, timeline, sightings] = await Promise.all([
          http.get('/api/cats'),
          http.get('/api/timeline?limit=20'),
          http.get('/api/sightings?limit=6')
        ]);
        this.setData({
          cats: (cats || []).map(c => ({ ...c, avatar: resolveThumb(c.avatar, 128) })),
          timeline: this.groupTimeline(timeline || []),
          timelineCount: (timeline || []).length,
          sightings: (sightings || []).map(item => ({
            ...item,
            image: resolveThumb(item.image, 300),
            timeStr: this.formatTime(item.createdAt)
          })),

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

  goSightingDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/sighting-detail/sighting-detail?sightingId=${id}` });
  },


  goCatDetail(e) {
    const catId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/cat-detail/cat-detail?catId=${catId}` });
  },

  toggleTimeline() {
    this.setData({ timelineExpanded: !this.data.timelineExpanded });
  },

  // 手账风格：按日期分组 timeline
  groupTimeline(list) {
    const typeEmoji = { feeding: '/images/icon-feeding.svg', care: '/images/icon-care.svg', health: '/images/icon-health.svg' };
    const typeLabel = { feeding: '喂食', care: '养护', health: '健康' };
    const typeColor = { feeding: 'blue', care: 'green', health: 'pink' };
    const now = new Date();
    const todayStr = this.dateKey(now);
    const yestStr = this.dateKey(new Date(now - 86400000));
    const groups = {};
    let gi = 0;
    list.forEach(item => {
      const d = new Date(item.time);
      const key = this.dateKey(d);
      let label = key;
      if (key === todayStr) label = '今天';
      else if (key === yestStr) label = '昨天';
      if (!groups[key]) groups[key] = { label, items: [] };
      groups[key].items.push({
        ...item,
        gi: gi++,
        emoji: typeEmoji[item.type] || '/images/icon-care-other.svg',
        typeLabel: typeLabel[item.type] || '记录',
        typeColor: typeColor[item.type] || 'blue',
        timeStr: this.formatTime(item.time),
        hourMin: this.formatHourMin(d)
      });
    });
    return Object.keys(groups)
      .sort((a, b) => b.localeCompare(a))
      .map(k => groups[k]);
  },

  dateKey(d) {
    return (d.getMonth() + 1) + '/' + d.getDate();
  },

  formatHourMin(d) {
    return String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0');
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
