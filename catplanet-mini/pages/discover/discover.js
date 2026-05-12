const http = require('../../utils/request');

Page({
  data: {
    activeTab: 'nearby',       // nearby / adoption
    placeType: '',             // '' = 全部, 'hospital' = 宠物医院, 'petstore' = 宠物店
    hospitals: [],
    adoptionCats: [],
    loading: false,
    location: null
  },

  onShow() {
    if (this.data.activeTab === 'nearby') {
      this.getLocation();
    } else {
      this.loadAdoptionCats();
    }
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    if (tab === 'nearby') {
      this.getLocation();
    } else {
      this.loadAdoptionCats();
    }
  },

  switchPlaceType(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ placeType: type });
    if (this.data.location) {
      this.loadHospitals(this.data.location.lat, this.data.location.lng);
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
      let url = `/api/hospitals?lat=${lat}&lng=${lng}&radius=10`;
      if (this.data.placeType) {
        url += `&type=${this.data.placeType}`;
      }
      const hospitals = await http.get(url);
      // 计算距离并排序
      const list = (hospitals || []).map(item => {
        const dist = this.calcDistance(lat, lng, item.lat, item.lng);
        return { ...item, distance: dist, distanceText: this.formatDistance(dist) };
      });
      list.sort((a, b) => a.distance - b.distance);
      this.setData({ hospitals: list });
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  },

  // Haversine 公式计算两点距离 (km)
  calcDistance(lat1, lng1, lat2, lng2) {
    const R = 6371;
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLng / 2) * Math.sin(dLng / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  },

  formatDistance(km) {
    if (km < 1) {
      return Math.round(km * 1000) + 'm';
    }
    return km.toFixed(1) + 'km';
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
    if (this.data.activeTab === 'nearby' && this.data.location) {
      this.loadHospitals(this.data.location.lat, this.data.location.lng)
        .then(() => wx.stopPullDownRefresh());
    } else {
      this.loadAdoptionCats().then(() => wx.stopPullDownRefresh());
    }
  }
});
