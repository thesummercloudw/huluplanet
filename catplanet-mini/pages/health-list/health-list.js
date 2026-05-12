const http = require('../../utils/request');

Page({
  data: {
    activeTab: 'list',
    records: [],
    stats: null,
    loading: false,
    days: 30,
    healthTypeNames: {
      vaccine: '疫苗',
      deworm: '驱虫',
      checkup: '体检',
      medicine: '用药',
      weight: '称重'
    },
    healthTypeIcons: {
      vaccine: '💉',
      deworm: '💊',
      checkup: '🏥',
      medicine: '🩺',
      weight: '⚖️'
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
      const records = await http.get('/api/records/health?limit=50');
      this.setData({
        records: (records || []).map(r => ({
          ...r,
          typeLabel: this.data.healthTypeNames[r.healthType] || r.healthType,
          typeIcon: this.data.healthTypeIcons[r.healthType] || '📋',
          costStr: r.cost ? `¥${r.cost}` : '',
          dateStr: r.recordDate || ''
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
      const stats = await http.get(`/api/records/health/stats?days=${this.data.days}`);
      let maxVal = 0;
      if (stats && stats.dailyStats) {
        stats.dailyStats.forEach(d => {
          if (d.count > maxVal) maxVal = d.count;
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
    wx.navigateTo({ url: '/pages/health-add/health-add' });
  },

  onPullDownRefresh() {
    this.loadRecords().then(() => wx.stopPullDownRefresh());
  }
});
