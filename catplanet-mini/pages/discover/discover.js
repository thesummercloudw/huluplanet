const http = require('../../utils/request');

Page({
  data: {
    activeTab: 'hospital',
    hospitals: [],
    adoptionCats: [],
    loading: false,
    location: null
  },

  onShow() {
    if (this.data.activeTab === 'hospital') {
      this.getLocation();
    } else {
      this.loadAdoptionCats();
    }
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    if (tab === 'hospital') {
      this.getLocation();
    } else {
      this.loadAdoptionCats();
    }
  },

  getLocation() {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.setData({ location: { lat: res.latitude, lng: res.longitude } });
        this.loadHospitals(res.latitude, res.longitude);
      },
      fail: () => {
        // 默认坐标（北京）
        this.setData({ location: { lat: 39.9042, lng: 116.4074 } });
        this.loadHospitals(39.9042, 116.4074);
      }
    });
  },

  async loadHospitals(lat, lng) {
    this.setData({ loading: true });
    try {
      const hospitals = await http.get(`/api/hospitals?lat=${lat}&lng=${lng}&radius=10`);
      this.setData({ hospitals: hospitals || [] });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  async loadAdoptionCats() {
    this.setData({ loading: true });
    try {
      const cats = await http.get('/api/adoption/cats?page=1&size=20');
      this.setData({ adoptionCats: cats || [] });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  goHospitalDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/hospital-detail/hospital-detail?hospitalId=${id}` });
  },

  goAdoptionDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/adoption-detail/adoption-detail?adoptId=${id}` });
  },

  openNavigation(e) {
    const { lat, lng, name, address } = e.currentTarget.dataset;
    wx.openLocation({
      latitude: parseFloat(lat),
      longitude: parseFloat(lng),
      name: name,
      address: address
    });
  },

  onPullDownRefresh() {
    if (this.data.activeTab === 'hospital' && this.data.location) {
      this.loadHospitals(this.data.location.lat, this.data.location.lng)
        .then(() => wx.stopPullDownRefresh());
    } else {
      this.loadAdoptionCats().then(() => wx.stopPullDownRefresh());
    }
  }
});
