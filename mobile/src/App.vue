<script>
export default {
  onLaunch() {
    // 启动时校验登录态
    const token = uni.getStorageSync('his_token')
    if (!token) {
      const pages = getCurrentPages()
      const current = pages.length ? pages[pages.length - 1].route : ''
      if (current !== 'pages/login/index') {
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/tokens.scss';

page {
  background-color: $color-bg;
  color: $color-text;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Microsoft YaHei', sans-serif;
  font-size: $font-md;
  line-height: 1.55;
}

/* ================= 页面骨架 ================= */
.page-root {
  display: flex;
  flex-direction: column;
  min-height: $page-min-height;
  background-color: $color-bg;
}

.page-body {
  display: flex;
  flex-direction: column;
  gap: $gap-md;
  padding: $gap-md $page-padding $gap-xl $page-padding;
  flex: 1;
}

/* 带底部固定操作栏时给内容留出空间 */
.page-body.has-bar {
  padding-bottom: 220rpx;
}

/* 带底部标签栏时给内容留出空间，避免末行被固定栏遮挡 */
.page-body.tabbed {
  padding-bottom: 200rpx;
}

/* ================= 页头（无导航栏页面使用） ================= */
.page-head {
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 8rpx $page-padding 0 $page-padding;
}

.page-head-titles {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  min-width: 0;
}

.page-head-title {
  font-size: $font-xl;
  font-weight: 600;
  color: $color-text;
  letter-spacing: -0.5rpx;
}

.page-head-desc {
  font-size: $font-sm;
  color: $color-text-sub;
}

/* 数字使用等宽字形，保证纵向对齐 */
.num {
  font-family: 'SF Mono', 'JetBrains Mono', Consolas, monospace;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.3rpx;
}

/* ================= 卡片 ================= */
.card {
  display: flex;
  flex-direction: column;
  background-color: $color-card;
  border-radius: $radius-card;
  padding: $gap-md;
  gap: $gap-sm;
  border: 2rpx solid $color-border;
}

.card-head {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
}

.card-title {
  font-size: $font-lg;
  font-weight: 600;
  color: $color-text;
}

.card-meta {
  font-size: $font-xs;
  color: $color-text-muted;
}

.card-link {
  font-size: $font-sm;
  color: $color-primary-text;
}

/* 列表分隔行 */
.list-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 20rpx 0;
  border-top: 2rpx solid $color-divider;
}

.list-row:first-child {
  border-top: none;
}

/* ================= 行与文字 ================= */
.row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
}

.col {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
}

.t-sub {
  color: $color-text-sub;
}

.t-muted {
  color: $color-text-muted;
}

.t-primary {
  color: $color-primary-text;
}

.t-bold {
  font-weight: 600;
}

.t-sm {
  font-size: $font-sm;
}

.t-xs {
  font-size: $font-xs;
}

.t-lg {
  font-size: $font-lg;
}

/* ================= 状态标签 ================= */
.chip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36rpx;
  padding: 0 12rpx;
  border-radius: $radius-chip;
  font-size: $font-xs;
  font-weight: 500;
  white-space: nowrap;
  background-color: $color-info-tint;
  color: $color-info;
}

.chip.warn {
  background-color: $color-warn-tint;
  color: $color-warn;
}

.chip.danger {
  background-color: $color-danger-tint;
  color: $color-danger;
}

.chip.success {
  background-color: $color-success-tint;
  color: $color-success;
}

.chip.primary {
  background-color: $color-primary-tint;
  color: $color-primary-text;
}

.chip.muted {
  background-color: $color-info-tint;
  color: $color-text-muted;
}

/* ================= 按钮 ================= */
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 92rpx;
  border-radius: $radius-input;
  background-color: $color-primary;
  color: $color-text-invert;
  font-size: $font-lg;
  font-weight: 600;
  transition: background-color 150ms ease;
}

.btn-primary:active {
  background-color: $color-primary-active;
}

.btn-primary.disabled,
.btn-primary.is-disabled {
  background-color: $color-primary-line;
  color: rgba(255, 255, 255, 0.9);
}

.btn-ghost {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 92rpx;
  border-radius: $radius-input;
  background-color: $color-card;
  border: 2rpx solid $color-primary-line;
  color: $color-primary-text;
  font-size: $font-lg;
  font-weight: 600;
}

.btn-ghost:active {
  background-color: $color-primary-tint;
}

/* 表格内小操作按钮 */
.btn-mini {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 56rpx;
  padding: 0 26rpx;
  border-radius: $radius-sm;
  background-color: $color-primary-tint;
  border: 2rpx solid $color-primary-line;
  color: $color-primary-text;
  font-size: $font-sm;
  font-weight: 600;
}

.btn-mini:active {
  background-color: #d9e9de;
}

.btn-mini.plain {
  background-color: $color-card;
  border-color: $color-border;
  color: $color-text-sub;
}

/* ================= 输入 ================= */
.input-box {
  display: flex;
  align-items: center;
  height: 88rpx;
  padding: 0 24rpx;
  box-sizing: border-box;
  background-color: $color-fill;
  border: 2rpx solid transparent;
  border-radius: $radius-input;
  font-size: $font-md;
  color: $color-text;
}

.input-box.focus {
  border-color: $color-primary-line;
  background-color: $color-card;
}

.ph {
  color: $color-text-muted;
}

/* 分段控件 */
.segment {
  display: flex;
  flex-direction: row;
  padding: 6rpx;
  box-sizing: border-box;
  background-color: $color-sunken;
  border: 2rpx solid $color-border;
  border-radius: $radius-input;
}

.seg-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 68rpx;
  border-radius: $radius-xs;
  font-size: $font-sm;
  color: $color-text-sub;
  transition: background-color 150ms ease;
}

.seg-item.on {
  background-color: $color-card;
  border: 2rpx solid $color-primary-line;
  color: $color-primary-text;
  font-weight: 600;
}

/* 可选标签组 */
.chip-wrap {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 14rpx;
}

.pick-chip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64rpx;
  padding: 0 24rpx;
  border-radius: $radius-sm;
  background-color: $color-sunken;
  border: 2rpx solid $color-border;
  font-size: $font-sm;
  color: $color-text-sub;
}

.pick-chip.on {
  background-color: $color-primary-tint;
  border-color: $color-primary-line;
  color: $color-primary-text;
  font-weight: 600;
}

/* ================= 底部固定操作栏 ================= */
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: $gap-md;
  padding: 20rpx $page-padding 36rpx $page-padding;
  background-color: $color-card;
  border-top: 2rpx solid $color-border;
  z-index: 20;
}

.bottom-total {
  display: flex;
  flex-direction: column;
  gap: 2rpx;
  min-width: 200rpx;
}

.bottom-total-label {
  font-size: $font-xs;
  color: $color-text-sub;
}

.bottom-total-value {
  font-size: $font-xl;
  font-weight: 600;
  color: $color-text;
}

.flex1 {
  flex: 1;
}

/* ================= 空状态 ================= */
.empty {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 72rpx 0;
  font-size: $font-sm;
  color: $color-text-muted;
}

/* ================= 概览数值区（替代深色 hero） ================= */
.summary {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  padding: $gap-md;
  background-color: $color-card;
  border: 2rpx solid $color-border;
  border-radius: $radius-card;
}

.summary-head {
  display: flex;
  flex-direction: row;
  align-items: baseline;
  justify-content: space-between;
  gap: $gap-sm;
}

.summary-label {
  font-size: $font-sm;
  color: $color-text-sub;
}

.summary-value {
  font-size: $font-hero;
  font-weight: 600;
  color: $color-text;
  letter-spacing: -1rpx;
  line-height: 1.15;
}

.summary-foot {
  display: flex;
  flex-direction: row;
  gap: $gap-md;
  padding-top: 16rpx;
  border-top: 2rpx solid $color-divider;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.summary-item-value {
  font-size: $font-md;
  font-weight: 600;
  color: $color-text;
}

.summary-item-label {
  font-size: $font-sm;
  color: $color-text-sub;
}

/* ================= 功能宫格 ================= */
.grid {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}

.grid-item {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  padding: 18rpx 0;
}

.grid-icon {
  width: 76rpx;
  height: 76rpx;
  border-radius: $radius-sm;
  background-color: $color-sunken;
  border: 2rpx solid $color-border;
  display: flex;
  align-items: center;
  justify-content: center;
}

.grid-svg {
  width: 40rpx;
  height: 40rpx;
}

.grid-text {
  font-size: $font-sm;
  color: $color-text-sub;
}
</style>
