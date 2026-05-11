const http = require('../../utils/request');

Page({
  data: {
    foodId: null,
    foodName: '',
    score: 0,
    content: '',
    cats: [],
    catId: null
  },

  onLoad(options) {
    this.setData({
      foodId: options.foodId,
      foodName: options.foodName || ''
    });
    this.loadCats();
  },

  async loadCats() {
    try {
      const cats = await http.get('/api/cats');
      this.setData({ cats: cats || [] });
    } catch (e) {
      console.error(e);
    }
  },

  setScore(e) {
    this.setData({ score: parseInt(e.currentTarget.dataset.score) });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  onSelectCat(e) {
    const catId = e.currentTarget.dataset.catid;
    this.setData({ catId: this.data.catId === catId ? null : catId });
  },

  async onSubmit() {
    const { foodId, score, content, catId } = this.data;
    if (!score) {
      wx.showToast({ title: '请评分', icon: 'none' });
      return;
    }
    if (!content.trim()) {
      wx.showToast({ title: '请输入短评内容', icon: 'none' });
      return;
    }
    if (content.length > 200) {
      wx.showToast({ title: '短评不能超过200字', icon: 'none' });
      return;
    }

    try {
      await http.post('/api/catfood/ugc', { foodId, score, content, catId });
      wx.showToast({ title: '提交成功，审核中', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 1500);
    } catch (e) {
      wx.showToast({ title: '提交失败', icon: 'none' });
    }
  }
});
