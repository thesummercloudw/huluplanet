const http = require('../../utils/request');

Page({
  data: {
    cats: [],
    selectedCatId: null,
    vaccinePresets: ['猫三联', '狂犬疫苗', '猫五联', '猫瘟疫苗', 'FeLV疫苗'],
    vaccineName: '',
    doseOptions: [
      { value: 1, label: '第1针' },
      { value: 2, label: '第2针' },
      { value: 3, label: '第3针' },
      { value: 0, label: '加强针' }
    ],
    doseNumber: 1,
    recordDate: '',
    nextDate: '',
    hospitalName: '',
    cost: '',
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

  onVaccinePreset(e) {
    this.setData({ vaccineName: e.currentTarget.dataset.name });
  },

  onVaccineInput(e) {
    this.setData({ vaccineName: e.detail.value });
  },

  onDoseSelect(e) {
    this.setData({ doseNumber: Number(e.currentTarget.dataset.dose) });
  },

  onDateChange(e) { this.setData({ recordDate: e.detail.value }); },
  onNextDateChange(e) { this.setData({ nextDate: e.detail.value }); },
  onHospitalInput(e) { this.setData({ hospitalName: e.detail.value }); },
  onCostInput(e) { this.setData({ cost: e.detail.value }); },
  onNoteInput(e) { this.setData({ note: e.detail.value }); },

  async onSubmit() {
    const { selectedCatId, vaccineName, doseNumber, recordDate, nextDate, hospitalName, cost, note } = this.data;
    if (!selectedCatId) {
      wx.showToast({ title: '请选择猫咪', icon: 'none' }); return;
    }
    if (!vaccineName) {
      wx.showToast({ title: '请选择或输入疫苗类型', icon: 'none' }); return;
    }
    if (!recordDate) {
      wx.showToast({ title: '请选择接种日期', icon: 'none' }); return;
    }

    this.setData({ submitting: true });
    try {
      const subtypeStr = doseNumber > 0 ? `${vaccineName} 第${doseNumber}针` : `${vaccineName} 加强针`;
      await http.post('/api/records/health', {
        catId: selectedCatId,
        healthType: 'vaccine',
        subtype: subtypeStr,
        recordDate,
        nextDueDate: nextDate || null,
        hospitalName: hospitalName || null,
        cost: cost ? Number(cost) : null,
        note: note || null
      });
      const successMsg = nextDate ? '记录成功，已设置提醒 🔔' : '疫苗记录成功 🎉';
      wx.showToast({ title: successMsg, icon: 'none' });
      setTimeout(() => wx.navigateBack(), 800);
    } catch (e) {
      wx.showToast({ title: '记录失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
