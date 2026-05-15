const http = require('../../utils/request');
const { resolveImage } = require('../../utils/request');
const app = getApp();

Page({
  data: {
    sightingId: null,
    sighting: null,
    comments: [],
    loading: false,
    inputContent: '',
    selectedImages: [],
    canSend: false,
    sending: false,
    publisherNickname: '',
    publisherAvatar: ''
  },

  onLoad(options) {
    if (options.sightingId) {
      this.setData({ sightingId: options.sightingId });
      this.loadDetail(options.sightingId);
    }
  },

  async loadDetail(sightingId) {
    this.setData({ loading: true });
    try {
      const res = await http.get(`/api/sightings/${sightingId}`);
      if (res) {
        const sighting = res.sighting;
        sighting.timeStr = this.formatTime(sighting.createdAt);

        const userId = app.globalData.userId;
        const comments = (res.comments || []).map(c => ({
          ...c,
          timeStr: this.formatTime(c.createdAt),
          isMine: String(c.userId) === String(userId)
        }));

        this.setData({
          sighting,
          comments,
          publisherNickname: res.publisherNickname || '',
          publisherAvatar: res.publisherAvatar || ''
        });
      }
    } catch (e) {
      console.error('load detail error:', e);
    } finally {
      this.setData({ loading: false });
    }
  },

  onCommentInput(e) {
    const inputContent = e.detail.value;
    this.setData({
      inputContent,
      canSend: inputContent.trim().length > 0
    });
  },

  chooseCommentImages() {
    const remaining = 3 - this.data.selectedImages.length;
    if (remaining <= 0) {
      wx.showToast({ title: '最多选择3张图片', icon: 'none' });
      return;
    }
    wx.chooseMedia({
      count: remaining,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newImages = res.tempFiles.map(f => ({
          tempUrl: f.tempFilePath,
          uploaded: false,
          serverPath: ''
        }));
        this.setData({
          selectedImages: [...this.data.selectedImages, ...newImages]
        });
      }
    });
  },

  removeImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = [...this.data.selectedImages];
    images.splice(index, 1);
    this.setData({ selectedImages: images });
  },

  async sendComment() {
    const { inputContent, selectedImages, sightingId, canSend, sending } = this.data;
    if (!canSend || sending) return;
    if (inputContent.length > 500) {
      wx.showToast({ title: '评论不能超过500字', icon: 'none' });
      return;
    }

    this.setData({ sending: true });
    wx.showLoading({ title: '发送中...' });

    try {
      // 先上传图片
      const uploadedPaths = [];
      for (const img of selectedImages) {
        const data = await http.upload(img.tempUrl);
        uploadedPaths.push(data.url);
      }

      // 提交评论
      await http.post('/api/sightings/comments', {
        sightingId: sightingId,
        content: inputContent,
        images: uploadedPaths.length > 0 ? uploadedPaths : null
      });

      wx.showToast({ title: '评论成功', icon: 'success' });
      this.setData({ inputContent: '', selectedImages: [], canSend: false });

      // 重新加载数据
      this.loadDetail(sightingId);
    } catch (e) {
      console.error('send comment error:', e);
    } finally {
      this.setData({ sending: false });
      wx.hideLoading();
    }
  },

  async deleteComment(e) {
    const commentId = e.currentTarget.dataset.id;
    const res = await wx.showModal({ title: '提示', content: '确定删除此评论？' });
    if (!res.confirm) return;

    try {
      await http.del(`/api/sightings/comments/${commentId}`);
      wx.showToast({ title: '已删除', icon: 'success' });
      this.loadDetail(this.data.sightingId);
    } catch (e) {
      console.error('delete comment error:', e);
    }
  },

  previewImage() {
    if (this.data.sighting && this.data.sighting.image) {
      wx.previewImage({
        current: this.data.sighting.image,
        urls: [this.data.sighting.image]
      });
    }
  },

  previewCommentImage(e) {
    const { urls, current } = e.currentTarget.dataset;
    wx.previewImage({ current, urls });
  },

  formatTime(timeStr) {
    if (!timeStr) return '';
    const date = new Date(timeStr);
    const now = new Date();
    const diffMs = now - date;
    const diffMin = Math.floor(diffMs / 60000);
    if (diffMin < 1) return '刚刚';
    if (diffMin < 60) return diffMin + '分钟前';
    const diffHour = Math.floor(diffMin / 60);
    if (diffHour < 24) return diffHour + '小时前';
    const diffDay = Math.floor(diffHour / 24);
    if (diffDay < 7) return diffDay + '天前';
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return month + '/' + day;
  },

  onPullDownRefresh() {
    this.loadDetail(this.data.sightingId).then(() => wx.stopPullDownRefresh());
  }
});
