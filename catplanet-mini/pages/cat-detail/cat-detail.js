const http = require('../../utils/request');

Page({
  data: {
    cat: null
  },

  onLoad(options) {
    if (options.catId) {
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

  goEdit() {
    const catId = this.data.cat.catId;
    wx.navigateTo({ url: `/pages/cat-edit/cat-edit?catId=${catId}` });
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
