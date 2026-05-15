const http = require('../../utils/request');

Page({
  data: {
    cats: [],
    selectedCatId: null,
    medicineName: '',
    reasonPresets: ['感冒', '肠胃炎', '皮肤病', '泌尿问题', '眼部感染', '术后恢复'],
    reason: '',
    dosage: '',
    dosageUnits: ['片', 'ml', 'mg', '粒', '滴'],
    dosageUnit: '片',
    frequencyOptions: [
      { value: 'once', label: '每天1次' },
      { value: 'twice', label: '每天2次' },
      { value: 'three', label: '每天3次' },
      { value: 'asneeded', label: '按需' }
    ],
    frequency: 'once',
    duration: '',
    recordDate: '',
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

  onMedicineInput(e) { this.setData({ medicineName: e.detail.value }); },

  onReasonPreset(e) {
    this.setData({ reason: e.currentTarget.dataset.reason });
  },

  onReasonInput(e) { this.setData({ reason: e.detail.value }); },
  onDosageInput(e) { this.setData({ dosage: e.detail.value }); },
  
  onUnitSelect(e) {
    this.setData({ dosageUnit: e.currentTarget.dataset.unit });
  },

  onFrequencySelect(e) {
    this.setData({ frequency: e.currentTarget.dataset.freq });
  },

  onDurationInput(e) { this.setData({ duration: e.detail.value }); },
  onDateChange(e) { this.setData({ recordDate: e.detail.value }); },
  onHospitalInput(e) { this.setData({ hospitalName: e.detail.value }); },
  onCostInput(e) { this.setData({ cost: e.detail.value }); },
  onNoteInput(e) { this.setData({ note: e.detail.value }); },

  async onSubmit() {
    const { selectedCatId, medicineName, reason, dosage, dosageUnit, frequency, duration, recordDate, hospitalName, cost, note } = this.data;
    if (!selectedCatId) {
      wx.showToast({ title: '请选择猫咪', icon: 'none' }); return;
    }
    if (!medicineName) {
      wx.showToast({ title: '请输入药品名称', icon: 'none' }); return;
    }
    if (!recordDate) {
      wx.showToast({ title: '请选择开始日期', icon: 'none' }); return;
    }

    const freqLabels = { once: '每天1次', twice: '每天2次', three: '每天3次', asneeded: '按需' };
    this.setData({ submitting: true });
    try {
      let noteStr = '';
      if (reason) noteStr += `原因: ${reason}\n`;
      if (dosage) noteStr += `剂量: ${dosage}${dosageUnit} ${freqLabels[frequency]}\n`;
      if (duration) noteStr += `疗程: ${duration}天\n`;
      if (note) noteStr += note;

      await http.post('/api/records/health', {
        catId: selectedCatId,
        healthType: 'medicine',
        subtype: medicineName,
        recordDate,
        hospitalName: hospitalName || null,
        cost: cost ? Number(cost) : null,
        note: noteStr || null
      });
      wx.showToast({ title: '用药记录成功 🎉', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 800);
    } catch (e) {
      wx.showToast({ title: '记录失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
