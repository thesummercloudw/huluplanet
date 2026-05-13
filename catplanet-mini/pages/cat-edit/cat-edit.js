const http = require('../../utils/request');
const { resolveImage } = require('../../utils/request');

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
    loading: false,
    showFireworks: false
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
        avatarTemp: resolveImage(cat.avatar) || '',
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
      this.setData({
        'form.avatar': data.url,
        avatarTemp: resolveImage(data.url)
      });
      wx.showToast({ title: '头像已上传', icon: 'success' });
      // 上传成功后自动识别品种（仅当品种字段为空时）
      if (!this.data.form.breed) {
        this.recognizeBreed(data.url);
      }
    } catch (e) {
      console.error('upload avatar error', e);
      this.setData({ avatarTemp: this.data.form.avatar ? resolveImage(this.data.form.avatar) : '' });
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

  // 🎆 Canvas 烟花引擎
  startFireworks() {
    const query = wx.createSelectorQuery();
    query.select('#fireworksCanvas')
      .fields({ node: true, size: true })
      .exec((res) => {
        if (!res[0]) return;
        const canvas = res[0].node;
        const ctx = canvas.getContext('2d');
        const info = wx.getWindowInfo();
        const dpr = info.pixelRatio;
        const width = info.windowWidth;
        const height = info.windowHeight;
        canvas.width = width * dpr;
        canvas.height = height * dpr;
        ctx.scale(dpr, dpr);

        // 绚烂的霓虹色彩
        const COLORS = [
          '#FF1744', '#FF4081', '#F50057', '#FF6090',
          '#D500F9', '#E040FB', '#AA00FF', '#CE93D8',
          '#651FFF', '#7C4DFF', '#448AFF', '#40C4FF',
          '#00E5FF', '#18FFFF', '#00E676', '#69F0AE',
          '#76FF03', '#C6FF00', '#FFEA00', '#FFD600',
          '#FFC400', '#FF9100', '#FF6D00', '#FF3D00',
        ];

        const particles = [];
        let launchCount = 0;
        const maxLaunches = 30;
        let animId = null;

        // 创建一朵烟花爆炸
        function createBurst(x, y) {
          const count = 50 + Math.floor(Math.random() * 30);
          const palette = [
            COLORS[Math.floor(Math.random() * COLORS.length)],
            COLORS[Math.floor(Math.random() * COLORS.length)],
            COLORS[Math.floor(Math.random() * COLORS.length)],
          ];
          for (let i = 0; i < count; i++) {
            const angle = (Math.PI * 2 / count) * i + (Math.random() - 0.5) * 0.4;
            const speed = 1.5 + Math.random() * 5;
            particles.push({
              x, y,
              vx: Math.cos(angle) * speed,
              vy: Math.sin(angle) * speed,
              life: 1,
              decay: 0.006 + Math.random() * 0.009,
              color: palette[Math.floor(Math.random() * palette.length)],
              size: 1.5 + Math.random() * 3,
              trail: [],
            });
          }
          // 中心闪光白色火花
          for (let i = 0; i < 15; i++) {
            const angle = Math.random() * Math.PI * 2;
            const speed = 0.5 + Math.random() * 7;
            particles.push({
              x, y,
              vx: Math.cos(angle) * speed,
              vy: Math.sin(angle) * speed,
              life: 1,
              decay: 0.02 + Math.random() * 0.02,
              color: '#FFFFFF',
              size: 1 + Math.random() * 1.5,
              trail: [],
            });
          }
        }

        // 首波：同时绽放 4 朵
        createBurst(width * 0.2, height * 0.2);
        createBurst(width * 0.8, height * 0.15);
        createBurst(width * 0.5, height * 0.35);
        createBurst(width * 0.35, height * 0.55);
        launchCount = 4;

        // 持续发射
        function scheduleLaunch() {
          if (launchCount >= maxLaunches) return;
          const x = Math.random() * width * 0.85 + width * 0.075;
          const y = Math.random() * height * 0.7 + height * 0.05;
          createBurst(x, y);
          launchCount++;
          if (launchCount < maxLaunches) {
            setTimeout(scheduleLaunch, 120 + Math.random() * 250);
          }
        }
        setTimeout(scheduleLaunch, 300);

        // 动画循环
        function animate() {
          // 半透明黑色覆盖产生拖尾
          ctx.globalCompositeOperation = 'source-over';
          ctx.fillStyle = 'rgba(0, 0, 0, 0.18)';
          ctx.fillRect(0, 0, width, height);
          ctx.globalCompositeOperation = 'lighter';

          for (let i = particles.length - 1; i >= 0; i--) {
            const p = particles[i];
            // 记录轨迹
            p.trail.push({ x: p.x, y: p.y });
            if (p.trail.length > 6) p.trail.shift();
            // 物理更新
            p.x += p.vx;
            p.y += p.vy;
            p.vy += 0.035;
            p.vx *= 0.985;
            p.life -= p.decay;

            if (p.life <= 0) {
              particles.splice(i, 1);
              continue;
            }

            // 绘制拖尾
            for (let t = 0; t < p.trail.length; t++) {
              const alpha = (t / p.trail.length) * p.life * 0.4;
              ctx.globalAlpha = alpha;
              ctx.beginPath();
              ctx.arc(p.trail[t].x, p.trail[t].y, p.size * 0.6, 0, Math.PI * 2);
              ctx.fillStyle = p.color;
              ctx.fill();
            }

            // 绘制粒子本体 + 光晕
            ctx.globalAlpha = p.life;
            ctx.beginPath();
            ctx.arc(p.x, p.y, p.size * p.life, 0, Math.PI * 2);
            ctx.fillStyle = p.color;
            ctx.fill();

            // 外层光晕
            ctx.globalAlpha = p.life * 0.3;
            ctx.beginPath();
            ctx.arc(p.x, p.y, p.size * p.life * 3, 0, Math.PI * 2);
            ctx.fillStyle = p.color;
            ctx.fill();
          }

          ctx.globalAlpha = 1;
          ctx.globalCompositeOperation = 'source-over';

          if (particles.length > 0 || launchCount < maxLaunches) {
            animId = canvas.requestAnimationFrame(animate);
          }
        }

        animate();
      });
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

      // 🎆 彩蛋：名字包含"警花"时绽放烟火
      if (form.name.includes('警花')) {
        this.setData({ showFireworks: true });
        setTimeout(() => this.startFireworks(), 100);
        setTimeout(() => {
          this.setData({ showFireworks: false });
          wx.navigateBack();
        }, 6000);
      } else {
        setTimeout(() => wx.navigateBack(), 1000);
      }
    } catch (e) {
      console.error(e);
    } finally {
      this.setData({ loading: false });
    }
  }
});
