<template>
  <div class="shell">
    <!-- 侧栏 -->
    <aside class="rail">
      <div class="rail-brand">
        <span class="rail-mark" aria-hidden="true">HIS</span>
        <span class="rail-name">
          <b>南华大学附属医院 HIS</b>
          <i>管理控制台</i>
        </span>
      </div>

      <nav class="rail-nav">
        <template v-for="group in groups" :key="group.title">
          <div v-if="group.title" class="rail-group">{{ group.title }}</div>
          <router-link
            v-for="m in group.items"
            :key="m.path"
            :to="m.path"
            class="rail-item"
            :class="{ 'is-active': route.path === m.path }"
          >
            <el-icon class="rail-icon"><component :is="m.icon" /></el-icon>
            <span>{{ m.title }}</span>
          </router-link>
        </template>
      </nav>

      <div class="rail-foot">
        <span class="rail-dot" :class="online ? 'ok' : 'off'"></span>
        <span>{{ online ? '服务已连接' : '服务未连接' }}</span>
      </div>
    </aside>

    <!-- 主体 -->
    <div class="body">
      <header class="bar">
        <div class="bar-crumb">
          <span class="bar-crumb-root">管理后台</span>
          <el-icon class="bar-crumb-sep"><ArrowRight /></el-icon>
          <span class="bar-crumb-cur">{{ currentTitle }}</span>
        </div>

        <div class="bar-right">
          <span class="bar-clock num">{{ clock }}</span>
          <span class="bar-split" aria-hidden="true"></span>
          <span class="bar-user">
            <b>{{ realName || '未登录' }}</b>
            <i>{{ roleName }}</i>
          </span>
          <button type="button" class="bar-exit" @click="logout">退出</button>
        </div>
      </header>

      <main class="canvas">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const store = useUserStore()

const groups = [
  {
    title: '',
    items: [{ path: '/dashboard', title: '运营工作台', icon: 'Odometer' }]
  },
  {
    title: '业务数据',
    items: [
      { path: '/biz/register', title: '挂号记录', icon: 'Tickets' },
      { path: '/biz/drugs', title: '药品目录', icon: 'Box' },
      { path: '/biz/finance', title: '营收统计', icon: 'TrendCharts' }
    ]
  },
  {
    title: '基础字典',
    items: [
      { path: '/system/department', title: '科室管理', icon: 'OfficeBuilding' },
      { path: '/system/regist-level', title: '挂号级别', icon: 'Ticket' },
      { path: '/system/settle-category', title: '结算类别', icon: 'Wallet' },
      { path: '/system/fmed-item', title: '非药品项目', icon: 'PriceTag' },
      { path: '/system/disease', title: '诊断目录', icon: 'FirstAidKit' },
      { path: '/system/constant', title: '常数类别', icon: 'Collection' }
    ]
  },
  {
    title: '系统设置',
    items: [
      { path: '/system/user', title: '用户管理', icon: 'User' },
      { path: '/system/scheduling', title: '医生排班', icon: 'Calendar' }
    ]
  }
]

const currentTitle = computed(() => route.meta.title || '运营工作台')
const realName = computed(() => store.realName)
const roleName = computed(() => store.roleName)

const online = ref(true)
const clock = ref('')

let timer = null

function tick() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  clock.value = `${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

onMounted(() => {
  tick()
  timer = setInterval(tick, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function logout() {
  store.logout()
  router.push('/login')
}
</script>

<style scoped>
.shell {
  display: flex;
  height: 100vh;
  background: var(--his-canvas);
}

/* ---------- 侧栏 ---------- */
.rail {
  width: 244px;
  flex: none;
  display: flex;
  flex-direction: column;
  background: var(--his-surface);
  border-right: 1px solid var(--his-border);
}

.rail-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 58px;
  padding: 0 16px;
  border-bottom: 1px solid var(--his-border);
}

.rail-mark {
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  border-radius: var(--his-radius);
  background: var(--his-primary);
  color: var(--his-text-invert);
  font-size: var(--his-font-2xs);
  font-weight: 700;
  letter-spacing: 0.06em;
  flex: none;
}

.rail-name {
  display: flex;
  flex-direction: column;
  min-width: 0;
  line-height: 1.25;
}

.rail-name b {
  font-size: var(--his-font-sm);
  font-weight: 600;
  color: var(--his-text);
  white-space: nowrap;
}

.rail-name i {
  font-size: var(--his-font-xs);
  font-style: normal;
  color: var(--his-text-muted);
}

.rail-nav {
  flex: 1;
  overflow-y: auto;
  padding: 10px 8px 16px 8px;
}

.rail-group {
  padding: 14px 8px 6px 8px;
  font-size: var(--his-font-xs);
  font-weight: 600;
  color: var(--his-text-muted);
  letter-spacing: 0.04em;
}

.rail-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 9px;
  height: 34px;
  padding: 0 9px;
  border-radius: var(--his-radius);
  font-size: var(--his-font-base);
  color: var(--his-text-sub);
  text-decoration: none;
  transition: background-color 160ms ease, color 160ms ease;
}

.rail-item:hover {
  background: var(--his-hover);
  color: var(--his-text);
}

.rail-item.is-active {
  background: var(--his-primary-tint);
  color: var(--his-primary-text);
  font-weight: 600;
}

.rail-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 2px;
  height: 16px;
  border-radius: 0 2px 2px 0;
  background: var(--his-primary);
}

.rail-icon {
  font-size: var(--his-font-md);
  flex: none;
}

.rail-foot {
  display: flex;
  align-items: center;
  gap: 7px;
  height: 38px;
  padding: 0 16px;
  border-top: 1px solid var(--his-border);
  font-size: var(--his-font-xs);
  color: var(--his-text-muted);
}

.rail-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex: none;
}

.rail-dot.ok {
  background: var(--his-success);
}

.rail-dot.off {
  background: var(--his-danger);
}

/* ---------- 顶栏 ---------- */
.body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.bar {
  height: 58px;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: var(--his-surface);
  border-bottom: 1px solid var(--his-border);
}

.bar-crumb {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: var(--his-font-base);
  min-width: 0;
}

.bar-crumb-root {
  color: var(--his-text-muted);
}

.bar-crumb-sep {
  font-size: var(--his-font-xs);
  color: var(--his-text-muted);
}

.bar-crumb-cur {
  font-weight: 600;
  color: var(--his-text);
}

.bar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-clock {
  font-size: var(--his-font-sm);
  color: var(--his-text-muted);
}

.bar-split {
  width: 1px;
  height: 18px;
  background: var(--his-border);
}

.bar-user {
  display: flex;
  align-items: baseline;
  gap: 7px;
  font-size: var(--his-font-sm);
}

.bar-user b {
  font-weight: 600;
  color: var(--his-text);
}

.bar-user i {
  font-style: normal;
  color: var(--his-text-sub);
}

.bar-exit {
  height: 28px;
  padding: 0 12px;
  border: 1px solid var(--his-border-strong);
  border-radius: var(--his-radius);
  background: transparent;
  color: var(--his-text-sub);
  font-size: var(--his-font-sm);
  font-family: inherit;
  cursor: pointer;
  transition: all 160ms ease;
}

.bar-exit:hover {
  border-color: var(--his-danger);
  color: var(--his-danger);
  background: var(--his-danger-tint);
}

/* ---------- 画布 ---------- */
.canvas {
  flex: 1;
  overflow: auto;
  padding: 18px 20px 28px 20px;
}
</style>
