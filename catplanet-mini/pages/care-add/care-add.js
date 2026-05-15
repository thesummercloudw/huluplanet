const http = require('../../utils/request');

Page({
  data: {
    cats: [],
    selectedCatId: null,
    careType: '',
    careTypes: [
      { value: 'litter', label: '清理猫砂', icon: '/images/icon-care-litter.svg' },
      { value: 'bath', label: '洗澡', icon: '/images/icon-care-bath.svg' },
      { value: 'grooming', label: '梳毛', icon: '/images/icon-care-grooming.svg' },
      { value: 'nail', label: '剪指甲', icon: '/images/icon-care-nail.svg' },
      { value: 'play', label: '陪玩', icon: '/images/icon-care-play.svg' },
      { value: 'other', label: '其他', icon: '/images/icon-care-other.svg' }
    ],
    note: '',
    submitting: false
  },

  onLoad() {
    this.loadCats();
  },

  async loadCats() {
    try {
      const cats = await http.get('/api/cats');
      this.setData({
        cats: cats || [],
        selectedCatId: cats && cats.length > 0 ? cats[0].catId : null
      });
    } catch (e) {
      console.error('load cats error', e);
    }
  },

  onCatSelect(e) {
    this.setData({ selectedCatId: e.currentTarget.dataset.id });
  },

  onTypeSelect(e) {
    this.setData({ careType: e.currentTarget.dataset.type });
  },

  onNoteInput(e) {
    this.setData({ note: e.detail.value });
  },

  async onSubmit() {
    const { selectedCatId, careType, note } = this.data;
    if (!selectedCatId) {
      wx.showToast({ title: '请选择猫咪', icon: 'none' }); return;
    }
    if (!careType) {
      wx.showToast({ title: '请选择养护类型', icon: 'none' }); return;
    }

    this.setData({ submitting: true });
    try {
      await http.post('/api/records/care', {
        catId: selectedCatId,
        careType,
        note: note || null
      });
      wx.showToast({ title: '记录成功 🎉', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 800);
    } catch (e) {
      wx.showToast({ title: '记录失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
