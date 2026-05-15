const http = require('../../utils/request');
const app = getApp();

// 记忆存储的 key
const MEMORY_KEY = 'adoption_form_memory';

Page({
  data: {
    images: [],
    form: {
      name: '',
      gender: 'unknown',
      ageEstimate: '',
      breedEstimate: '',
      city: '',
      province: '',
      district: '',
      personality: '',
      reasonForAdoption: '',
      contactMethod: '',
      healthStatus: {
        vaccine: '',
        deworm: '',
        neutered: ''
      }
    },
    regionValue: [],  // picker的值 ['省','市','区']
    regionText: '',   // 显示文本
    submitting: false,
    // 下拉选项
    ageOptions: ['未满1月龄', '约1-3月龄', '约3-6月龄', '约6-12月龄', '约1-2岁', '约2-5岁', '5岁以上', '不确定'],
    breedOptions: ['橘猫', '狸花猫', '黑猫', '白猫', '奶牛猫', '三花猫', '玳瑁猫', '英短蓝猫', '英短银渐层', '美短', '布偶猫', '暹罗猫', '加菲猫', '田园猫(其他)'],
    vaccineOptions: ['已打3针', '已打2针', '已打1针', '未打', '不确定'],
    dewormOptions: ['已做(体内外)', '已做(体内)', '已做(体外)', '未做', '不确定'],
    neuteredOptions: ['已绝育', '未绝育', '不确定'],
    // 下拉选中索引
    ageIndex: -1,
    breedIndex: -1,
    vaccineIndex: -1,
    dewormIndex: -1,
    neuteredIndex: -1,
    // 记忆提示
    hasMemory: false
  },

  onLoad() {
    this.loadMemory();
  },

  /** 从本地存储加载记忆数据 */
  loadMemory() {
    try {
      const memory = wx.getStorageSync(MEMORY_KEY);
      if (memory) {
        const updates = { hasMemory: true };
        if (memory.regionValue && memory.regionValue.length === 3) {
          updates.regionValue = memory.regionValue;
          updates.regionText = memory.regionValue.join(' ');
          updates['form.province'] = memory.regionValue[0];
          updates['form.city'] = memory.regionValue[1];
          updates['form.district'] = memory.regionValue[2];
        }
        if (memory.contactMethod) updates['form.contactMethod'] = memory.contactMethod;
        this.setData(updates);
      }
    } catch (e) {
      console.warn('读取表单记忆失败', e);
    }
  },

  /** 保存记忆到本地存储 */
  saveMemory() {
    try {
      const { contactMethod } = this.data.form;
      const { regionValue } = this.data;
      wx.setStorageSync(MEMORY_KEY, { regionValue, contactMethod });
    } catch (e) {
      console.warn('保存表单记忆失败', e);
    }
  },

  /** 用户点击"使用当前定位" —— 调用微信原生地图选点，无需第三方Key */
  getLocation() {
    wx.chooseLocation({
      success: (res) => {
        if (res.address) {
          // 从地址字符串解析省市区
          const parsed = this.parseAddress(res.address);
          if (parsed.province) {
            const regionValue = [parsed.province, parsed.city, parsed.district];
            this.setData({
              regionValue,
              regionText: regionValue.filter(v => v).join(' '),
              'form.province': parsed.province,
              'form.city': parsed.city,
              'form.district': parsed.district
            });
          } else {
            wx.showToast({ title: '无法识别地址，请手动选择', icon: 'none' });
          }
        }
      },
      fail: () => {
        wx.showToast({ title: '定位取消', icon: 'none' });
      }
    });
  },
  
  /** 从地址字符串解析省市区 */
  parseAddress(address) {
    let province = '', city = '', district = '';
    // 匹配模式: XX省/自治区/市 + XX市/自治州/地区/盟 + XX区/县/市/旗
    const provinceReg = /^(.+?(?:省|自治区|特别行政区))|^(北京|天津|上海|重庆)/;
    const cityReg = /(?:省|自治区|特别行政区|^北京|^天津|^上海|^重庆)(.+?(?:市|自治州|地区|盟))/;
    const districtReg = /(?:市|自治州|地区|盟)(.+?(?:区|县|市|旗))/;
  
    const pMatch = address.match(provinceReg);
    if (pMatch) {
      province = (pMatch[1] || pMatch[2] || '').trim();
      // 直辖市特殊处理
      if (['北京', '天津', '上海', '重庆'].includes(province)) {
        province = province + '市';
        city = province;
        const dMatch = address.match(/^(?:北京|天津|上海|重庆)市?(.+?(?:区|县))/);
        if (dMatch) district = dMatch[1];
        return { province, city, district };
      }
    }
  
    const cMatch = address.match(cityReg);
    if (cMatch) city = (cMatch[1] || '').trim();
  
    const dMatch = address.match(districtReg);
    if (dMatch) district = (dMatch[1] || '').trim();
  
    return { province, city, district };
  },

  /** 省市区选择器变更 */
  onRegionChange(e) {
    const value = e.detail.value; // ["江苏省", "南京市", "鼓楼区"]
    this.setData({
      regionValue: value,
      regionText: value.join(' '),
      'form.province': value[0],
      'form.city': value[1],
      'form.district': value[2]
    });
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  // 下拉选择器事件
  onAgePick(e) {
    const idx = e.detail.value;
    this.setData({
      ageIndex: idx,
      'form.ageEstimate': this.data.ageOptions[idx]
    });
  },

  onBreedPick(e) {
    const idx = e.detail.value;
    this.setData({
      breedIndex: idx,
      'form.breedEstimate': this.data.breedOptions[idx]
    });
  },

  onVaccinePick(e) {
    const idx = e.detail.value;
    this.setData({
      vaccineIndex: idx,
      'form.healthStatus.vaccine': this.data.vaccineOptions[idx]
    });
  },

  onDewormPick(e) {
    const idx = e.detail.value;
    this.setData({
      dewormIndex: idx,
      'form.healthStatus.deworm': this.data.dewormOptions[idx]
    });
  },

  onNeuteredPick(e) {
    const idx = e.detail.value;
    this.setData({
      neuteredIndex: idx,
      'form.healthStatus.neutered': this.data.neuteredOptions[idx]
    });
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
    if (!this.data.regionText) {
      wx.showToast({ title: '请选择所在地区', icon: 'none' });
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
        province: form.province || null,
        city: form.city,
        district: form.district || null,
        personality: form.personality || null,
        reasonForAdoption: form.reasonForAdoption || null,
        contactMethod: form.contactMethod,
        healthStatus: form.healthStatus
      };

      await http.post('/api/adoption/cats', data);
      // 发布成功后保存记忆
      this.saveMemory();
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
