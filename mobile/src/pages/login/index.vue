<template>
  <view class="login">
    <StatusBar />

    <view class="brand">
      <view class="brand-mark">
        <text class="brand-mark-text">HIS</text>
      </view>
      <text class="brand-name">东软云 HIS</text>
      <text class="brand-sub">门诊业务移动工作台</text>
    </view>

    <view class="panel">
      <view class="panel-head">
        <text class="panel-title">账号登录</text>
        <text class="panel-desc">选择角色可自动填入对应演示账号</text>
      </view>

      <view class="field">
        <text class="field-label">登录账号</text>
        <input
          class="input-box"
          v-model="form.userName"
          placeholder="请输入登录账号"
          placeholder-class="ph"
        />
      </view>

      <view class="field">
        <text class="field-label">登录密码</text>
        <input
          class="input-box"
          v-model="form.password"
          password
          placeholder="请输入登录密码"
          placeholder-class="ph"
        />
      </view>

      <view class="field">
        <text class="field-label">选择角色</text>
        <view class="role-grid">
          <view
            v-for="r in roles"
            :key="r.useType"
            class="role"
            :class="{ on: Number(form.useType) === r.useType }"
            @tap="pickRole(r)"
          >
            <text class="role-name">{{ r.name }}</text>
            <text class="role-acc">{{ r.demoAccount }}</text>
          </view>
        </view>
      </view>

      <view class="btn-primary login-btn" :class="{ 'is-disabled': loading }" @tap="onLogin">
        {{ loading ? '登录中…' : '登 录' }}
      </view>

      <text class="tip">演示账号统一密码 123456</text>
    </view>

    <text class="copyright">© 2026 东软云 HIS · 仅用于教学实训</text>
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import { authApi } from '@/api'
import { ROLES, homeOf } from '@/api/config'
import { useUserStore } from '@/store/user'

export default {
  components: { StatusBar },
  data() {
    return {
      roles: ROLES,
      form: { userName: 'reg01', password: '123456', useType: 2 },
      loading: false
    }
  },
  onLoad() {
    const store = useUserStore()
    if (store.logged) {
      uni.reLaunch({ url: homeOf(store.useType) })
    }
  },
  methods: {
    pickRole(r) {
      this.form.useType = r.useType
      this.form.userName = r.demoAccount
      this.form.password = '123456'
    },
    async onLogin() {
      if (!this.form.userName || !this.form.password) {
        uni.showToast({ title: '请输入账号与密码', icon: 'none' })
        return
      }
      this.loading = true
      try {
        const store = useUserStore()
        const user = await store.login(this.form.userName, this.form.password)
        uni.showToast({ title: `欢迎，${user.realName}`, icon: 'none' })
        setTimeout(() => uni.reLaunch({ url: homeOf(user.useType) }), 500)
      } catch (e) {
        // 错误提示已在请求层统一处理
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.login {
  display: flex;
  flex-direction: column;
  min-height: $page-min-height;
  padding: 0 $page-padding 40rpx $page-padding;
  box-sizing: border-box;
  background-color: $color-bg;
}

/* ---------- 品牌 ---------- */
.brand {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  padding: 56rpx 8rpx 48rpx 8rpx;
}

.brand-mark {
  width: 84rpx;
  height: 84rpx;
  border-radius: $radius-sm;
  background-color: $color-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;
}

.brand-mark-text {
  font-size: 26rpx;
  font-weight: 700;
  color: $color-text-invert;
  letter-spacing: 1rpx;
}

.brand-name {
  font-size: 46rpx;
  font-weight: 600;
  color: $color-text;
  letter-spacing: -0.5rpx;
}

.brand-sub {
  font-size: $font-sm;
  color: $color-text-sub;
}

/* ---------- 表单 ---------- */
.panel {
  display: flex;
  flex-direction: column;
  gap: $gap-md;
  padding: 36rpx;
  background-color: $color-card;
  border: 2rpx solid $color-border;
  border-radius: $radius-card;
}

.panel-head {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  padding-bottom: 8rpx;
}

.panel-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $color-text;
}

.panel-desc {
  font-size: $font-sm;
  color: $color-text-sub;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.field-label {
  font-size: $font-sm;
  color: $color-text-sub;
}

/* ---------- 角色选择 ---------- */
.role-grid {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 12rpx;
}

.role {
  display: flex;
  flex-direction: column;
  gap: 2rpx;
  min-width: 190rpx;
  flex: 1;
  padding: 14rpx 20rpx;
  border-radius: $radius-sm;
  background-color: $color-sunken;
  border: 2rpx solid $color-border;
}

.role.on {
  background-color: $color-primary-tint;
  border-color: $color-primary-line;
}

.role-name {
  font-size: $font-sm;
  color: $color-text-sub;
}

.role.on .role-name {
  color: $color-primary-text;
  font-weight: 600;
}

.role-acc {
  font-size: $font-xs;
  color: $color-text-muted;
  font-family: 'SF Mono', Consolas, monospace;
}

.role.on .role-acc {
  color: $color-primary-text;
}

.login-btn {
  margin-top: 8rpx;
}

.tip {
  font-size: $font-xs;
  color: $color-text-muted;
  text-align: center;
}

.copyright {
  margin-top: auto;
  padding-top: 56rpx;
  text-align: center;
  font-size: $font-xs;
  color: $color-text-muted;
}
</style>
