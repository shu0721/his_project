<template>
  <view class="page-root">
    <StatusBar />

    <view class="page-head">
      <view class="page-head-titles">
        <text class="page-head-title">{{ greeting }}，{{ realName }}</text>
        <text class="page-head-desc">{{ deptName }} · 门诊医生</text>
      </view>
      <text class="chip primary">出诊中</text>
    </view>

    <view class="page-body">
      <!-- 今日接诊 -->
      <view class="summary">
        <view class="summary-head">
          <text class="t-sm t-sub">今日已接诊</text>
          <text class="t-xs t-muted">{{ overview.visitingCount || 0 }} 人就诊中</text>
        </view>
        <view class="accepted">
          <text class="summary-value num">{{ acceptedCount }}</text>
          <text class="accepted-unit">人次</text>
        </view>
        <view class="summary-foot">
          <view class="summary-item">
            <text class="summary-item-value num">{{ overview.waitingCount || 0 }}</text>
            <text class="summary-item-label">候诊人数</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ overview.visitingCount || 0 }}</text>
            <text class="summary-item-label">就诊中</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ overview.finishedCount || 0 }}</text>
            <text class="summary-item-label">已诊毕</text>
          </view>
        </view>
      </view>

      <!-- 诊疗功能 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">诊疗功能</text>
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

      <!-- 候诊患者 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">候诊患者</text>
          <text class="card-link" @tap="loadAll">刷新</text>
        </view>

        <view v-if="!waiting.length" class="empty"><text>暂无候诊患者</text></view>

        <view v-for="p in waiting" :key="p.id" class="patient">
          <view class="col">
            <text class="patient-name">{{ p.realName }}</text>
            <text class="patient-sub">{{ p.genderText }} · {{ p.age }}岁 · {{ p.registName }}</text>
          </view>
          <view class="patient-right">
            <text class="chip" :class="p.visitState === 2 ? 'primary' : 'warn'">{{ p.visitStateText }}</text>
            <view class="btn-mini" @tap="start(p)">{{ p.visitState === 2 ? '继续病历' : '接诊' }}</view>
          </view>
        </view>
      </view>
    </view>

    <AppTabBar :current="0" :items="tabs" />
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import AppTabBar from '@/components/AppTabBar.vue'
import { doctorApi } from '@/api'
import { STORAGE_USER } from '@/api/config'
import { useUserStore } from '@/store/user'
import { iconUrl } from '@/utils/icons'

export default {
  components: { StatusBar, AppTabBar },
  data() {
    return {
      overview: {},
      waiting: [],
      menus: [
        { text: '病历首页', icon: 'file', action: 'record' },
        { text: '检查申请', icon: 'scan', action: 'check' },
        { text: '处置申请', icon: 'scissors', action: 'treat' },
        { text: '门诊确诊', icon: 'stethoscope', action: 'diagnose' },
        { text: '成药处方', icon: 'pill', action: 'rx' },
        { text: '草药处方', icon: 'leaf', action: 'herb' },
        { text: '组套管理', icon: 'layers', action: 'sets' },
        { text: '费用查询', icon: 'wallet', action: 'fee' }
      ],
      tabs: [
        { text: '工作台', icon: 'grid' },
        { text: '病历', icon: 'file' },
        { text: '处方', icon: 'pill' },
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
    deptName() {
      return this.store.deptName
    },
    acceptedCount() {
      return (this.overview.visitingCount || 0) + (this.overview.finishedCount || 0)
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
    // 页面从后台恢复时 store 可能是缓存快照，先同步一次再拉数据
    this.store.sync()
    this.loadAll()
  },
  methods: {
    icon(name) {
      return iconUrl(name)
    },
    /** 当前登录医生 ID（store 未就绪时兜底读本地缓存） */
    doctorId() {
      const id = this.store.userId
      if (id) return id
      const cached = uni.getStorageSync(STORAGE_USER)
      return cached && cached.id ? cached.id : 0
    },
    loadAll() {
      const doctorId = this.doctorId()
      if (!doctorId) return
      this.loadWaiting(doctorId)
      this.loadOverview(doctorId)
    },
    async loadWaiting(doctorId) {
      try {
        this.waiting = await doctorApi.waitingList({ doctorId })
      } catch (e) {}
    },
    async loadOverview(doctorId) {
      try {
        this.overview = await doctorApi.overview(doctorId)
      } catch (e) {}
    },
    /** 取当前接诊对象：优先就诊中，其次候诊 */
    currentPatient() {
      return this.waiting.find((x) => x.visitState === 2) || this.waiting[0] || null
    },
    async start(p) {
      if (p.visitState === 1) {
        try {
          await doctorApi.accept(p.id)
        } catch (e) {
          return
        }
      }
      uni.navigateTo({ url: `/pages/doc/record?registId=${p.id}` })
    },
    onMenu(m) {
      if (m.action === 'record' || m.action === 'rx' || m.action === 'herb') {
        const target = this.currentPatient()
        if (!target) return uni.showToast({ title: '暂无候诊患者', icon: 'none' })
        const page = m.action === 'record' ? 'record' : 'prescription'
        uni.navigateTo({ url: `/pages/doc/${page}?registId=${target.id}` })
        return
      }
      if (m.action === 'check' || m.action === 'treat') {
        const target = this.currentPatient()
        if (!target) return uni.showToast({ title: '暂无候诊患者', icon: 'none' })
        this.openCheckApply(target, m.action)
        return
      }
      uni.showToast({ title: `${m.text} 功能开发中`, icon: 'none' })
    },
    async openCheckApply(target, action) {
      try {
        const items = await doctorApi.fmedItems({ recordType: action === 'check' ? 2 : 4 })
        if (!items.length) return uni.showToast({ title: '无可申请项目', icon: 'none' })
        uni.showActionSheet({
          itemList: items.slice(0, 6).map((i) => `${i.itemName}  ¥${i.price}`),
          success: async (res) => {
            const picked = items[res.tapIndex]
            await doctorApi.createCheckApply({
              registId: target.id,
              doctorId: this.store.userId,
              items: [{ itemId: picked.id, num: 1 }]
            })
            uni.showToast({ title: '申请已提交', icon: 'none' })
          }
        })
      } catch (e) {}
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.accepted {
  display: flex;
  flex-direction: row;
  align-items: baseline;
  gap: 10rpx;
}

.accepted-unit {
  font-size: $font-sm;
  color: $color-text-sub;
}

.patient {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 20rpx 0;
  border-top: 2rpx solid $color-divider;
}

.patient-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $color-text;
}

.patient-sub {
  font-size: $font-sm;
  color: $color-text-sub;
}

.patient-right {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 14rpx;
  flex: none;
}
</style>
