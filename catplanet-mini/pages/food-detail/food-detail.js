const http = require('../../utils/request');
const { resolveImage } = require('../../utils/request');

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
        const food = res.food;
        if (food) food.image = resolveImage(food.image);
        const pgcReview = res.pgcReview;
        if (pgcReview) pgcReview.cover = resolveImage(pgcReview.cover);
        this.setData({
          food,
          pgcReview,
          ugcReviews: (res.ugcReviews || []).map(r => ({
            ...r,
            images: r.images ? r.images.map(img => resolveImage(img)) : []
          }))
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
