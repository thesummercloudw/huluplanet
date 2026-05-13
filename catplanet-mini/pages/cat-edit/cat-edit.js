const http = require('../../utils/request');

const breedOptions = [
  '未知', '中华田园猫', '英国短毛猫', '美国短毛猫', '布偶猫', '暹罗猫',
  '波斯猫', '缅因猫', '苏格兰折耳猫', '俄罗斯蓝猫', '孟买猫',
  '阿比西尼亚猫', '索马里猫', '挪威森林猫', '西伯利亚猫', '土耳其安哥拉猫',
  '伯曼猫', '巴厘猫', '东方短毛猫', '埃及猫', '奥西猫',
  '日本短尾猫', '加拿大无毛猫', '德文卷毛猫', '柯尼斯卷毛猫', '塞尔凯克卷毛猫',
  '曼基康猫', '英国长毛猫', '异国短毛猫', '金吉拉猫', '喜马拉雅猫',
  '拉格多尔猫', '美国卷耳猫', '孟加拉猫', '萨凡纳猫', '豹猫',
  '狸花猫', '橘猫', '奶牛猫', '三花猫', '玳瑁猫',
  '蓝猫', '蓝白猫', '银渐层', '金渐层', '缅甸猫',
  '夏特尔猫', '哈瓦那棕猫', '科拉特猫', '新加坡猫', '山东狮子猫',
  '临清狮子猫', '玄猫', '白猫', '其他'
];

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
    recognizing: false,
    breedOptions: breedOptions,
    breedIndex: 0,
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
      const breedIndex = breedOptions.indexOf(cat.breed || '未知');
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
        genderIndex: genderIndex >= 0 ? genderIndex : 0,
        breedIndex: breedIndex >= 0 ? breedIndex : 0
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
      // 上传成功后自动识别品种（仅当品种字段为空时）
      if (!this.data.form.breed) {
        this.recognizeBreed(data.url);
      }
    } catch (e) {
      console.error('upload avatar error', e);
      this.setData({ avatarTemp: this.data.form.avatar || '' });
    } finally {
      this.setData({ uploading: false });
    }
  },

  async recognizeBreed(imageUrl) {
    this.setData({ recognizing: true });
    try {
      const data = await http.post('/api/breed-recognition', { imageUrl });
      if (data.breed) {
        const breedIndex = breedOptions.indexOf(data.breed);
        this.setData({
          'form.breed': data.breed,
          breedIndex: breedIndex >= 0 ? breedIndex : 0
        });
        wx.showToast({ title: `识别为: ${data.breed}`, icon: 'none', duration: 2000 });
      }
    } catch (e) {
      console.error('breed recognition error', e);
      // 识别失败不影响使用，静默处理
    } finally {
      this.setData({ recognizing: false });
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  onBreedChange(e) {
    const index = e.detail.value;
    this.setData({
      breedIndex: index,
      'form.breed': breedOptions[index] === '未知' ? '' : breedOptions[index]
    });
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
