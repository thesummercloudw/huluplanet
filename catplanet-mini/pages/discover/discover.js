const http = require('../../utils/request');
const { resolveImage, resolveThumb } = require('../../utils/request');

Page({
  data: {
    placeType: '',             // '' = 全部, 'hospital' = 宠物医院, 'petstore' = 宠物店
    hospitals: [],
    adoptionCats: [],
    loading: false,
    location: null,
    locationCity: ''           // 用户当前城市
  },

  onShow() {
    this.getLocation();
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
        this.reverseGeocode(res.latitude, res.longitude);
      },
      fail: () => {
        // 默认坐标（北京）
        this.setData({ location: { lat: 39.9042, lng: 116.4074 }, locationCity: '' });
        this.loadHospitals(39.9042, 116.4074);
        this.loadAdoptionCats();
      }
    });
  },

  /** 通过后端接口获取用户所在城市 */
  async reverseGeocode(lat, lng) {
    try {
      const result = await http.get(`/api/public/geocode/city?lat=${lat}&lng=${lng}`);
      const city = (result && result.city) || '';
      this.setData({ locationCity: city });
      // 带城市筛选加载领养数据
      this.loadAdoptionCats(city);
    } catch (e) {
      // 降级：不筛选城市，展示全部
      this.loadAdoptionCats();
    }
  },

  async loadHospitals(lat, lng) {
    this.setData({ loading: true });
    try {
      let url = `/api/hospitals?lat=${lat}&lng=${lng}&radius=10`;
      if (this.data.placeType) {
        url += `&type=${this.data.placeType}`;
      }
      const hospitals = await http.get(url);
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

  goHospitalDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/hospital-detail/hospital-detail?hospitalId=${id}` });
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
    const city = this.data.locationCity;
    const promises = [this.loadAdoptionCats(city)];
    if (this.data.location) {
      promises.push(this.loadHospitals(this.data.location.lat, this.data.location.lng));
    }
    Promise.all(promises).then(() => wx.stopPullDownRefresh());
  },

  async loadAdoptionCats(city) {
    try {
      let url = '/api/adoption/cats?page=1&size=10';
      if (city) {
        url += `&city=${encodeURIComponent(city)}`;
      }
      const adoptionCats = await http.get(url);
      this.setData({
        adoptionCats: (adoptionCats || []).map(c => ({ ...c, cover: resolveThumb(c.cover, 200) }))
      });
    } catch (e) {
      console.error('loadAdoptionCats error:', e);
    }
  },

  goAdoptionAdd() {
    wx.navigateTo({ url: '/pages/adoption-add/adoption-add' });
  },

  goAdoptionDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/adoption-detail/adoption-detail?adoptId=${id}` });
  }
});
