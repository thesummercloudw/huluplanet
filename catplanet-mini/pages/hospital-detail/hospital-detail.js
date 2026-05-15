const http = require('../../utils/request');
const { resolveImage } = require('../../utils/request');

Page({
  data: {
    hospitalId: null,
    hospital: null,
    reviews: []
  },

  onLoad(options) {
    if (options.hospitalId) {
      this.setData({ hospitalId: options.hospitalId });
      this.loadDetail(options.hospitalId);
    }
  },

  onShow() {
    if (this.data.hospitalId) {
      this.loadDetail(this.data.hospitalId);
    }
  },

  async loadDetail(hospitalId) {
    try {
      const res = await http.get(`/api/hospitals/${hospitalId}`);
      if (res) {
        this.setData({
          hospital: res.hospital,
          reviews: (res.reviews || []).map(r => ({
            ...r,
            avatar: r.avatar ? resolveImage(r.avatar) : ''
          }))
        });
      }
    } catch (e) {
      console.error(e);
    }
  },

  goWriteReview() {
    const name = encodeURIComponent(this.data.hospital.name || '');
    wx.navigateTo({
      url: `/pages/hospital-review-write/hospital-review-write?hospitalId=${this.data.hospitalId}&name=${name}`
    });
  },

  openNavigation() {
    const { lat, lng, name, address } = this.data.hospital;
    wx.openLocation({
      latitude: parseFloat(lat),
      longitude: parseFloat(lng),
      name,
      address
    });
  },

  makeCall() {
    if (this.data.hospital.phone) {
      wx.makePhoneCall({ phoneNumber: this.data.hospital.phone });
    }
  }
});
