const http = require('../../utils/request');

Page({
  data: {
    activeTab: 'all',
    foods: [],
    pgcList: [],
    loading: false,
    page: 1,
    filters: {
      foodType: '',
      ageStage: ''
    }
  },

  onShow() {
    this.loadData();
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab, page: 1 });
    if (tab === 'pgc') {
      this.loadPgc();
    } else {
      this.loadFoods();
    }
  },

  async loadData() {
    this.loadFoods();
    this.loadPgc();
  },

  async loadFoods() {
    this.setData({ loading: true });
    try {
      const { foodType, ageStage } = this.data.filters;
      let url = `/api/catfood?page=${this.data.page}&size=20`;
      if (foodType) url += `&foodType=${foodType}`;
      if (ageStage) url += `&ageStage=${ageStage}`;
      const foods = await http.get(url);
      this.setData({ foods: foods || [] });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  async loadPgc() {
    try {
      const pgcList = await http.get('/api/catfood/pgc?limit=10');
      this.setData({ pgcList: pgcList || [] });
    } catch (e) {
      console.error(e);
    }
  },

  onFilterType(e) {
    const type = e.currentTarget.dataset.type;
    const current = this.data.filters.foodType;
    this.setData({
      'filters.foodType': current === type ? '' : type,
      page: 1
    });
    this.loadFoods();
  },

  onFilterAge(e) {
    const age = e.currentTarget.dataset.age;
    const current = this.data.filters.ageStage;
    this.setData({
      'filters.ageStage': current === age ? '' : age,
      page: 1
    });
    this.loadFoods();
  },

  goDetail(e) {
    const foodId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/food-detail/food-detail?foodId=${foodId}` });
  },

  goPgcDetail(e) {
    const foodId = e.currentTarget.dataset.foodid;
    wx.navigateTo({ url: `/pages/food-detail/food-detail?foodId=${foodId}` });
  },

  onPullDownRefresh() {
    this.setData({ page: 1 });
    this.loadData().then(() => wx.stopPullDownRefresh());
  }
});
