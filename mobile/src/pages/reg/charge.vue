<template>
  <view class="page-root">
    <StatusBar />
    <NavBar title="收费结算" :rightText="registId ? '待收费' : ''" @right="backToList" />

    <view class="page-body" :class="{ 'has-bar': registId }">
      <!-- 待收费列表 -->
      <template v-if="!registId">
        <view class="card">
          <view class="card-head">
            <text class="card-title">待收费患者</text>
            <text class="card-meta">就诊已完成 · {{ pending.length }} 人</text>
          </view>

          <view v-if="!pending.length" class="empty"><text>暂无需收费的就诊记录</text></view>

          <view v-for="r in pending" :key="r.id" class="pending" @tap="pick(r)">
            <view class="col">
              <text class="pending-name">{{ r.realName }}</text>
              <text class="pending-sub num">{{ r.caseNumber }} · {{ r.deptName }}</text>
            </view>
            <text class="chip" :class="r.visitState === 2 ? 'primary' : 'muted'">{{ r.visitStateText }}</text>
          </view>
        </view>
      </template>

      <!-- 结算 -->
      <template v-else>
        <view class="card">
          <view class="card-head">
            <text class="card-title">患者信息</text>
            <text class="chip">{{ patient.settleName || '自费' }}</text>
          </view>
          <view class="row">
            <view class="col">
              <text class="p-name">{{ patient.realName }}</text>
              <text class="p-sub">{{ patient.genderText }} · {{ patient.age }}岁 · {{ patient.idnumber }}</text>
            </view>
          </view>
          <view class="p-meta">
            <text class="p-meta-line num">就诊号 {{ patient.caseNumber }}</text>
            <text class="p-meta-line">{{ patient.deptName }} · {{ patient.doctorName }}</text>
          </view>
        </view>

        <!-- 费用清单 -->
        <view class="card">
          <view class="card-head">
            <text class="card-title">费用清单</text>
            <text class="card-meta">共 {{ items.length }} 项</text>
          </view>

          <view v-if="!items.length" class="empty"><text>暂无待收费项目</text></view>

          <view v-for="(it, i) in items" :key="i" class="fee">
            <view class="col">
              <text class="fee-name">{{ it.name }}</text>
              <text class="fee-sub">{{ it.format || '项目' }} × {{ it.amount }}</text>
            </view>
            <text class="fee-sum num">¥{{ it.sum.toFixed(2) }}</text>
          </view>

          <view v-if="items.length" class="fee-total">
            <text class="t-sm t-sub">应收合计</text>
            <text class="fee-total-value num">¥{{ total }}</text>
          </view>
        </view>

        <!-- 结算类别 -->
        <view class="card">
          <view class="card-head">
            <text class="card-title">结算类别</text>
          </view>
          <view class="chip-wrap">
            <view
              v-for="s in settles"
              :key="s.id"
              class="pick-chip"
              :class="{ on: payForm.settleId === s.id }"
              @tap="payForm.settleId = s.id"
            >
              <text>{{ s.settleName }}</text>
            </view>
          </view>
        </view>

        <!-- 支付方式 -->
        <view class="card">
          <view class="card-head">
            <text class="card-title">支付方式</text>
          </view>
          <view class="chip-wrap">
            <view
              v-for="p in payMethods"
              :key="p.value"
              class="pick-chip"
              :class="{ on: payForm.payMethod === p.value }"
              @tap="payForm.payMethod = p.value"
            >
              <text>{{ p.label }}</text>
            </view>
          </view>
        </view>
      </template>
    </view>

    <view v-if="registId" class="bottom-bar">
      <view class="bottom-total">
        <text class="bottom-total-label">实收合计</text>
        <text class="bottom-total-value num">¥{{ total }}</text>
      </view>
      <view class="btn-primary flex1" :class="{ 'is-disabled': paying }" @tap="pay">
        {{ paying ? '处理中…' : '确认收费' }}
      </view>
    </view>
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import NavBar from '@/components/NavBar.vue'
import { registerApi } from '@/api'

export default {
  components: { StatusBar, NavBar },
  data() {
    return {
      registId: null,
      pending: [],
      settles: [],
      patient: {},
      items: [],
      paying: false,
      payForm: { settleId: 1, payMethod: 1 },
      payMethods: [
        { value: 1, label: '现金' },
        { value: 2, label: '微信' },
        { value: 3, label: '支付宝' },
        { value: 4, label: '银行卡' }
      ]
    }
  },
  computed: {
    total() {
      const sum = this.items.reduce((acc, it) => acc + Number(it.sum || 0), 0)
      return sum.toFixed(2)
    }
  },
  onLoad(opts) {
    this.loadSettles()
    if (opts && opts.registId) {
      this.registId = Number(opts.registId)
      this.loadCharge()
    } else {
      this.loadPending()
    }
  },
  methods: {
    async loadSettles() {
      try {
        this.settles = await registerApi.settleCategories()
      } catch (e) {}
    },
    async loadPending() {
      try {
        this.pending = await registerApi.list({ visitState: 3 })
      } catch (e) {}
    },
    async loadCharge() {
      try {
        const data = await registerApi.chargeList(this.registId)
        this.patient = data.register || {}
        this.items = data.items || []
        if (this.patient.settleId) this.payForm.settleId = this.patient.settleId
      } catch (e) {}
    },
    pick(r) {
      this.registId = r.id
      this.loadCharge()
    },
    backToList() {
      this.registId = null
      this.items = []
      this.loadPending()
    },
    async pay() {
      if (!this.items.length) return uni.showToast({ title: '没有待收费项目', icon: 'none' })
      this.paying = true
      try {
        const data = await registerApi.pay({
          registId: this.registId,
          settleId: this.payForm.settleId,
          payMethod: this.payForm.payMethod
        })
        uni.showModal({
          title: '收费成功',
          content: `发票号 ${data.invoiceNum}\n实收 ¥${Number(data.money).toFixed(2)}`,
          showCancel: false,
          success: () => this.backToList()
        })
      } catch (e) {
      } finally {
        this.paying = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.flex1 {
  flex: 1;
}

/* ---------- 待收费 ---------- */
.pending {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 20rpx 0;
  border-top: 2rpx solid $color-divider;
}

.pending-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $color-text;
}

.pending-sub {
  font-size: $font-sm;
  color: $color-text-sub;
}

/* ---------- 患者 ---------- */
.p-name {
  font-size: 34rpx;
  font-weight: 600;
  color: $color-text;
}

.p-sub {
  font-size: $font-sm;
  color: $color-text-sub;
}

.p-meta {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  padding-top: 16rpx;
  border-top: 2rpx solid $color-divider;
}

.p-meta-line {
  font-size: $font-sm;
  color: $color-text-sub;
}

/* ---------- 费用 ---------- */
.fee {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 16rpx 0;
  border-top: 2rpx solid $color-divider;
}

.fee-name {
  font-size: $font-md;
  color: $color-text;
}

.fee-sub {
  font-size: $font-xs;
  color: $color-text-muted;
}

.fee-sum {
  font-size: $font-md;
  font-weight: 600;
  color: $color-text;
  flex: none;
}

.fee-total {
  display: flex;
  flex-direction: row;
  align-items: baseline;
  justify-content: space-between;
  padding-top: 16rpx;
  border-top: 2rpx solid $color-divider;
}

.fee-total-value {
  font-size: 34rpx;
  font-weight: 600;
  color: $color-primary-text;
}
</style>
