<template>
  <view class="page-root">
    <StatusBar />

    <view class="page-head">
      <view class="page-head-titles">
        <text class="page-head-title">{{ greeting }}，{{ realName }}</text>
        <text class="page-head-desc">{{ roleName }} · {{ deptName || '门诊大厅' }}</text>
      </view>
    </view>

    <view class="page-body tabbed">
      <!-- 今日概况 -->
      <view class="summary">
        <view class="summary-head">
          <text class="t-sm t-sub">今日实收</text>
          <text class="t-xs t-muted">共 {{ overview.registCount || 0 }} 号</text>
        </view>
        <text class="summary-value num">¥{{ money(overview.todayRevenue) }}</text>
        <view class="summary-foot">
          <view class="summary-item">
            <text class="summary-item-value num">{{ overview.registCount || 0 }}</text>
            <text class="summary-item-label">挂号人次</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ overview.prescriptionCount || 0 }}</text>
            <text class="summary-item-label">处方张数</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ pendingCount }}</text>
            <text class="summary-item-label">待收费</text>
          </view>
        </view>
      </view>

      <!-- 常用功能 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">常用功能</text>
        </view>
        <view class="grid">
          <view v-for="m in menus" :key="m.text" class="grid-item" @tap="onMenu(m)">
            <view class="grid-icon">
              <image class="grid-svg" :src="icon(m.icon)" />
            </view>
            <text class="grid-text">{{ m.text }}</text>
          </view>
        </view>
      </view>

      <!-- 今日挂号队列 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">今日挂号队列</text>
          <text class="card-link" @tap="refresh">刷新</text>
        </view>

        <view v-if="!queue.length" class="empty"><text>今天还没有挂号记录</text></view>

        <view v-for="r in queue" :key="r.id" class="queue" @tap="toCharge(r)">
          <view class="queue-main">
            <view class="queue-line">
              <text class="queue-name">{{ r.realName }}</text>
              <text class="chip" :class="stateClass(r.visitState)">{{ r.visitStateText }}</text>
            </view>
            <text class="queue-sub num">{{ r.caseNumber }}</text>
            <text class="queue-sub">{{ r.deptName }} · {{ r.registName }}</text>
          </view>
          <image class="queue-arrow" :src="arrowIcon" />
        </view>
      </view>
    </view>

    <AppTabBar :current="0" :items="tabs" />
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import AppTabBar from '@/components/AppTabBar.vue'
import { registerApi } from '@/api'
import { useUserStore } from '@/store/user'
import { iconUrl } from '@/utils/icons'

export default {
  components: { StatusBar, AppTabBar },
  data() {
    return {
      overview: {},
      queue: [],
      menus: [
        { text: '现场挂号', icon: 'calendar', path: '/pages/reg/register' },
        { text: '收费结算', icon: 'cash', path: '/pages/reg/charge' },
        { text: '退号处理', icon: 'refund', action: 'refund' },
        { text: '就诊卡', icon: 'card', action: 'card' },
        { text: '挂号查询', icon: 'list', action: 'query' },
        { text: '发票管理', icon: 'ledger', action: 'invoice' },
        { text: '交班结算', icon: 'wallet', action: 'shift' },
        { text: '刷新数据', icon: 'refresh', action: 'refresh' }
      ],
      tabs: [
        { text: '工作台', icon: 'grid' },
        { text: '挂号', icon: 'ticket', path: '/pages/reg/register' },
        { text: '收费', icon: 'cash', path: '/pages/reg/charge' },
        { text: '我的', icon: 'user' }
      ]
    }
  },
  computed: {
    store() {
      return useUserStore()
    },
    realName() {
      return this.store.realName
    },
    roleName() {
      return this.store.roleName
    },
    deptName() {
      return this.store.deptName
    },
    pendingCount() {
      return this.queue.filter((r) => r.visitState === 3).length
    },
    arrowIcon() {
      return iconUrl('arrowRight')
    },
    greeting() {
      const h = new Date().getHours()
      if (h < 6) return '凌晨好'
      if (h < 12) return '早上好'
      if (h < 14) return '中午好'
      if (h < 18) return '下午好'
      return '晚上好'
    }
  },
  onShow() {
    this.store.sync()
    this.loadOverview()
    this.loadQueue()
  },
  methods: {
    icon(name) {
      return iconUrl(name)
    },
    money(v) {
      const n = Number(v || 0)
      return n.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },
    stateClass(s) {
      if (s === 1) return 'warn'
      if (s === 2) return 'primary'
      if (s === 3) return 'success'
      return 'muted'
    },
    async loadOverview() {
      try {
        this.overview = await registerApi.overview()
      } catch (e) {}
    },
    async loadQueue() {
      try {
        this.queue = await registerApi.list({})
      } catch (e) {}
    },
    refresh() {
      this.loadOverview()
      this.loadQueue()
      uni.showToast({ title: '已刷新', icon: 'none' })
    },
    onMenu(m) {
      if (m.path) {
        uni.navigateTo({ url: m.path })
        return
      }
      if (m.action === 'refresh') return this.refresh()
      if (m.action === 'refund') {
        uni.showModal({
          title: '退号处理',
          content: '请从下方挂号队列中选择记录，进入详情后执行退号。',
          showCancel: false
        })
        return
      }
      uni.showToast({ title: `${m.text} 功能开发中`, icon: 'none' })
    },
    toCharge(r) {
      uni.navigateTo({ url: `/pages/reg/charge?registId=${r.id}` })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.queue {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 20rpx 0;
  border-top: 2rpx solid $color-divider;
}

.queue-main {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
  flex: 1;
}

.queue-line {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 12rpx;
}

.queue-name {
  font-size: $font-md;
  font-weight: 600;
  color: $color-text;
}

.queue-sub {
  font-size: $font-sm;
  color: $color-text-sub;
}

.queue-arrow {
  width: 32rpx;
  height: 32rpx;
  flex: none;
}
</style>
