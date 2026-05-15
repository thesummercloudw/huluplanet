const http = require('../../utils/request');

Page({
  data: {
    cats: [],
    selectedCatId: null,
    weight: '',
    recordDate: '',
    note: '',
    submitting: false
  },

  onLoad(options) {
    this.loadCats();
    const today = new Date();
    const dateStr = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`;
    this.setData({ recordDate: dateStr });
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

  onWeightInput(e) { this.setData({ weight: e.detail.value }); },
  onDateChange(e) { this.setData({ recordDate: e.detail.value }); },
  onNoteInput(e) { this.setData({ note: e.detail.value }); },

  async onSubmit() {
    const { selectedCatId, weight, recordDate, note } = this.data;
    if (!selectedCatId) {
      wx.showToast({ title: '请选择猫咪', icon: 'none' }); return;
    }
    if (!weight) {
      wx.showToast({ title: '请输入体重', icon: 'none' }); return;
    }
    if (!recordDate) {
      wx.showToast({ title: '请选择日期', icon: 'none' }); return;
    }

    this.setData({ submitting: true });
    try {
      await http.post('/api/records/health', {
        catId: selectedCatId,
        healthType: 'weight',
        subtype: '称重',
        recordDate,
        valueNumeric: Number(weight),
        note: note || null
      });
      wx.showToast({ title: '体重记录成功 🎉', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 800);
    } catch (e) {
      wx.showToast({ title: '记录失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
