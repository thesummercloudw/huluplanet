const http = require('../../utils/request');

Page({
  data: {
    cats: [],
    selectedCatId: null
  },

  onLoad(options) {
    this.loadCats();
    if (options && options.catId) {
      this.setData({ selectedCatId: options.catId });
    }
  },

  async loadCats() {
    try {
      const cats = await http.get('/api/cats');
      this.setData({
        cats: cats || [],
        selectedCatId: this.data.selectedCatId || (cats && cats.length > 0 ? cats[0].catId : null)
      });
    } catch (e) {
      console.error('load cats error', e);
    }
  },

  onCatSelect(e) {
    this.setData({ selectedCatId: e.currentTarget.dataset.id });
  },

  goPage(e) {
    const type = e.currentTarget.dataset.type;
    const catId = this.data.selectedCatId;
    const catParam = catId ? `?catId=${catId}` : '';
    
    const pageMap = {
      vaccine: '/pages/health-vaccine/health-vaccine',
      deworm: '/pages/health-deworm/health-deworm',
      checkup: '/pages/health-checkup/health-checkup',
      medicine: '/pages/health-medicine/health-medicine',
      weight: '/pages/health-vaccine/health-vaccine' // weight stays simple
    };

    // weight uses a simple inline modal
    if (type === 'weight') {
      this.showWeightInput();
      return;
    }

    wx.navigateTo({ url: pageMap[type] + catParam });
  },

  showWeightInput() {
    const catId = this.data.selectedCatId;
    if (!catId) {
      wx.showToast({ title: '请先选择猫咪', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: `/pages/health-weight/health-weight?catId=${catId}` });
  }
});
