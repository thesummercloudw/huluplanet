const http = require('../../utils/request');

Page({
  data: {
    imageUrl: '',
    content: '',
    lat: null,
    lng: null,
    address: '',
    canSubmit: false,
    submitting: false
  },

  onLoad() {
    this.chooseLocation();
  },

  // 选择位置（打开地图选点，返回可读地址）
  chooseLocation() {
    wx.chooseLocation({
      success: (res) => {
        this.setData({
          lat: res.latitude,
          lng: res.longitude,
          address: res.name || res.address || '未知地点'
        });
        this.checkCanSubmit();
      },
      fail: () => {
        if (!this.data.lat) {
          wx.showToast({ title: '请点击地点选择位置', icon: 'none' });
        }
      }
    });
  },

  // 选择/拍摄图片
  chooseImage() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      camera: 'back',
      success: async (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        wx.showLoading({ title: '上传中...' });
        try {
          const data = await http.upload(tempFilePath);
          this.setData({ imageUrl: data.url });
          this.checkCanSubmit();
        } catch (e) {
          console.error('upload error:', e);
        } finally {
          wx.hideLoading();
        }
      }
    });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  checkCanSubmit() {
    const { imageUrl, lat } = this.data;
    this.setData({ canSubmit: !!imageUrl && lat !== null });
  },

  async submit() {
    const { imageUrl, content, lat, lng, address, canSubmit, submitting } = this.data;
    if (!canSubmit || submitting) return;

    this.setData({ submitting: true });
    wx.showLoading({ title: '发布中...' });

    try {
      await http.post('/api/sightings', {
        image: imageUrl,
        content: content,
        lat: lat,
        lng: lng,
        address: address
      });
      wx.showToast({ title: '发布成功！', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (e) {
      console.error('submit error:', e);
    } finally {
      this.setData({ submitting: false });
      wx.hideLoading();
    }
  }
});
