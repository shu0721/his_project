<template>
  <view class="page-root">
    <StatusBar />
    <NavBar title="门诊发药" rightText="发药记录" @right="showHistory" />

    <view class="page-body tabbed">
      <!-- 待发药概览 -->
      <view class="summary">
        <view class="summary-head">
          <text class="t-sm t-sub">待发药处方</text>
          <text class="t-xs t-muted">今日已发 {{ dispensedCount }} 张</text>
        </view>
        <view class="pending-line">
          <text class="summary-value num">{{ pendingCount }}</text>
          <text class="pending-unit">张</text>
        </view>
      </view>

      <!-- 处方列表 -->
      <view v-if="!list.length" class="card">
        <view class="empty"><text>暂无待发药处方</text></view>
      </view>

      <view v-for="p in list" :key="p.id" class="card rx" @tap="toggle(p)">
        <view class="card-head">
          <text class="rx-no num">RX{{ String(p.id).padStart(6, '0') }}</text>
          <text class="chip" :class="p.prescriptionState === 1 ? 'warn' : 'success'">
            {{ p.prescriptionStateText }}
          </text>
        </view>

        <view class="rx-body">
          <text class="rx-patient">{{ p.realName }} · {{ p.genderText }} · {{ p.age }}岁</text>
          <view class="rx-line">
            <text class="rx-sum">{{ p.items ? p.items.length : 0 }} 种药品</text>
            <text class="rx-amount num">¥{{ Number(p.totalAmount).toFixed(2) }}</text>
          </view>
        </view>

        <view class="rx-foot">
          <text class="rx-doc">{{ p.doctorName }} · {{ p.deptName }}</text>
          <view v-if="p.prescriptionState === 1" class="btn-mini" @tap.stop="dispense(p)">确认发药</view>
          <text v-else class="rx-toggle">{{ expanded === p.id ? '收起明细' : '查看明细' }}</text>
        </view>

        <!-- 明细 -->
        <view v-if="expanded === p.id" class="detail">
          <view v-for="(it, i) in p.items" :key="i" class="detail-row">
            <text class="detail-name">{{ it.drugsName }} {{ it.drugsFormat }}</text>
            <text class="detail-amt num">×{{ it.amount }}{{ it.drugsUnit }} ¥{{ Number(it.sum).toFixed(2) }}</text>
          </view>
        </view>
      </view>
    </view>

    <AppTabBar :current="0" :items="tabs" />
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import NavBar from '@/components/NavBar.vue'
import AppTabBar from '@/components/AppTabBar.vue'
import { pharmacyApi } from '@/api'

export default {
  components: { StatusBar, NavBar, AppTabBar },
  data() {
    return {
      overview: {},
      list: [],
      expanded: null,
      tabs: [
        { text: '发药', icon: 'pill' },
        { text: '库存', icon: 'grid', path: '/pages/pharm/drugs' },
        { text: '我的', icon: 'user' }
      ]
    }
  },
  computed: {
    pendingCount() {
      return this.overview.pendingCount != null
        ? this.overview.pendingCount
        : this.list.filter((x) => x.prescriptionState === 1).length
    },
    dispensedCount() {
      return this.overview.dispensedToday || 0
    }
  },
  onShow() {
    this.loadOverview()
    this.loadList()
  },
  methods: {
    async loadOverview() {
      try {
        this.overview = await pharmacyApi.overview()
      } catch (e) {}
    },
    async loadList() {
      try {
        this.list = await pharmacyApi.prescriptions({ state: 1 })
      } catch (e) {}
    },
    toggle(p) {
      this.expanded = this.expanded === p.id ? null : p.id
    },
    async dispense(p) {
      try {
        const data = await pharmacyApi.dispense(p.id)
        const detail = (data.items || [])
          .map((i) => `${i.drugsName} ×${i.amount}（余 ${i.remainStock}）`)
          .join('\n')
        uni.showModal({
          title: '发药成功',
          content: detail || '已完成发药',
          showCancel: false,
          success: () => {
            this.loadList()
            this.loadOverview()
          }
        })
      } catch (e) {}
    },
    showHistory() {
      pharmacyApi
        .prescriptions({ state: 3 })
        .then((list) => {
          if (!list.length) return uni.showToast({ title: '暂无发药记录', icon: 'none' })
          uni.showModal({
            title: '发药记录',
            content: list
              .slice(0, 6)
              .map((x) => `RX${String(x.id).padStart(6, '0')} ${x.realName} ¥${Number(x.totalAmount).toFixed(2)}`)
              .join('\n'),
            showCancel: false
          })
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.page-body.tabbed {
  padding-bottom: 200rpx;
}

.pending-line {
  display: flex;
  flex-direction: row;
  align-items: baseline;
  gap: 10rpx;
}

.pending-unit {
  font-size: $font-sm;
  color: $color-text-sub;
}

/* ---------- 处方 ---------- */
.rx-no {
  font-size: 26rpx;
  font-weight: 600;
  color: $color-text;
  letter-spacing: 0.4rpx;
}

.rx-body {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  padding-top: 14rpx;
  border-top: 2rpx solid $color-divider;
}

.rx-patient {
  font-size: $font-sm;
  color: $color-text-sub;
}

.rx-line {
  display: flex;
  flex-direction: row;
  align-items: baseline;
  justify-content: space-between;
}

.rx-sum {
  font-size: $font-sm;
  color: $color-text;
}

.rx-amount {
  font-size: 30rpx;
  font-weight: 600;
  color: $color-text;
}

.rx-foot {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
}

.rx-doc {
  font-size: $font-sm;
  color: $color-text-sub;
  flex: 1;
}

.rx-toggle {
  font-size: $font-sm;
  color: $color-primary-text;
}

/* ---------- 明细 ---------- */
.detail {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  padding-top: 14rpx;
  border-top: 2rpx solid $color-divider;
  background-color: $color-sunken;
  margin: 0 -#{$gap-md} -#{$gap-md};
  padding-left: $gap-md;
  padding-right: $gap-md;
  padding-bottom: $gap-md;
  border-radius: 0 0 $radius-card $radius-card;
}

.detail-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
}

.detail-name {
  font-size: $font-sm;
  color: $color-text-sub;
  flex: 1;
}

.detail-amt {
  font-size: $font-sm;
  color: $color-text;
  flex: none;
}
</style>
