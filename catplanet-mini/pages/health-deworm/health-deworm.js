const http = require('../../utils/request');

Page({
  data: {
    cats: [],
    selectedCatId: null,
    dewormTypes: [
      { value: 'internal', label: '体内驱虫', icon: '/images/icon-health-deworm.svg' },
      { value: 'external', label: '体外驱虫', icon: '/images/icon-health-deworm.svg' },
      { value: 'both', label: '内外同驱', icon: '/images/icon-health-deworm.svg' }
    ],
    dewormType: 'internal',
    productPresets: ['大宠爱', '博来恩', '海乐妙', '福来恩', '爱沃克', '拜宠清'],
    productName: '',
    intervalPresets: [
      { days: 30, label: '1个月后' },
      { days: 60, label: '2个月后' },
      { days: 90, label: '3个月后' }
    ],
    nextInterval: 0,
    recordDate: '',
    nextDate: '',
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

  onDewormType(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ dewormType: type });
    // 根据类型更新品牌推荐
    const presets = {
      internal: ['拜宠清', '海乐妙', '大宠爱'],
      external: ['福来恩', '大宠爱', '爱沃克'],
      both: ['博来恩', '大宠爱', '爱沃克', '海乐妙']
    };
    this.setData({ productPresets: presets[type] || presets.both });
  },

  onProductPreset(e) {
    this.setData({ productName: e.currentTarget.dataset.name });
  },

  onProductInput(e) {
    this.setData({ productName: e.detail.value });
  },

  onDateChange(e) { this.setData({ recordDate: e.detail.value }); },
  
  onIntervalPreset(e) {
    const days = Number(e.currentTarget.dataset.days);
    this.setData({ nextInterval: days });
    // 计算下次日期
    const base = new Date(this.data.recordDate);
    base.setDate(base.getDate() + days);
    const nextStr = `${base.getFullYear()}-${String(base.getMonth()+1).padStart(2,'0')}-${String(base.getDate()).padStart(2,'0')}`;
    this.setData({ nextDate: nextStr });
  },

  onNextDateChange(e) {
    this.setData({ nextDate: e.detail.value, nextInterval: 0 });
  },

  onNoteInput(e) { this.setData({ note: e.detail.value }); },

  async onSubmit() {
    const { selectedCatId, dewormType, productName, recordDate, nextDate, note } = this.data;
    if (!selectedCatId) {
      wx.showToast({ title: '请选择猫咪', icon: 'none' }); return;
    }
    if (!productName) {
      wx.showToast({ title: '请选择或输入驱虫产品', icon: 'none' }); return;
    }
    if (!recordDate) {
      wx.showToast({ title: '请选择驱虫日期', icon: 'none' }); return;
    }

    const typeLabels = { internal: '体内', external: '体外', both: '内外' };
    this.setData({ submitting: true });
    try {
      await http.post('/api/records/health', {
        catId: selectedCatId,
        healthType: 'deworm',
        subtype: `${typeLabels[dewormType]}驱虫 - ${productName}`,
        recordDate,
        nextDueDate: nextDate || null,
        note: note || null
      });
      const successMsg = nextDate ? '记录成功，已设置提醒 🔔' : '驱虫记录成功 🎉';
      wx.showToast({ title: successMsg, icon: 'none' });
      setTimeout(() => wx.navigateBack(), 800);
    } catch (e) {
      wx.showToast({ title: '记录失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
