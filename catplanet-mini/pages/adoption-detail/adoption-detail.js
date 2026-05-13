const http = require('../../utils/request');
const { resolveImage } = require('../../utils/request');

Page({
  data: {
    adoptId: null,
    cat: null,
    showApplyForm: false,
    form: {
      selfIntro: '',
      experience: '',
      familyEnv: '',
      commitmentSigned: 0
    }
  },

  onLoad(options) {
    if (options.adoptId) {
      this.setData({ adoptId: options.adoptId });
      this.loadDetail(options.adoptId);
    }
  },

  async loadDetail(adoptId) {
    try {
      const cat = await http.get(`/api/adoption/cats/${adoptId}`);
      cat.cover = resolveImage(cat.cover);
      if (cat.images && Array.isArray(cat.images)) {
        cat.images = cat.images.map(img => resolveImage(img));
      }
      this.setData({ cat });
    } catch (e) {
      console.error(e);
    }
  },

  showApply() {
    this.setData({ showApplyForm: true });
  },

  hideApply() {
    this.setData({ showApplyForm: false });
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  toggleCommitment() {
    this.setData({ 'form.commitmentSigned': this.data.form.commitmentSigned ? 0 : 1 });
  },

  async submitApply() {
    const { form, adoptId } = this.data;
    if (!form.commitmentSigned) {
      wx.showToast({ title: '请先签署养宠承诺', icon: 'none' });
      return;
    }
    if (!form.selfIntro.trim()) {
      wx.showToast({ title: '请填写自我介绍', icon: 'none' });
      return;
    }

    try {
      await http.post('/api/adoption/apply', {
        adoptId,
        selfIntro: form.selfIntro,
        experience: form.experience,
        familyEnv: form.familyEnv,
        commitmentSigned: form.commitmentSigned
      });
      wx.showToast({ title: '申请已提交', icon: 'success' });
      setTimeout(() => {
        this.setData({ showApplyForm: false });
      }, 1500);
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败', icon: 'none' });
    }
  }
});
