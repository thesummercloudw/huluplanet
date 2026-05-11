const http = require('../../utils/request');

Page({
  data: {
    foodId: null,
    food: null,
    pgcReview: null,
    ugcReviews: [],
    activeSection: 'info'
  },

  onLoad(options) {
    if (options.foodId) {
      this.setData({ foodId: options.foodId });
      this.loadDetail(options.foodId);
    }
  },

  async loadDetail(foodId) {
    try {
      const res = await http.get(`/api/catfood/${foodId}`);
      if (res) {
        this.setData({
          food: res.food,
          pgcReview: res.pgcReview,
          ugcReviews: res.ugcReviews || []
        });
      }
    } catch (e) {
      console.error(e);
    }
  },

  switchSection(e) {
    this.setData({ activeSection: e.currentTarget.dataset.section });
  },

  goWriteReview() {
    wx.navigateTo({
      url: `/pages/review-write/review-write?foodId=${this.data.foodId}&foodName=${this.data.food.name}`
    });
  },

  onPullDownRefresh() {
    this.loadDetail(this.data.foodId).then(() => wx.stopPullDownRefresh());
  }
});
