<template>
  <view class="page-root">
    <StatusBar />
    <NavBar title="门诊病历" :rightText="recordAllergy ? '过敏史' : ''" @right="showAllergy" />

    <view class="page-body has-bar">
      <!-- 患者 -->
      <view class="card">
        <view class="card-head">
          <view class="col">
            <text class="p-name">{{ patient.realName }}</text>
            <text class="p-sub">{{ patient.genderText }} · {{ patient.age }}岁 · {{ patient.registName }}</text>
          </view>
          <text class="chip primary">{{ patient.visitStateText }}</text>
        </view>
        <view v-if="recordAllergy" class="alert">
          <text class="alert-text">过敏史：{{ recordAllergy }}</text>
        </view>
      </view>

      <!-- 病情记录 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">病情记录</text>
          <text class="card-meta">带 * 为必填</text>
        </view>

        <view class="field">
          <text class="field-label">主诉 *</text>
          <textarea
            class="textarea"
            v-model="form.readme"
            placeholder="患者主要症状与持续时间"
            placeholder-class="ph"
          />
        </view>

        <view class="field">
          <text class="field-label">现病史</text>
          <textarea
            class="textarea"
            v-model="form.present"
            placeholder="发病经过、伴随症状、院外诊疗情况"
            placeholder-class="ph"
          />
        </view>

        <view class="field">
          <text class="field-label">既往史</text>
          <textarea
            class="textarea small"
            v-model="form.history"
            placeholder="既往疾病与手术史"
            placeholder-class="ph"
          />
        </view>

        <view class="field">
          <text class="field-label">体格检查</text>
          <textarea
            class="textarea small"
            v-model="form.physique"
            placeholder="生命体征与阳性体征"
            placeholder-class="ph"
          />
        </view>
      </view>

      <!-- 初步诊断 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">初步诊断 *</text>
          <text class="card-link" @tap="pickDisease">从诊断库选择</text>
        </view>
        <view class="diag" @tap="pickDisease">
          <text class="diag-text" :class="{ ph: !form.diagnosis }">
            {{ form.diagnosis || '点击选择 ICD-10 诊断' }}
          </text>
          <image class="diag-arrow" :src="arrowIcon" />
        </view>
      </view>

      <!-- 处理意见 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">处理意见</text>
        </view>
        <textarea
          class="textarea"
          v-model="form.handling"
          placeholder="治疗方案与注意事项"
          placeholder-class="ph"
        />
      </view>

      <!-- 快捷开单 -->
      <view class="quick">
        <view class="quick-item" @tap="gotoPrescription">
          <text>开立处方</text>
        </view>
        <view class="quick-item" @tap="openApply(2)">
          <text>开检查</text>
        </view>
        <view class="quick-item" @tap="openApply(4)">
          <text>开处置</text>
        </view>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="btn-ghost flex1" @tap="save(false)">保存病历</view>
      <view class="btn-primary flex1" @tap="save(true)">诊 毕</view>
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
      patient: {},
      diseases: [],
      form: {
        readme: '',
        present: '',
        history: '',
        physique: '',
        allergy: '',
        diagnosis: '',
        handling: '',
        diseaseIds: []
      }
    }
  },
  computed: {
    store() {
      return useUserStore()
    },
    recordAllergy() {
      return this.form.allergy
    },
    arrowIcon() {
      return iconUrl('arrowRight')
    }
  },
  async onLoad(opts) {
    this.store.sync()
    this.registId = Number(opts.registId || 0)
    if (!this.registId) {
      uni.showToast({ title: '缺少就诊参数', icon: 'none' })
      return
    }
    await this.load()
  },
  methods: {
    async load() {
      try {
        const data = await doctorApi.medicalRecord(this.registId)
        this.patient = data.register || {}
        const mr = data.record || {}
        this.form.readme = mr.readme || ''
        this.form.present = mr.present || ''
        this.form.history = mr.history || ''
        this.form.physique = mr.physique || ''
        this.form.allergy = mr.allergy || ''
        this.form.diagnosis = mr.diagnosis || ''
        this.form.handling = mr.handling || ''
        this.diseases = data.diseases || []
      } catch (e) {}
    },
    showAllergy() {
      uni.showModal({
        title: '过敏史',
        content: this.form.allergy || '无记录',
        showCancel: false
      })
    },
    async pickDisease() {
      let list = this.diseases
      if (!list.length) {
        try {
          list = await doctorApi.diseases()
          this.diseases = list
        } catch (e) {
          return
        }
      }
      if (!list.length) return uni.showToast({ title: '诊断库为空', icon: 'none' })
      uni.showActionSheet({
        itemList: list.slice(0, 6).map((d) => `${d.diseaseName} (${d.diseaseCode || ''})`),
        success: (res) => {
          const d = list[res.tapIndex]
          this.form.diagnosis = d.diseaseName
          this.form.diseaseIds = [d.id]
        }
      })
    },
    gotoPrescription() {
      uni.navigateTo({ url: `/pages/doc/prescription?registId=${this.registId}` })
    },
    async openApply(recordType) {
      try {
        const items = await doctorApi.fmedItems({ recordType })
        if (!items.length) return uni.showToast({ title: '无可申请项目', icon: 'none' })
        uni.showActionSheet({
          itemList: items.slice(0, 6).map((i) => `${i.itemName}  ¥${i.price}`),
          success: async (res) => {
            const picked = items[res.tapIndex]
            await doctorApi.createCheckApply({
              registId: this.registId,
              doctorId: this.store.userId,
              items: [{ itemId: picked.id, num: 1 }]
            })
            uni.showToast({ title: '申请已提交', icon: 'none' })
          }
        })
      } catch (e) {}
    },
    async save(finish) {
      if (!this.form.readme) return uni.showToast({ title: '请填写主诉', icon: 'none' })
      if (!this.form.diagnosis) return uni.showToast({ title: '请选择初步诊断', icon: 'none' })
      try {
        await doctorApi.saveMedicalRecord({
          registId: this.registId,
          doctorId: this.store.userId,
          ...this.form,
          caseState: finish ? 3 : 2,
          finish
        })
        uni.showToast({ title: finish ? '已诊毕' : '病历已保存', icon: 'none' })
        if (finish) setTimeout(() => uni.navigateBack(), 600)
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

.p-name {
  font-size: $font-lg;
  font-weight: 600;
  color: $color-text;
}

.p-sub {
  font-size: $font-sm;
  color: $color-text-sub;
}

.alert {
  display: flex;
  align-items: center;
  padding: 14rpx 18rpx;
  border-radius: $radius-sm;
  background-color: $color-danger-tint;
}

.alert-text {
  font-size: $font-sm;
  color: $color-danger;
}

/* ---------- 表单 ---------- */
.field {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.field-label {
  font-size: $font-sm;
  color: $color-text-sub;
}

.textarea {
  width: 100%;
  height: 150rpx;
  padding: 20rpx;
  box-sizing: border-box;
  background-color: $color-fill;
  border: 2rpx solid transparent;
  border-radius: $radius-input;
  font-size: $font-md;
  color: $color-text;
  line-height: 1.6;
}

.textarea.small {
  height: 112rpx;
}

/* ---------- 诊断 ---------- */
.diag {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  height: 88rpx;
  padding: 0 24rpx;
  background-color: $color-fill;
  border-radius: $radius-input;
}

.diag-text {
  font-size: $font-md;
  color: $color-text;
}

.diag-arrow {
  width: 32rpx;
  height: 32rpx;
  flex: none;
}

/* ---------- 快捷开单 ---------- */
.quick {
  display: flex;
  flex-direction: row;
  gap: 18rpx;
}

.quick-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  background-color: $color-card;
  border: 2rpx solid $color-primary-line;
  border-radius: $radius-sm;
  font-size: $font-sm;
  font-weight: 600;
  color: $color-primary-text;
}

.quick-item:active {
  background-color: $color-primary-tint;
}
</style>
