const http = require('../../utils/request');

Page({
  data: {
    catId: null,
    isEdit: false,
    form: {
      name: '',
      avatar: '',
      breed: '',
      gender: 'unknown',
      birthday: '',
      weightKg: '',
      isNeutered: 0,
      adoptionDate: ''
    },
    avatarTemp: '',
    uploading: false,
    genderOptions: ['未知', '弟弟(公)', '妹妹(母)'],
    genderValues: ['unknown', 'male', 'female'],
    genderIndex: 0,
    loading: false
  },

  onLoad(options) {
    if (options.catId) {
      this.setData({ catId: options.catId, isEdit: true });
      this.loadCat(options.catId);
    }
  },

  async loadCat(catId) {
    try {
      const cat = await http.get(`/api/cats/${catId}`);
      const genderIndex = this.data.genderValues.indexOf(cat.gender || 'unknown');
      this.setData({
        form: {
          name: cat.name || '',
          avatar: cat.avatar || '',
          breed: cat.breed || '',
          gender: cat.gender || 'unknown',
          birthday: cat.birthday || '',
          weightKg: cat.weightKg ? String(cat.weightKg) : '',
          isNeutered: cat.isNeutered || 0,
          adoptionDate: cat.adoptionDate || ''
        },
        avatarTemp: cat.avatar || '',
        genderIndex: genderIndex >= 0 ? genderIndex : 0
      });
    } catch (e) {
      console.error(e);
    }
  },

  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      sizeType: ['compressed'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        this.setData({ avatarTemp: tempFilePath });
        this.uploadAvatar(tempFilePath);
      }
    });
  },

  async uploadAvatar(filePath) {
    this.setData({ uploading: true });
    try {
      const data = await http.upload(filePath);
      this.setData({ 'form.avatar': data.url });
      wx.showToast({ title: '头像已上传', icon: 'success' });
    } catch (e) {
      console.error('upload avatar error', e);
      this.setData({ avatarTemp: this.data.form.avatar || '' });
    } finally {
      this.setData({ uploading: false });
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  onGenderChange(e) {
    const index = e.detail.value;
    this.setData({
      genderIndex: index,
      'form.gender': this.data.genderValues[index]
    });
  },

  onBirthdayChange(e) {
    this.setData({ 'form.birthday': e.detail.value });
  },

  onAdoptionDateChange(e) {
    this.setData({ 'form.adoptionDate': e.detail.value });
  },

  toggleNeutered() {
    this.setData({ 'form.isNeutered': this.data.form.isNeutered ? 0 : 1 });
  },

  async handleSubmit() {
    const { form, catId, isEdit, uploading } = this.data;
    if (!form.name.trim()) {
      wx.showToast({ title: '请输入猫咪名字', icon: 'none' });
      return;
    }
    if (uploading) {
      wx.showToast({ title: '图片正在上传中', icon: 'none' });
      return;
    }

    const payload = {
      name: form.name,
      avatar: form.avatar || null,
      breed: form.breed || null,
      gender: form.gender,
      birthday: form.birthday || null,
      weightKg: form.weightKg ? parseFloat(form.weightKg) : null,
      isNeutered: form.isNeutered,
      adoptionDate: form.adoptionDate || null
    };

    this.setData({ loading: true });
    try {
      if (isEdit) {
        await http.put(`/api/cats/${catId}`, payload);
        wx.showToast({ title: '已更新', icon: 'success' });
      } else {
        await http.post('/api/cats', payload);
        wx.showToast({ title: '添加成功', icon: 'success' });
      }
      setTimeout(() => wx.navigateBack(), 1000);
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  }
});
