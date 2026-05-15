const http = require('../../utils/request');

Page({
  data: {
    hospitalId: null,
    hospitalName: '',
    score: 0,
    content: '',
    tags: [
      { name: '态度好', selected: false },
      { name: '环境干净', selected: false },
      { name: '技术专业', selected: false },
      { name: '价格合理', selected: false },
      { name: '等待时间短', selected: false },
      { name: '设备先进', selected: false },
      { name: '品种齐全', selected: false },
      { name: '服务贴心', selected: false }
    ]
  },

  onLoad(options) {
    this.setData({
      hospitalId: options.hospitalId,
      hospitalName: decodeURIComponent(options.name || '')
    });
  },

  setScore(e) {
    this.setData({ score: parseInt(e.currentTarget.dataset.score) });
  },

  toggleTag(e) {
    const index = e.currentTarget.dataset.index;
    const key = `tags[${index}].selected`;
    this.setData({ [key]: !this.data.tags[index].selected });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  async onSubmit() {
    const { hospitalId, score, content, tags } = this.data;
    if (!score) {
      wx.showToast({ title: '请评分', icon: 'none' });
      return;
    }
    if (!content.trim()) {
      wx.showToast({ title: '请输入评价内容', icon: 'none' });
      return;
    }

    const selectedTags = tags.filter(t => t.selected).map(t => t.name);
    try {
      await http.post('/api/hospitals/reviews', {
        hospitalId,
        score,
        content,
        serviceTags: selectedTags.length > 0 ? selectedTags : null
      });
      wx.showToast({ title: '发表成功', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 1500);
    } catch (e) {
      wx.showToast({ title: '提交失败', icon: 'none' });
    }
  }
});
