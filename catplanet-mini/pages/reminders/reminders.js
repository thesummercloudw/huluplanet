const http = require('../../utils/request');

Page({
  data: {
    activeTab: 'pending',
    reminders: [],
    loading: false
  },

  onShow() {
    this.loadReminders();
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    this.loadReminders();
  },

  async loadReminders() {
    this.setData({ loading: true });
    try {
      const status = this.data.activeTab === 'all' ? '' : this.data.activeTab;
      const url = status ? `/api/reminders?status=${status}` : '/api/reminders';
      const reminders = await http.get(url);
      this.setData({
        reminders: (reminders || []).map(r => ({
          ...r,
          icon: this.getTypeIcon(r.type),
          triggerText: this.formatTrigger(r.triggerAt),
          statusText: this.getStatusText(r.status)
        }))
      });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  getTypeIcon(type) {
    const icons = {
      vaccine: '💉', deworm: '💊', checkup: '🏥',
      feeding: '🍗', care: '✨', custom: '🔔'
    };
    return icons[type] || '🔔';
  },

  getStatusText(status) {
    const map = { pending: '待触发', sent: '已提醒', done: '已完成', cancelled: '已取消' };
    return map[status] || status;
  },

  formatTrigger(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    const now = new Date();
    const diff = d - now;
    if (diff < 0) return '已过期';
    if (diff < 86400000) return '今天 ' + this.pad(d.getHours()) + ':' + this.pad(d.getMinutes());
    if (diff < 172800000) return '明天 ' + this.pad(d.getHours()) + ':' + this.pad(d.getMinutes());
    if (diff < 604800000) return Math.ceil(diff / 86400000) + '天后';
    return (d.getMonth() + 1) + '/' + d.getDate();
  },

  pad(n) {
    return n < 10 ? '0' + n : '' + n;
  },

  async handleDone(e) {
    const id = e.currentTarget.dataset.id;
    try {
      await http.put(`/api/reminders/${id}/done`);
      wx.showToast({ title: '已完成', icon: 'success' });
      this.loadReminders();
    } catch (e) {
      console.error(e);
    }
  },

  async handleCancel(e) {
    const id = e.currentTarget.dataset.id;
    const res = await wx.showModal({
      title: '取消提醒',
      content: '确定要取消这个提醒吗？',
      confirmColor: '#E58C7A'
    });
    if (res.confirm) {
      try {
        await http.put(`/api/reminders/${id}/cancel`);
        wx.showToast({ title: '已取消' });
        this.loadReminders();
      } catch (e) {
        console.error(e);
      }
    }
  },

  goAdd() {
    wx.navigateTo({ url: '/pages/health-add/health-add' });
  }
});
