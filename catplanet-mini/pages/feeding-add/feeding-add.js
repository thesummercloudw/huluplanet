const http = require('../../utils/request');
const app = getApp();

Page({
  data: {
    cats: [],
    selectedCatId: null,
    foodName: '',
    amountG: '',
    mealType: 'main',
    mealTypes: [
      { value: 'main', label: '主食' },
      { value: 'wet', label: '湿粮' },
      { value: 'snack', label: '零食' },
      { value: 'dry', label: '干粮' }
    ],
    quickAmounts: [30, 50, 80],
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
    this.setData({ selectedCatId: Number(e.currentTarget.dataset.id) });
  },

  onFoodInput(e) {
    this.setData({ foodName: e.detail.value });
  },

  onAmountInput(e) {
    this.setData({ amountG: e.detail.value });
  },

  onQuickAmount(e) {
    this.setData({ amountG: String(e.currentTarget.dataset.amount) });
  },

  onMealTypeChange(e) {
    this.setData({ mealType: this.data.mealTypes[e.detail.value].value });
  },

  async onSubmit() {
    const { selectedCatId, foodName, amountG, mealType } = this.data;
    if (!selectedCatId) {
      wx.showToast({ title: '请选择猫咪', icon: 'none' }); return;
    }
    if (!foodName.trim()) {
      wx.showToast({ title: '请输入食物名称', icon: 'none' }); return;
    }

    this.setData({ submitting: true });
    try {
      await http.post('/api/records/feeding', {
        catId: selectedCatId,
        foodName: foodName.trim(),
        amountG: amountG ? Number(amountG) : null,
        mealType
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
