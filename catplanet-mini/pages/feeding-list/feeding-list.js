const http = require('../../utils/request');

Page({
  data: {
    activeTab: 'list',  // list | stats
    records: [],
    stats: null,
    loading: false,
    days: 7,
    mealTypeNames: {
      main: '主食',
      wet: '湿粮',
      snack: '零食',
      dry: '干粮'
    }
  },

  onShow() {
    this.loadRecords();
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    if (tab === 'stats' && !this.data.stats) {
      this.loadStats();
    }
  },

  async loadRecords() {
    this.setData({ loading: true });
    try {
      const records = await http.get('/api/records/feeding?limit=50');
      this.setData({
        records: (records || []).map(r => ({
          ...r,
          timeStr: this.formatTime(r.fedAt),
          mealLabel: this.data.mealTypeNames[r.mealType] || r.mealType
        }))
      });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  async loadStats() {
    try {
      const stats = await http.get(`/api/records/feeding/stats?days=${this.data.days}`);
      // 计算柱状图最大值
      let maxVal = 0;
      if (stats && stats.dailyStats) {
        stats.dailyStats.forEach(d => {
          if (d.value > maxVal) maxVal = d.value;
        });
      }
      this.setData({ stats, maxBarValue: maxVal || 1 });
    } catch (e) {
      console.error(e);
    }
  },

  onDaysChange(e) {
    const days = Number(e.currentTarget.dataset.days);
    this.setData({ days });
    this.loadStats();
  },

  goAdd() {
    wx.navigateTo({ url: '/pages/feeding-add/feeding-add' });
  },

  onPullDownRefresh() {
    this.loadRecords().then(() => wx.stopPullDownRefresh());
  },

  formatTime(timeStr) {
    if (!timeStr) return '';
    const d = new Date(timeStr.replace(/-/g, '/').replace('T', ' '));
    if (isNaN(d.getTime())) return '';
    const now = new Date();
    const diff = now - d;
    if (diff < 60000) return '刚刚';
    if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
    if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
    const month = d.getMonth() + 1;
    const day = d.getDate();
    const hour = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${month}/${day} ${hour}:${min}`;
  }
});
