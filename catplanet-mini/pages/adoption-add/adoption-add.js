const http = require('../../utils/request');
const app = getApp();

Page({
  data: {
    images: [],
    form: {
      name: '',
      gender: 'unknown',
      ageEstimate: '',
      breedEstimate: '',
      city: '',
      personality: '',
      reasonForAdoption: '',
      contactMethod: '',
      healthStatus: {
        vaccine: '',
        deworm: '',
        neutered: ''
      }
    },
    submitting: false
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  onHealthInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.healthStatus.${field}`]: e.detail.value });
  },

  setGender(e) {
    const gender = e.currentTarget.dataset.gender;
    this.setData({ 'form.gender': gender });
  },

  chooseImage() {
    const remain = 6 - this.data.images.length;
    wx.chooseMedia({
      count: remain,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newPaths = res.tempFiles.map(f => f.tempFilePath);
        this.setData({ images: [...this.data.images, ...newPaths] });
      }
    });
  },

  removeImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = [...this.data.images];
    images.splice(index, 1);
    this.setData({ images });
  },

  async uploadImages() {
    const urls = [];
    for (const filePath of this.data.images) {
      const res = await http.upload(filePath);
      urls.push(res.url);
    }
    return urls;
  },

  async submit() {
    if (this.data.submitting) return;

    const { form, images } = this.data;

    // 校验必填项
    if (images.length === 0) {
      wx.showToast({ title: '请至少上传一张照片', icon: 'none' });
      return;
    }
    if (!form.name.trim()) {
      wx.showToast({ title: '请填写猫咪名字', icon: 'none' });
      return;
    }
    if (!form.city.trim()) {
      wx.showToast({ title: '请填写所在城市', icon: 'none' });
      return;
    }
    if (!form.contactMethod.trim()) {
      wx.showToast({ title: '请填写联系方式', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });

    try {
      // 上传图片
      wx.showLoading({ title: '上传图片中...' });
      const uploadedUrls = await this.uploadImages();
      wx.hideLoading();

      // 提交领养信息
      const data = {
        name: form.name,
        cover: uploadedUrls[0],
        images: uploadedUrls,
        gender: form.gender,
        ageEstimate: form.ageEstimate || null,
        breedEstimate: form.breedEstimate || null,
        city: form.city,
        personality: form.personality || null,
        reasonForAdoption: form.reasonForAdoption || null,
        contactMethod: form.contactMethod,
        healthStatus: form.healthStatus
      };

      await http.post('/api/adoption/cats', data);
      wx.showToast({ title: '发布成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (e) {
      wx.showToast({ title: e.message || '发布失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
