const http = require('../../utils/request');

Page({
  data: {
    cat: null,
    activeTab: 'info',
    records: [],
    recordLoading: false
  },

  onLoad(options) {
    if (options.catId) {
      this.catId = options.catId;
      this.loadCat(options.catId);
    }
  },

  async loadCat(catId) {
    try {
      const cat = await http.get(`/api/cats/${catId}`);
      this.setData({ cat });
    } catch (e) {
      console.error(e);
    }
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    if (tab === 'records' && this.data.records.length === 0) {
      this.loadRecords();
    }
  },

  async loadRecords() {
    this.setData({ recordLoading: true });
    try {
      const records = await http.get(`/api/timeline?catId=${this.catId}&limit=30`);
      this.setData({
        records: (records || []).map(r => ({
          ...r,
          icon: this.getTypeIcon(r.type),
          title: r.summary,
          description: '',
          timeText: this.formatTime(r.time)
        }))
      });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ recordLoading: false });
    }
  },

  getTypeIcon(type) {
    const icons = { feeding: '🍗', care: '✨', health: '💊' };
    return icons[type] || '📋';
  },

  formatTime(dateStr) {
    const d = new Date(dateStr);
    const now = new Date();
    const diff = now - d;
    if (diff < 60000) return '刚刚';
    if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
    if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
    if (diff < 604800000) return Math.floor(diff / 86400000) + '天前';
    return (d.getMonth() + 1) + '月' + d.getDate() + '日';
  },

  goEdit() {
    const catId = this.data.cat.catId;
    wx.navigateTo({ url: `/pages/cat-edit/cat-edit?catId=${catId}` });
  },

  goAddFeeding() {
    wx.navigateTo({ url: `/pages/feeding-add/feeding-add?catId=${this.catId}` });
  },

  goAddCare() {
    wx.navigateTo({ url: `/pages/care-add/care-add?catId=${this.catId}` });
  },

  goAddHealth() {
    wx.navigateTo({ url: `/pages/health-add/health-add?catId=${this.catId}` });
  },

  onShow() {
    if (this.data.activeTab === 'records' && this.catId) {
      this.loadRecords();
    }
  },

  async handleDelete() {
    const res = await wx.showModal({
      title: '确认删除',
      content: `确定要从星球移除 ${this.data.cat.name} 吗？`,
      confirmColor: '#FF8C69'
    });
    if (res.confirm) {
      try {
        await http.del(`/api/cats/${this.data.cat.catId}`);
        wx.showToast({ title: '已移除', icon: 'success' });
        setTimeout(() => wx.navigateBack(), 1000);
      } catch (e) {
        console.error(e);
      }
    }
  }
});
