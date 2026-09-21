<template>
  <view class="page-root">
    <StatusBar />
    <NavBar title="系统管理" />

    <view class="page-body tabbed">
      <!-- 基础数据概览 -->
      <view class="summary">
        <view class="summary-head">
          <text class="t-sm t-sub">基础数据总览</text>
          <text class="t-xs t-muted">来自实时业务库</text>
        </view>
        <view class="summary-foot no-border">
          <view class="summary-item">
            <text class="summary-item-value num">{{ counts.department }}</text>
            <text class="summary-item-label">科室</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ counts.drugs }}</text>
            <text class="summary-item-label">药品</text>
          </view>
          <view class="summary-item">
            <text class="summary-item-value num">{{ counts.disease }}</text>
            <text class="summary-item-label">诊断</text>
          </view>
        </view>
      </view>

      <!-- 字典入口 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">基础字典</text>
          <text class="card-meta">{{ dictMenus.length }} 类</text>
        </view>
        <view v-for="m in dictMenus" :key="m.key" class="entry" @tap="openDict(m)">
          <view class="col">
            <text class="entry-name">{{ m.name }}</text>
            <text class="entry-desc">{{ m.desc }}</text>
          </view>
          <image class="entry-arrow" :src="arrowIcon" />
        </view>
      </view>

      <!-- 系统设置 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">系统设置</text>
        </view>
        <view v-for="m in sysMenus" :key="m.key" class="entry" @tap="openSys(m)">
          <view class="col">
            <text class="entry-name">{{ m.name }}</text>
            <text class="entry-desc">{{ m.desc }}</text>
          </view>
          <image class="entry-arrow" :src="arrowIcon" />
        </view>
      </view>

      <!-- 当前账号 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">当前账号</text>
        </view>
        <view class="account">
          <view class="col">
            <text class="acc-name">{{ realName }}</text>
            <text class="acc-sub">{{ roleName }} · {{ user.userName }}</text>
          </view>
          <view class="logout" @tap="logout">退出登录</view>
        </view>
      </view>
    </view>

    <AppTabBar :current="2" :items="tabs" />
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import NavBar from '@/components/NavBar.vue'
import AppTabBar from '@/components/AppTabBar.vue'
import { systemApi, pharmacyApi, authApi } from '@/api'
import { useUserStore } from '@/store/user'
import { iconUrl } from '@/utils/icons'

export default {
  components: { StatusBar, NavBar, AppTabBar },
  data() {
    return {
      counts: { department: 0, drugs: 0, disease: 0 },
      dictMenus: [
        { key: 'department', name: '科室管理', desc: '门诊、医技与职能科室字典' },
        { key: 'regist', name: '挂号级别', desc: '普通号、专家号及挂号费' },
        { key: 'settle', name: '结算类别', desc: '自费、医保与商保结算方式' },
        { key: 'fmed', name: '非药品收费项目', desc: '检验、检查与处置项目定价' },
        { key: 'disease', name: '诊断目录', desc: 'ICD-10 诊断库维护' }
      ],
      sysMenus: [
        { key: 'users', name: '用户管理', desc: '账号、角色与所属科室' },
        { key: 'scheduling', name: '医生排班', desc: '出诊日期、午别与号源' },
        { key: 'constant', name: '常数类别', desc: '系统常数与字典项' }
      ],
      tabs: [
        { text: '工作台', icon: 'grid' },
        { text: '用户', icon: 'user' },
        { text: '设置', icon: 'gear' }
      ]
    }
  },
  computed: {
    store() {
      return useUserStore()
    },
    user() {
      return this.store.user || {}
    },
    realName() {
      return this.store.realName
    },
    roleName() {
      return this.store.roleName
    },
    arrowIcon() {
      return iconUrl('arrowRight')
    }
  },
  onShow() {
    this.store.sync()
    this.loadCounts()
  },
  methods: {
    async loadCounts() {
      try {
        const [depts, drugs, diseases] = await Promise.all([
          systemApi.departments().catch(() => []),
          pharmacyApi.drugs().catch(() => []),
          systemApi.diseases().catch(() => [])
        ])
        this.counts = {
          department: depts.length,
          drugs: drugs.length,
          disease: diseases.length
        }
      } catch (e) {}
    },
    /** 以列表弹窗展示基础数据，完整维护在管理后台 */
    async show(title, loader) {
      try {
        const list = await loader()
        if (!list || !list.length) return uni.showToast({ title: `${title}暂无数据`, icon: 'none' })
        uni.showModal({
          title: `${title}（${list.length}）`,
          content: list.slice(0, 8).join('\n'),
          showCancel: false
        })
      } catch (e) {}
    },
    openDict(m) {
      const map = {
        department: () => this.show('科室管理', () => systemApi.departments().then((l) => l.map((x) => x.deptName))),
        regist: () =>
          this.show('挂号级别', () =>
            systemApi.registLevels().then((l) => l.map((x) => `${x.registName} ¥${Number(x.registFee).toFixed(2)}`))
          ),
        settle: () => this.show('结算类别', () => systemApi.settleCategories().then((l) => l.map((x) => x.settleName))),
        fmed: () =>
          this.show('非药品收费项目', () =>
            systemApi.fmedItems({}).then((l) => l.map((x) => `${x.itemName} ¥${Number(x.price).toFixed(2)}`))
          ),
        disease: () =>
          this.show('诊断目录', () =>
            systemApi.diseases().then((l) => l.map((x) => `${x.diseaseCode || ''} ${x.diseaseName}`))
          )
      }
      map[m.key]()
    },
    openSys(m) {
      if (m.key === 'users') {
        return this.show('用户管理', () => authApi.users({}).then((l) => l.map((u) => `${u.userName} · ${u.realName}`)))
      }
      if (m.key === 'scheduling') {
        return this.show('医生排班', () =>
          systemApi
            .scheduling({})
            .then((l) =>
              l.map((s) => `${String(s.schedDate).slice(0, 10)} ${s.deptName || ''} ${s.doctorName || s.realName || ''} ${s.noon || ''}`)
            )
        )
      }
      if (m.key === 'constant') {
        return this.show('常数类别', () => systemApi.constantTypes().then((l) => l.map((x) => x.constantTypeName)))
      }
      uni.showToast({ title: `${m.name} 请前往管理后台`, icon: 'none' })
    },
    logout() {
      uni.showModal({
        title: '退出登录',
        content: '确认退出当前账号？',
        success: (res) => {
          if (res.confirm) useUserStore().logout()
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.page-body.tabbed {
  padding-bottom: 200rpx;
}

.summary-foot.no-border {
  border-top: none;
  padding-top: 0;
}

.entry {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 20rpx 0;
  border-top: 2rpx solid $color-divider;
}

.entry-name {
  font-size: $font-md;
  color: $color-text;
}

.entry-desc {
  font-size: $font-xs;
  color: $color-text-sub;
}

.entry-arrow {
  width: 32rpx;
  height: 32rpx;
  flex: none;
}

.account {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding-top: 14rpx;
  border-top: 2rpx solid $color-divider;
}

.acc-name {
  font-size: 32rpx;
  font-weight: 600;
  color: $color-text;
}

.acc-sub {
  font-size: $font-sm;
  color: $color-text-sub;
}

.logout {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64rpx;
  padding: 0 30rpx;
  border-radius: $radius-sm;
  background-color: $color-card;
  border: 2rpx solid #eec9c7;
  color: $color-danger;
  font-size: $font-sm;
  font-weight: 600;
  flex: none;
}

.logout:active {
  background-color: $color-danger-tint;
}
</style>
