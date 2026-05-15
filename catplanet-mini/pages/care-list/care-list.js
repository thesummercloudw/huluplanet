const http = require('../../utils/request');

Page({
  data: {
    activeTab: 'list',
    records: [],
    stats: null,
    loading: false,
    days: 7,
    cats: [],
    selectedCatId: null,
    careTypeNames: {
      litter: '清理猫砂',
      bath: '洗澡',
      grooming: '梳毛',
      nail: '剪指甲',
      play: '陪玩',
      other: '其他'
    },
    careTypeIcons: {
      litter: '/images/icon-care-litter.svg',
      bath: '/images/icon-care-bath.svg',
      grooming: '/images/icon-care-grooming.svg',
      nail: '/images/icon-care-nail.svg',
      play: '/images/icon-care-play.svg',
      other: '/images/icon-care-other.svg'
    }
  },

  onShow() {
    this.loadCats();
  },

  async loadCats() {
    try {
      const cats = await http.get('/api/cats');
      this.setData({ cats: cats || [] });
    } catch (e) {
      console.error('load cats error', e);
    }
    this.loadRecords();
    if (this.data.activeTab === 'stats') {
      this.loadStats();
    }
  },

  onCatFilter(e) {
    const catId = e.currentTarget.dataset.id || null;
    this.setData({ selectedCatId: catId, stats: null });
    this.loadRecords();
    if (this.data.activeTab === 'stats') {
      this.loadStats();
    }
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
      let url = '/api/records/care?limit=50';
      if (this.data.selectedCatId) {
        url += '&catId=' + this.data.selectedCatId;
      }
      const records = await http.get(url);
      this.setData({
        records: (records || []).map(r => ({
          ...r,
          timeStr: this.formatTime(r.doneAt),
          typeLabel: this.data.careTypeNames[r.careType] || r.careType,
          typeIcon: this.data.careTypeIcons[r.careType] || '/images/icon-care-other.svg'
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
      let url = `/api/records/care/stats?days=${this.data.days}`;
      if (this.data.selectedCatId) {
        url += '&catId=' + this.data.selectedCatId;
      }
      const stats = await http.get(url);
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
    wx.navigateTo({ url: '/pages/care-add/care-add' });
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
