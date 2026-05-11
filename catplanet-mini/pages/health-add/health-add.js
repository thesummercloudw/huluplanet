const http = require('../../utils/request');

Page({
  data: {
    cats: [],
    selectedCatId: null,
    healthType: '',
    healthTypes: [
      { value: 'vaccine', label: '疫苗', icon: '💉' },
      { value: 'deworm', label: '驱虫', icon: '💊' },
      { value: 'checkup', label: '体检', icon: '🏥' },
      { value: 'medicine', label: '用药', icon: '🩺' },
      { value: 'weight', label: '称重', icon: '⚖️' }
    ],
    subtype: '',
    recordDate: '',
    hospitalName: '',
    cost: '',
    valueNumeric: '',
    note: '',
    submitting: false
  },

  onLoad() {
    this.loadCats();
    // 默认日期为今天
    const today = new Date();
    const dateStr = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`;
    this.setData({ recordDate: dateStr });
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
    this.setData({ selectedCatId: Number(e.currentTarget.dataset.id) });
  },

  onTypeSelect(e) {
    this.setData({ healthType: e.currentTarget.dataset.type, subtype: '' });
  },

  onSubtypeInput(e) { this.setData({ subtype: e.detail.value }); },
  onDateChange(e) { this.setData({ recordDate: e.detail.value }); },
  onHospitalInput(e) { this.setData({ hospitalName: e.detail.value }); },
  onCostInput(e) { this.setData({ cost: e.detail.value }); },
  onWeightInput(e) { this.setData({ valueNumeric: e.detail.value }); },
  onNoteInput(e) { this.setData({ note: e.detail.value }); },

  async onSubmit() {
    const { selectedCatId, healthType, subtype, recordDate, hospitalName, cost, valueNumeric, note } = this.data;
    if (!selectedCatId) {
      wx.showToast({ title: '请选择猫咪', icon: 'none' }); return;
    }
    if (!healthType) {
      wx.showToast({ title: '请选择健康类型', icon: 'none' }); return;
    }
    if (!recordDate) {
      wx.showToast({ title: '请选择日期', icon: 'none' }); return;
    }

    this.setData({ submitting: true });
    try {
      await http.post('/api/records/health', {
        catId: selectedCatId,
        healthType,
        subtype: subtype || null,
        recordDate,
        hospitalName: hospitalName || null,
        cost: cost ? Number(cost) : null,
        valueNumeric: valueNumeric ? Number(valueNumeric) : null,
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
