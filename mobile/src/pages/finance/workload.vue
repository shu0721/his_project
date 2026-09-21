<template>
  <view class="page-root">
    <StatusBar />
    <NavBar title="工作量统计" rightText="导出" @right="onExport" />

    <view class="page-body tabbed">
      <!-- 营收概览 -->
      <view class="summary">
        <view class="summary-head">
          <text class="t-sm t-sub">今日实收</text>
          <text class="t-xs t-muted">{{ revenue.registCount || 0 }} 人次挂号</text>
        </view>
        <text class="summary-value num">¥{{ money(revenue.todayRevenue) }}</text>
        <view class="summary-foot">
          <view class="summary-item">
            <text class="summary-item-value num">{{ revenue.registCount || 0 }}</text>
            <text class="summary-item-label">挂号人次</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ revenue.prescriptionCount || 0 }}</text>
            <text class="summary-item-label">处方张数</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ doctorCount }}</text>
            <text class="summary-item-label">出诊医生</text>
          </view>
        </view>
      </view>

      <!-- 近 7 日营收 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">近 7 日营收</text>
          <text class="card-meta num">合计 ¥{{ money(weekTotal) }}</text>
        </view>

        <view class="chart">
          <view v-for="(t, i) in trend" :key="i" class="col">
            <text class="col-value num">{{ short(t.money) }}</text>
            <view class="col-track">
              <view class="col-bar" :class="{ peak: t.money === maxMoney && t.money > 0 }" :style="{ height: barH(t.money) }" />
            </view>
            <text class="col-label num">{{ t.label }}</text>
          </view>
          <view v-if="!trend.length" class="empty" style="flex: 1"><text>暂无营收数据</text></view>
        </view>
      </view>

      <!-- 费用构成 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">费用构成</text>
          <text class="card-meta">今日</text>
        </view>

        <view v-if="!byType.length" class="empty"><text>暂无费用数据</text></view>

        <view v-for="(b, i) in byType" :key="i" class="type">
          <text class="type-name">{{ b.name }}</text>
          <view class="type-track"><view class="type-bar" :style="{ width: shareOf(b.money) }" /></view>
          <text class="type-money num">¥{{ money(b.money) }}</text>
        </view>
      </view>

      <!-- 医生工作量 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">医生工作量</text>
          <view class="doctor-list" @tap="loadDoctorWorkload">
            <text class="card-link">{{ workload.length ? '展开明细' : '加载明细' }}</text>
          </view>
        </view>

        <view v-if="!workload.length" class="empty"><text>暂无医生工作量数据</text></view>

        <view v-for="d in workload" :key="d.doctorId || d.doctorName" class="doctor">
          <text class="doctor-name">{{ d.doctorName }}</text>
          <view class="doctor-stats">
            <text class="doctor-stat num">挂号 {{ d.registerCount || 0 }}</text>
            <text class="doctor-stat num">已诊 {{ d.visitedCount || 0 }}</text>
          </view>
        </view>
      </view>

      <view class="note">
        <text class="note-text">医生工作量与费用科目维护请前往管理后台「营收统计」页面查看完整报表。</text>
      </view>
    </view>
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import NavBar from '@/components/NavBar.vue'
import { financeApi } from '@/api'

export default {
  components: { StatusBar, NavBar },
  data() {
    return {
      revenue: {},
      workload: []
    }
  },
  computed: {
    trend() {
      return this.revenue.trend || []
    },
    byType() {
      return this.revenue.byType || []
    },
    maxMoney() {
      return this.trend.reduce((m, t) => Math.max(m, Number(t.money || 0)), 0)
    },
    weekTotal() {
      return this.trend.reduce((s, t) => s + Number(t.money || 0), 0)
    },
    doctorCount() {
      return this.workload.length
    }
  },
  onShow() {
    this.loadRevenue()
    this.loadDoctorWorkload()
  },
  methods: {
    money(v) {
      const n = Number(v || 0)
      return n.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },
    short(v) {
      const n = Number(v || 0)
      if (n >= 10000) return (n / 10000).toFixed(1) + '万'
      return n.toFixed(0)
    },
    barH(v) {
      const max = this.maxMoney || 1
      const h = Math.round((Number(v || 0) / max) * 176)
      return (h < 8 ? 8 : h) + 'rpx'
    },
    shareOf(v) {
      const total = this.byType.reduce((s, b) => s + Number(b.money || 0), 0) || 1
      const pct = Math.round((Number(v || 0) / total) * 100)
      return (pct < 4 ? 4 : pct) + '%'
    },
    async loadRevenue() {
      try {
        this.revenue = await financeApi.revenue()
      } catch (e) {}
    },
    async loadDoctorWorkload() {
      if (this.workload.length) {
        uni.showToast({ title: `共 ${this.workload.length} 位医生`, icon: 'none' })
        return
      }
      try {
        this.workload = await financeApi.doctorWorkload()
        if (!this.workload.length) uni.showToast({ title: '暂无医生工作量数据', icon: 'none' })
      } catch (e) {}
    },
    onExport() {
      uni.showToast({ title: '报表导出开发中', icon: 'none' })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

/* ---------- 柱状图 ---------- */
.chart {
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  justify-content: space-between;
  gap: 8rpx;
  height: 250rpx;
  margin-top: 8rpx;
}

.col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.col-value {
  font-size: $font-xs;
  color: $color-text-muted;
  margin-bottom: 8rpx;
}

.col-track {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  border-bottom: 2rpx solid $color-border;
}

.col-bar {
  width: 64%;
  max-width: 40rpx;
  border-radius: 6rpx 6rpx 0 0;
  background-color: $color-primary-soft;
}

.col-bar.peak {
  background-color: $color-primary;
}

.col-label {
  margin-top: 8rpx;
  font-size: $font-xs;
  color: $color-text-muted;
}

/* ---------- 费用构成 ---------- */
.type {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 16rpx;
  padding: 14rpx 0;
  border-top: 2rpx solid $color-divider;
}

.type-name {
  font-size: $font-sm;
  color: $color-text-sub;
  width: 150rpx;
  flex: none;
}

.type-track {
  flex: 1;
  height: 8rpx;
  border-radius: 4rpx;
  background-color: $color-sunken;
  overflow: hidden;
}

.type-bar {
  height: 100%;
  border-radius: 4rpx;
  background-color: $color-primary-line;
}

.type-money {
  font-size: $font-md;
  font-weight: 600;
  color: $color-text;
  flex: none;
}

/* ---------- 医生 ---------- */
.doctor {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 18rpx 0;
  border-top: 2rpx solid $color-divider;
}

.doctor-name {
  font-size: $font-md;
  color: $color-text;
}

.doctor-stats {
  display: flex;
  flex-direction: row;
  gap: 22rpx;
}

.doctor-stat {
  font-size: $font-sm;
  color: $color-text-sub;
}

.note {
  padding: 18rpx 22rpx;
  border-radius: $radius-sm;
  background-color: $color-sunken;
  border: 2rpx solid $color-border;
}

.note-text {
  font-size: $font-xs;
  color: $color-text-sub;
  line-height: 1.6;
}
</style>
