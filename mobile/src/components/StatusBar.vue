<template>
  <view class="status">
    <text class="status-time num">{{ time }}</text>
    <view class="status-icons">
      <image class="si" :src="signalIcon" />
      <image class="si" :src="wifiIcon" />
      <image class="si si-battery" :src="batteryIcon" />
    </view>
  </view>
</template>

<script>
export default {
  name: 'StatusBar',
  data() {
    return {
      time: '09:41'
    }
  },
  computed: {
    signalIcon() {
      return this.svg(
        '<rect x="1" y="11" width="3" height="5" rx="1" fill="#1f2c3d"/>' +
          '<rect x="6" y="8" width="3" height="8" rx="1" fill="#1f2c3d"/>' +
          '<rect x="11" y="5" width="3" height="11" rx="1" fill="#1f2c3d"/>' +
          '<rect x="16" y="2" width="3" height="14" rx="1" fill="#1f2c3d"/>',
        22,
        20
      )
    },
    wifiIcon() {
      return this.svg(
        '<path d="M2.5 7.5a10.5 10.5 0 0 1 15 0" stroke="#1f2c3d" stroke-width="1.7" stroke-linecap="round" fill="none"/>' +
          '<path d="M6 11a5.5 5.5 0 0 1 8 0" stroke="#1f2c3d" stroke-width="1.7" stroke-linecap="round" fill="none"/>' +
          '<circle cx="10" cy="15" r="1.4" fill="#1f2c3d"/>',
        20,
        20
      )
    },
    batteryIcon() {
      return this.svg(
        '<rect x="1" y="5" width="20" height="11" rx="3" stroke="#1f2c3d" stroke-opacity="0.32" stroke-width="1.3" fill="none"/>' +
          '<rect x="3" y="7" width="13" height="7" rx="1.6" fill="#1f2c3d"/>' +
          '<path d="M23 8.8v3.4a2 2 0 0 0 0-3.4Z" fill="#1f2c3d" fill-opacity="0.4"/>',
        25,
        20
      )
    }
  },
  mounted() {
    this.tick()
    this.timer = setInterval(this.tick, 20000)
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer)
  },
  methods: {
    svg(inner, w, h) {
      const s = `<svg width="${w}" height="${h}" viewBox="0 0 ${w} ${h}" fill="none" xmlns="http://www.w3.org/2000/svg">${inner}</svg>`
      // #ifdef H5
      return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(s)
      // #endif
      // #ifndef H5
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(s)))
      // #endif
    },
    tick() {
      const d = new Date()
      const h = String(d.getHours()).padStart(2, '0')
      const m = String(d.getMinutes()).padStart(2, '0')
      this.time = `${h}:${m}`
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.status {
  height: $status-bar-height;
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  justify-content: space-between;
  padding: 0 $page-padding 10rpx $page-padding;
  box-sizing: border-box;
}

.status-time {
  font-size: 30rpx;
  font-weight: 600;
  color: $color-text;
}

.status-icons {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8rpx;
}

.si {
  width: 40rpx;
  height: 26rpx;
}

.si-battery {
  width: 46rpx;
}
</style>
