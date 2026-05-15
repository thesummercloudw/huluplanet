const http = require('../../utils/request');

Page({
  data: {
    cats: [],
    selectedCatId: null,
    checkItems: [
      { value: 'blood', label: '血常规', checked: false },
      { value: 'biochem', label: '生化', checked: false },
      { value: 'urine', label: '尿检', checked: false },
      { value: 'xray', label: 'X光', checked: false },
      { value: 'ultrasound', label: 'B超', checked: false },
      { value: 'fecal', label: '粪检', checked: false },
      { value: 'dental', label: '口腔检查', checked: false },
      { value: 'eye', label: '眼科检查', checked: false }
    ],
    customItem: '',
    resultStatus: '',
    resultDetail: '',
    recordDate: '',
    nextDate: '',
    hospitalName: '',
    cost: '',
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

  onCheckItemToggle(e) {
    const index = e.currentTarget.dataset.index;
    const key = `checkItems[${index}].checked`;
    this.setData({ [key]: !this.data.checkItems[index].checked });
  },

  onCustomItemInput(e) {
    this.setData({ customItem: e.detail.value });
  },

  onCustomItemAdd(e) {
    const val = e.detail.value.trim();
    if (!val) return;
    const items = [...this.data.checkItems, { value: val, label: val, checked: true }];
    this.setData({ checkItems: items, customItem: '' });
  },

  onResultStatus(e) {
    this.setData({ resultStatus: e.currentTarget.dataset.status });
  },

  onResultInput(e) { this.setData({ resultDetail: e.detail.value }); },
  onDateChange(e) { this.setData({ recordDate: e.detail.value }); },
  onNextDateChange(e) { this.setData({ nextDate: e.detail.value }); },
  onHospitalInput(e) { this.setData({ hospitalName: e.detail.value }); },
  onCostInput(e) { this.setData({ cost: e.detail.value }); },

  async onSubmit() {
    const { selectedCatId, checkItems, resultStatus, resultDetail, recordDate, nextDate, hospitalName, cost } = this.data;
    if (!selectedCatId) {
      wx.showToast({ title: '请选择猫咪', icon: 'none' }); return;
    }
    
    const selectedItems = checkItems.filter(i => i.checked).map(i => i.label);
    if (selectedItems.length === 0) {
      wx.showToast({ title: '请选择检查项目', icon: 'none' }); return;
    }
    if (!recordDate) {
      wx.showToast({ title: '请选择体检日期', icon: 'none' }); return;
    }

    this.setData({ submitting: true });
    try {
      const statusLabels = { normal: '正常', attention: '需关注', abnormal: '异常' };
      let noteStr = '';
      if (resultStatus) noteStr += `结果: ${statusLabels[resultStatus]}\n`;
      if (resultDetail) noteStr += resultDetail;
      if (nextDate) noteStr += `\n下次体检: ${nextDate}`;

      await http.post('/api/records/health', {
        catId: selectedCatId,
        healthType: 'checkup',
        subtype: selectedItems.join('、'),
        recordDate,
        hospitalName: hospitalName || null,
        cost: cost ? Number(cost) : null,
        note: noteStr || null
      });
      wx.showToast({ title: '体检记录成功 🎉', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 800);
    } catch (e) {
      wx.showToast({ title: '记录失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
