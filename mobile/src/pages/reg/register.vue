<template>
  <view class="page-root">
    <StatusBar />
    <NavBar title="现场挂号" />

    <view class="page-body has-bar">
      <!-- 患者信息 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">患者信息</text>
          <text class="card-link" @tap="fillDemo">填入示例</text>
        </view>

        <view class="form-row">
          <text class="form-label">姓名</text>
          <input class="input-box flex1" v-model="form.realName" placeholder="请输入患者姓名" placeholder-class="ph" />
        </view>

        <view class="form-row">
          <text class="form-label">性别</text>
          <view class="segment flex1">
            <view class="seg-item" :class="{ on: form.gender === 1 }" @tap="form.gender = 1">
              <text>男</text>
            </view>
            <view class="seg-item" :class="{ on: form.gender === 2 }" @tap="form.gender = 2">
              <text>女</text>
            </view>
          </view>
        </view>

        <view class="form-row">
          <text class="form-label">年龄</text>
          <input
            class="input-box flex1"
            type="number"
            v-model="form.age"
            placeholder="请输入年龄"
            placeholder-class="ph"
          />
        </view>

        <view class="form-row">
          <text class="form-label">证件号</text>
          <input class="input-box flex1" v-model="form.idnumber" placeholder="请输入身份证号" placeholder-class="ph" />
        </view>
      </view>

      <!-- 科室 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">就诊科室</text>
          <text class="card-meta">共 {{ departments.length }} 个门诊科室</text>
        </view>
        <view class="chip-wrap">
          <view
            v-for="d in departments"
            :key="d.id"
            class="pick-chip"
            :class="{ on: form.deptId === d.id }"
            @tap="pickDept(d)"
          >
            <text>{{ d.deptName }}</text>
          </view>
        </view>
      </view>

      <!-- 出诊医生 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">出诊医生</text>
          <view class="segment seg-compact">
            <view class="seg-item" :class="{ on: noon === '上午' }" @tap="setNoon('上午')">
              <text>上午</text>
            </view>
            <view class="seg-item" :class="{ on: noon === '下午' }" @tap="setNoon('下午')">
              <text>下午</text>
            </view>
          </view>
        </view>

        <view v-if="!doctors.length" class="empty"><text>该科室当前午别暂无排班</text></view>

        <view
          v-for="doc in doctors"
          :key="doc.schedulingId"
          class="doctor"
          :class="{ on: form.userId === doc.userId }"
          @tap="pickDoctor(doc)"
        >
          <view class="col">
            <text class="doctor-name">{{ doc.realName }}</text>
            <text class="doctor-sub">{{ doc.registName }} · 挂号费 ¥{{ doc.registFee.toFixed(2) }}</text>
          </view>
          <text class="doctor-quota num">余号 {{ doc.quota - doc.regNum }}</text>
        </view>
      </view>

      <!-- 挂号级别 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">挂号级别</text>
        </view>
        <view class="chip-wrap">
          <view
            v-for="lv in levels"
            :key="lv.id"
            class="pick-chip"
            :class="{ on: form.registLeId === lv.id }"
            @tap="form.registLeId = lv.id"
          >
            <text>{{ lv.registName }} ¥{{ lv.registFee.toFixed(2) }}</text>
          </view>
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
            :class="{ on: form.settleId === s.id }"
            @tap="form.settleId = s.id"
          >
            <text>{{ s.settleName }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部确认 -->
    <view class="bottom-bar">
      <view class="bottom-total">
        <text class="bottom-total-label">挂号费</text>
        <text class="bottom-total-value num">¥{{ fee }}</text>
      </view>
      <view class="btn-primary flex1" :class="{ 'is-disabled': submitting }" @tap="submit">
        {{ submitting ? '提交中…' : '确认挂号' }}
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
      departments: [],
      levels: [],
      settles: [],
      doctors: [],
      noon: '上午',
      submitting: false,
      form: {
        realName: '',
        gender: 1,
        age: '',
        idnumber: '',
        homeAddress: '北京市朝阳区',
        deptId: null,
        userId: null,
        registLeId: null,
        settleId: null
      }
    }
  },
  computed: {
    fee() {
      const lv = this.levels.find((x) => x.id === this.form.registLeId)
      return lv ? lv.registFee.toFixed(2) : '0.00'
    }
  },
  async onLoad() {
    await this.loadBase()
  },
  methods: {
    async loadBase() {
      try {
        const [depts, levels, settles] = await Promise.all([
          registerApi.departments(),
          registerApi.registLevels(),
          registerApi.settleCategories()
        ])
        // 只保留门诊临床科室
        this.departments = depts.filter((d) => d.deptType === 165)
        this.levels = levels
        this.settles = settles
        if (this.departments.length) this.pickDept(this.departments[0])
        if (levels.length) {
          const normal = levels.find((l) => l.registName === '普通号')
          this.form.registLeId = normal ? normal.id : levels[0].id
        }
        if (settles.length) this.form.settleId = settles[0].id
      } catch (e) {}
    },
    async pickDept(d) {
      if (this.form.deptId === d.id) return
      this.form.deptId = d.id
      this.form.userId = null
      await this.loadDoctors()
    },
    async setNoon(n) {
      if (this.noon === n) return
      this.noon = n
      await this.loadDoctors()
    },
    async loadDoctors() {
      if (!this.form.deptId) return
      try {
        this.doctors = await registerApi.doctors({ deptId: this.form.deptId, noon: this.noon })
        if (this.doctors.length) this.pickDoctor(this.doctors[0])
      } catch (e) {
        this.doctors = []
      }
    },
    pickDoctor(doc) {
      this.form.userId = doc.userId
      if (doc.registLeId) this.form.registLeId = doc.registLeId
    },
    fillDemo() {
      this.form.realName = '张伟'
      this.form.gender = 1
      this.form.age = 32
      this.form.idnumber = '110101199401151234'
    },
    async submit() {
      if (!this.form.realName) return uni.showToast({ title: '请填写患者姓名', icon: 'none' })
      if (!this.form.age) return uni.showToast({ title: '请填写年龄', icon: 'none' })
      if (!this.form.deptId) return uni.showToast({ title: '请选择就诊科室', icon: 'none' })
      if (!this.form.userId) return uni.showToast({ title: '请选择出诊医生', icon: 'none' })

      this.submitting = true
      try {
        const data = await registerApi.create({
          ...this.form,
          age: Number(this.form.age),
          noon: this.noon,
          channel: 1
        })
        uni.showModal({
          title: '挂号成功',
          content: `${data.realName} · ${data.deptName}\n就诊号 ${data.caseNumber}\n挂号费 ¥${this.fee}`,
          showCancel: false,
          success: () => uni.navigateBack()
        })
      } catch (e) {
      } finally {
        this.submitting = false
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

.form-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 20rpx;
}

.form-label {
  width: 124rpx;
  flex: none;
  font-size: $font-sm;
  color: $color-text-sub;
}

.seg-compact {
  width: 240rpx;
  flex: none;
}

.seg-compact .seg-item {
  height: 56rpx;
}

.seg-compact .seg-item text {
  font-size: $font-xs;
}

/* ---------- 医生列表 ---------- */
.doctor {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 20rpx;
  border-radius: $radius-sm;
  background-color: $color-sunken;
  border: 2rpx solid $color-border;
}

.doctor.on {
  background-color: $color-primary-tint;
  border-color: $color-primary-line;
}

.doctor-name {
  font-size: 30rpx;
  font-weight: 600;
  color: $color-text;
}

.doctor-sub {
  font-size: $font-sm;
  color: $color-text-sub;
}

.doctor-quota {
  font-size: $font-sm;
  color: $color-primary-text;
  flex: none;
}
</style>
