<template>
  <view class="page-root">
    <StatusBar />

    <view class="page-head">
      <view class="page-head-titles">
        <text class="page-head-title">医技工作台</text>
        <text class="page-head-desc">{{ deptName || '检验科' }} · {{ realName }}</text>
      </view>
      <text class="chip primary">今日 {{ overview.todayTotal || 0 }} 项</text>
    </view>

    <view class="page-body">
      <!-- 概览 -->
      <view class="summary">
        <view class="summary-head">
          <text class="t-sm t-sub">今日执行情况</text>
          <text class="t-xs t-muted">待执行 {{ pendingCount }} 项</text>
        </view>
        <view class="summary-foot no-border">
          <view class="summary-item">
            <text class="summary-item-value num">{{ overview.todayTotal || 0 }}</text>
            <text class="summary-item-label">今日申请</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ overview.finishedCount || 0 }}</text>
            <text class="summary-item-label">已完成</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ pendingCount }}</text>
            <text class="summary-item-label">待执行</text>
          </view>
        </view>
      </view>

      <!-- 类型筛选 -->
      <view class="segment">
        <view
          v-for="t in types"
          :key="String(t.value)"
          class="seg-item"
          :class="{ on: recordType === t.value }"
          @tap="setType(t.value)"
        >
          <text>{{ t.label }}</text>
        </view>
      </view>

      <!-- 申请列表 -->
      <view v-if="!applies.length" class="card">
        <view class="empty"><text>暂无待执行的申请</text></view>
      </view>

      <view v-for="a in applies" :key="a.id" class="card">
        <view class="card-head">
          <view class="col">
            <text class="apply-patient">{{ a.patientName }}</text>
            <text class="apply-dept">{{ a.deptName }}</text>
          </view>
          <text class="chip" :class="stateClass(a.state)">{{ a.stateText }}</text>
        </view>

        <view class="apply-body">
          <text class="apply-item">{{ a.name }}</text>
          <text class="apply-doctor">{{ a.doctorName }} 申请</text>
        </view>

        <view class="apply-actions">
          <view v-if="a.state === 0 || a.state === 1" class="btn-mini" @tap="start(a)">开始执行</view>
          <view v-if="a.state === 2 || a.state === 1" class="btn-mini plain" @tap="inputResult(a)">录入结果</view>
        </view>
      </view>

      <!-- 说明 -->
      <view class="note">
        <text class="note-text">检验、检查与处置项目的基础价格维护请前往管理后台的「非药品项目」页面。</text>
      </view>
    </view>
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import { techApi } from '@/api'
import { useUserStore } from '@/store/user'

export default {
  components: { StatusBar },
  data() {
    return {
      overview: {},
      recordType: null,
      applies: [],
      types: [
        { value: null, label: '全部' },
        { value: 2, label: '检验' },
        { value: 1, label: '检查' },
        { value: 4, label: '处置' }
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
    deptName() {
      return this.store.deptName
    },
    pendingCount() {
      return this.applies.filter((a) => a.state === 0 || a.state === 1).length
    }
  },
  onShow() {
    this.store.sync()
    this.loadOverview()
    this.loadApplies()
  },
  methods: {
    stateClass(s) {
      if (s === 0) return 'warn'
      if (s === 1) return 'primary'
      if (s === 2) return 'primary'
      if (s === 3) return 'success'
      return 'muted'
    },
    setType(v) {
      this.recordType = v
      this.loadApplies()
    },
    async loadOverview() {
      try {
        this.overview = await techApi.overview()
      } catch (e) {}
    },
    async loadApplies() {
      try {
        const params = {}
        if (this.recordType) params.recordType = this.recordType
        this.applies = await techApi.applies(params)
      } catch (e) {}
    },
    async start(a) {
      try {
        await techApi.start(a.id)
        uni.showToast({ title: '已开始执行', icon: 'none' })
        this.loadApplies()
      } catch (e) {}
    },
    inputResult(a) {
      uni.showModal({
        title: '录入结果',
        editable: true,
        placeholderText: '请输入检验 / 检查结果',
        success: async (res) => {
          if (!res.confirm || !res.content) return
          try {
            await techApi.result({ id: a.id, result: res.content })
            uni.showToast({ title: '结果已提交', icon: 'none' })
            this.loadApplies()
            this.loadOverview()
          } catch (e) {}
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.summary-foot.no-border {
  border-top: none;
  padding-top: 0;
}

.apply-patient {
  font-size: 30rpx;
  font-weight: 600;
  color: $color-text;
}

.apply-dept {
  font-size: $font-sm;
  color: $color-text-sub;
}

.apply-body {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  padding: 14rpx 0;
  border-top: 2rpx solid $color-divider;
}

.apply-item {
  font-size: $font-md;
  color: $color-text;
}

.apply-doctor {
  font-size: $font-xs;
  color: $color-text-muted;
}

.apply-actions {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  gap: 16rpx;
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
