const http = require('../../utils/request');
const app = getApp();

const FOOD_HISTORY_KEY = 'feeding_food_name_history';
const MAX_HISTORY = 10;

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
    submitting: false,
    foodHistory: []
  },

  onLoad() {
    this.loadCats();
    this.loadFoodHistory();
  },

  loadFoodHistory() {
    const history = wx.getStorageSync(FOOD_HISTORY_KEY) || [];
    this.setData({ foodHistory: history });
  },

  saveFoodHistory(name) {
    if (!name) return;
    let history = wx.getStorageSync(FOOD_HISTORY_KEY) || [];
    // 去重：如果已存在则移到最前
    history = history.filter(item => item !== name);
    history.unshift(name);
    // 限制数量
    if (history.length > MAX_HISTORY) {
      history = history.slice(0, MAX_HISTORY);
    }
    wx.setStorageSync(FOOD_HISTORY_KEY, history);
    this.setData({ foodHistory: history });
  },

  onFoodHistoryTap(e) {
    const name = e.currentTarget.dataset.name;
    this.setData({ foodName: name });
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
      const trimmedName = foodName.trim();
      await http.post('/api/records/feeding', {
        catId: selectedCatId,
        foodName: trimmedName,
        amountG: amountG ? Number(amountG) : null,
        mealType
      });
      this.saveFoodHistory(trimmedName);
      wx.showToast({ title: '记录成功 🎉', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 800);
    } catch (e) {
      wx.showToast({ title: '记录失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
