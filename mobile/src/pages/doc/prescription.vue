<template>
  <view class="page-root">
    <StatusBar />
    <NavBar title="开立处方" rightText="组套" @right="useSets" />

    <view class="page-body has-bar">
      <!-- 处方类型 -->
      <view class="segment">
        <view class="seg-item" :class="{ on: rxType === '成药处方' }" @tap="rxType = '成药处方'">
          <text>成药处方</text>
        </view>
        <view class="seg-item" :class="{ on: rxType === '草药处方' }" @tap="rxType = '草药处方'">
          <text>草药处方</text>
        </view>
      </view>

      <!-- 关联诊断 -->
      <view class="hint">
        <text class="hint-text">{{ diagnosisText }}</text>
      </view>

      <!-- 已添加药品 -->
      <view v-for="(it, idx) in items" :key="idx" class="drug">
        <view class="drug-head">
          <view class="col">
            <text class="drug-name">{{ it.drugsName }}</text>
            <text class="drug-spec">{{ it.drugsFormat }} · {{ it.drugsUnit }}</text>
          </view>
          <text class="drug-amt num">¥{{ (it.price * it.amount).toFixed(2) }}</text>
        </view>

        <view class="drug-meta">
          <text class="drug-use">{{ it.drugsUsage }} · {{ it.frequency }} · {{ it.dosage }}</text>
          <text class="drug-use">疗程 {{ it.days }} 天</text>
        </view>

        <view class="drug-foot">
          <view class="stepper">
            <view class="step" @tap="dec(idx)">−</view>
            <text class="step-num num">{{ it.amount }}</text>
            <view class="step" @tap="inc(idx)">+</view>
          </view>
          <text class="drug-del" @tap="removeItem(idx)">移除</text>
        </view>
      </view>

      <view v-if="!items.length" class="card">
        <view class="empty"><text>尚未添加药品，点击下方按钮从药品目录中选择</text></view>
      </view>

      <!-- 添加 -->
      <view class="add" @tap="addDrug">
        <image class="add-icon" :src="plusIcon" />
        <text>添加药品</text>
      </view>

      <!-- 小结 -->
      <view v-if="items.length" class="tally">
        <text class="t-xs t-sub">共 {{ items.length }} 种药品</text>
        <text class="tally-sum num">合计 ¥{{ total }}</text>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="btn-ghost flex1" @tap="saveAsSet">存为组套</view>
      <view class="btn-primary flex1" :class="{ 'is-disabled': !items.length }" @tap="submit">提交处方</view>
    </view>
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import NavBar from '@/components/NavBar.vue'
import { doctorApi } from '@/api'
import { useUserStore } from '@/store/user'
import { iconUrl } from '@/utils/icons'

export default {
  components: { StatusBar, NavBar },
  data() {
    return {
      registId: null,
      rxType: '成药处方',
      patient: {},
      record: {},
      diseases: [],
      items: [],
      drugs: []
    }
  },
  computed: {
    store() {
      return useUserStore()
    },
    total() {
      const sum = this.items.reduce((acc, it) => acc + Number(it.price || 0) * Number(it.amount || 0), 0)
      return sum.toFixed(2)
    },
    diagnosisText() {
      const d = this.record.diagnosis
      return d ? `关联诊断：${d} · ${this.patient.realName || ''}` : '尚未填写诊断，建议先完成门诊病历'
    },
    plusIcon() {
      return iconUrl('plus')
    }
  },
  async onLoad(opts) {
    this.store.sync()
    this.registId = Number(opts.registId || 0)
    if (!this.registId) {
      uni.showToast({ title: '缺少就诊参数', icon: 'none' })
      return
    }
    await Promise.all([this.loadRecord(), this.loadDrugs()])
  },
  methods: {
    async loadRecord() {
      try {
        const data = await doctorApi.medicalRecord(this.registId)
        this.patient = data.register || {}
        this.record = data.record || {}
      } catch (e) {}
    },
    async loadDrugs() {
      try {
        this.drugs = await doctorApi.drugs()
      } catch (e) {}
    },
    addDrug() {
      const list = this.rxType === '草药处方' ? this.drugs.filter((d) => d.drugsTypeId === 2) : this.drugs
      const source = list.length ? list : this.drugs
      if (!source.length) return uni.showToast({ title: '药品库为空', icon: 'none' })
      uni.showActionSheet({
        itemList: source.slice(0, 8).map((d) => `${d.drugsName}  ¥${d.drugsPrice}`),
        success: (res) => {
          const d = source[res.tapIndex]
          const exist = this.items.find((x) => x.drugsId === d.id)
          if (exist) {
            exist.amount += 1
          } else {
            this.items.push({
              drugsId: d.id,
              drugsName: d.drugsName,
              drugsFormat: d.drugsFormat,
              drugsUnit: d.drugsUnit,
              price: d.drugsPrice,
              stock: d.stock,
              amount: 1,
              days: 5,
              frequency: '每日3次',
              drugsUsage: '口服',
              dosage: '每次1粒'
            })
          }
          uni.showToast({ title: '已添加', icon: 'none' })
        }
      })
    },
    inc(idx) {
      this.items[idx].amount += 1
    },
    dec(idx) {
      if (this.items[idx].amount > 1) this.items[idx].amount -= 1
      else this.removeItem(idx)
    },
    removeItem(idx) {
      this.items.splice(idx, 1)
    },
    useSets() {
      uni.showToast({ title: '组套功能开发中', icon: 'none' })
    },
    saveAsSet() {
      if (!this.items.length) return uni.showToast({ title: '请先添加药品', icon: 'none' })
      uni.showToast({ title: '已存为组套', icon: 'none' })
    },
    async submit() {
      if (!this.items.length) return uni.showToast({ title: '请先添加药品', icon: 'none' })
      try {
        const data = await doctorApi.createPrescription({
          registId: this.registId,
          doctorId: this.store.userId,
          prescriptionName: this.rxType,
          items: this.items.map((it) => ({
            drugsId: it.drugsId,
            amount: it.amount,
            days: it.days,
            frequency: it.frequency,
            drugsUsage: it.drugsUsage,
            dosage: it.dosage
          }))
        })
        uni.showModal({
          title: '处方已提交',
          content: `处方号 ${data.id}\n共 ${data.items ? data.items.length : this.items.length} 种药品\n合计 ¥${Number(
            data.totalAmount
          ).toFixed(2)}`,
          showCancel: false,
          success: () => {
            this.items = []
            uni.navigateBack()
          }
        })
      } catch (e) {}
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.flex1 {
  flex: 1;
}

/* ---------- 提示 ---------- */
.hint {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  border-radius: $radius-sm;
  background-color: $color-primary-tint;
  border: 2rpx solid #d7e6f3;
}

.hint-text {
  font-size: $font-sm;
  color: $color-primary-text;
}

/* ---------- 药品条目 ---------- */
.drug {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  padding: $gap-md;
  background-color: $color-card;
  border: 2rpx solid $color-border;
  border-radius: $radius-card;
}

.drug-head {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: space-between;
  gap: $gap-sm;
}

.drug-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $color-text;
}

.drug-spec {
  font-size: $font-xs;
  color: $color-text-muted;
}

.drug-amt {
  font-size: 30rpx;
  font-weight: 600;
  color: $color-text;
  flex: none;
}

.drug-meta {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 6rpx 20rpx;
}

.drug-use {
  font-size: $font-xs;
  color: $color-text-sub;
}

.drug-foot {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding-top: 14rpx;
  border-top: 2rpx solid $color-divider;
}

.stepper {
  display: flex;
  flex-direction: row;
  align-items: center;
  border: 2rpx solid $color-border;
  border-radius: $radius-xs;
  overflow: hidden;
}

.step {
  width: 60rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: $color-text-sub;
  background-color: $color-sunken;
}

.step:active {
  background-color: #e7edf4;
}

.step-num {
  min-width: 68rpx;
  text-align: center;
  font-size: $font-md;
  color: $color-text;
}

.drug-del {
  font-size: $font-sm;
  color: $color-danger;
}

/* ---------- 添加按钮 ---------- */
.add {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  height: 96rpx;
  border-radius: $radius-card;
  background-color: $color-card;
  border: 2rpx dashed $color-primary-line;
  font-size: $font-md;
  font-weight: 600;
  color: $color-primary-text;
}

.add:active {
  background-color: $color-primary-tint;
}

.add-icon {
  width: 32rpx;
  height: 32rpx;
}

/* ---------- 小结 ---------- */
.tally {
  display: flex;
  flex-direction: row;
  align-items: baseline;
  justify-content: space-between;
  padding: 0 8rpx;
}

.tally-sum {
  font-size: 34rpx;
  font-weight: 600;
  color: $color-primary-text;
}
</style>
