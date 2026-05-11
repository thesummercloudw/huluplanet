const http = require('../../utils/request');

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

  async loadDetail(hospitalId) {
    try {
      const res = await http.get(`/api/hospitals/${hospitalId}`);
      if (res) {
        this.setData({
          hospital: res.hospital,
          reviews: res.reviews || []
        });
      }
    } catch (e) {
      console.error(e);
    }
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
