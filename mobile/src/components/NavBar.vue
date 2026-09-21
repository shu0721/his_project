<template>
  <view class="nav">
    <view class="nav-left" @tap="onLeft">
      <image class="nav-back" :src="backIcon" />
    </view>
    <text class="nav-title">{{ title }}</text>
    <view class="nav-right">
      <text v-if="rightText" class="nav-action" @tap="$emit('right')">{{ rightText }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'NavBar',
  props: {
    title: { type: String, default: '' },
    rightText: { type: String, default: '' }
  },
  emits: ['right'],
  computed: {
    backIcon() {
      const s =
        '<svg width="22" height="22" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg">' +
        '<path d="M13.5 5L7.5 11L13.5 17" stroke="#1f2b24" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>' +
        '</svg>'
      // #ifdef H5
      return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(s)
      // #endif
      // #ifndef H5
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(s)))
      // #endif
    }
  },
  methods: {
    onLeft() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.nav {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  height: $nav-height;
  padding: 0 $page-padding;
  box-sizing: border-box;
}

.nav-left,
.nav-right {
  width: 130rpx;
  display: flex;
  align-items: center;
}

.nav-right {
  justify-content: flex-end;
}

.nav-back {
  width: 44rpx;
  height: 44rpx;
}

.nav-title {
  font-size: $font-lg;
  font-weight: 600;
  color: $color-text;
  letter-spacing: -0.5rpx;
}

.nav-action {
  font-size: $font-sm;
  color: $color-primary-text;
}
</style>
