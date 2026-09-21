<template>
  <view class="tabbar">
    <view
      v-for="(item, idx) in items"
      :key="idx"
      class="tab"
      :class="{ active: idx === current }"
      @tap="go(idx, item)"
    >
      <image class="tab-icon" :src="iconOf(item, idx)" />
      <text class="tab-text">{{ item.text }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'AppTabBar',
  props: {
    items: {
      type: Array,
      default: () => [
        { text: '工作台', icon: 'grid' },
        { text: '挂号', icon: 'ticket' },
        { text: '收费', icon: 'cash' },
        { text: '我的', icon: 'user' }
      ]
    },
    current: { type: Number, default: 0 }
  },
  methods: {
    iconOf(item, idx) {
      const active = idx === this.current
      const color = active ? '#007a3f' : '#8a978e'
      const name = item.icon || 'grid'
      const base = {
        grid:
          '<rect x="3.5" y="3.5" width="7" height="7" rx="1.6"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.6"/><rect x="3.5" y="13.5" width="7" height="7" rx="1.6"/><rect x="13.5" y="13.5" width="7" height="7" rx="1.6"/>',
        ticket: '<path d="M3.5 8.5V5.5h17v3a2.5 2.5 0 0 0 0 5v3h-17v-3a2.5 2.5 0 0 0 0-5Z"/>',
        cash: '<rect x="3" y="6.5" width="18" height="11" rx="2"/><path d="M3 10.5h18"/><circle cx="12" cy="14" r="1.5"/>',
        user: '<circle cx="12" cy="8.5" r="3.5"/><path d="M5.5 19.5c0-3.3 2.9-5.5 6.5-5.5s6.5 2.2 6.5 5.5"/>',
        pill: '<rect x="3.2" y="9" width="17.6" height="7" rx="3.5" transform="rotate(-45 12 12.5)"/><path d="M9.5 9.5l5 5"/>',
        scan: '<path d="M4 8.5v-2a2 2 0 0 1 2-2h2M20 8.5v-2a2 2 0 0 0-2-2h-2M4 15.5v2a2 2 0 0 0 2 2h2M20 15.5v2a2 2 0 0 1-2 2h-2M4 12h16"/>',
        chart: '<path d="M4 20h16"/><path d="M7.5 20V12M12 20V6M16.5 20V14.5"/>',
        gear: '<circle cx="12" cy="12" r="3"/><path d="M12 3.5v2M12 18.5v2M4.9 7.2l1.7 1.2M17.4 15.6l1.7 1.2M4.9 16.8l1.7-1.2M17.4 8.4l1.7-1.2"/>',
        file: '<path d="M6.5 3.5H14L18.5 8v12.5h-12V3.5Z"/><path d="M13.5 3.5V8.5h5"/>'
      }[name]
      const s =
        `<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">` +
        `<g stroke="${color}" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" fill="none">${base}</g></svg>`
      // #ifdef H5
      return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(s)
      // #endif
      // #ifndef H5
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(s)))
      // #endif
    },
    go(idx, item) {
      if (idx === this.current) return
      if (item.path) uni.reLaunch({ url: item.path })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 132rpx;
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  padding-top: 12rpx;
  box-sizing: border-box;
  background-color: $color-card;
  border-top: 2rpx solid $color-border;
  z-index: 30;
}

.tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

.tab-icon {
  width: 44rpx;
  height: 44rpx;
}

.tab-text {
  font-size: $font-xs;
  color: $color-text-muted;
}

.tab.active .tab-text {
  color: $color-primary-text;
  font-weight: 600;
}
</style>
