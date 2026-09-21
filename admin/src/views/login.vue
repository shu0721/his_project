<template>
  <div class="gate">
    <!-- 左：品牌与说明 -->
    <section class="intro">
      <div class="intro-brand">
        <span class="intro-mark" aria-hidden="true">HIS</span>
        <span class="intro-name">南华大学附属医院 HIS</span>
      </div>

      <div class="intro-copy">
        <h1>医院信息管理控制台</h1>
        <p>
          面向挂号收费、门诊诊疗、医技执行、药房发放与财务结算的全流程协作平台。
          基础字典、排班号源、药品库存与营收统计集中维护。
        </p>
      </div>

      <ul class="intro-points">
        <li v-for="p in points" :key="p.title">
          <el-icon class="intro-point-icon"><component :is="p.icon" /></el-icon>
          <span>
            <b>{{ p.title }}</b>
            <i>{{ p.desc }}</i>
          </span>
        </li>
      </ul>

      <div class="intro-foot">门诊业务主线 · 实训演示环境</div>
    </section>

    <!-- 右：登录表单 -->
    <section class="form-side">
      <div class="form-box">
        <h2>账号登录</h2>
        <p class="form-hint">请使用医院分配的账号登录，演示账号见下方</p>

        <form @submit.prevent="onSubmit">
          <label class="field">
            <span class="field-label">登录账号</span>
            <el-input v-model="form.userName" size="large" placeholder="请输入登录账号" autocomplete="username" />
          </label>

          <label class="field">
            <span class="field-label">登录密码</span>
            <el-input
              v-model="form.password"
              type="password"
              size="large"
              placeholder="请输入登录密码"
              show-password
              autocomplete="current-password"
              @keyup.enter="onSubmit"
            />
          </label>

          <el-button type="primary" size="large" class="submit" :loading="loading" @click="onSubmit">
            登 录
          </el-button>
        </form>

        <div class="demo">
          <div class="demo-head">
            <span>演示账号</span>
            <i>统一密码 123456</i>
          </div>
          <div class="demo-grid">
            <button
              v-for="a in accounts"
              :key="a.id"
              type="button"
              class="demo-item"
              :class="{ 'is-on': form.userName === a.id }"
              @click="pick(a.id)"
            >
              <b>{{ a.role }}</b>
              <i>{{ a.id }}</i>
            </button>
          </div>
        </div>
      </div>

      <p class="copyright">© 2026 南华大学附属医院 HIS · 仅用于教学实训</p>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const store = useUserStore()

const form = reactive({ userName: 'admin', password: '123456' })
const loading = ref(false)

const points = [
  { title: '业务数据集中维护', desc: '科室、项目、诊断、药品与结算字典', icon: 'Collection' },
  { title: '门诊流程可追溯', desc: '挂号流水、就诊状态与退号处理', icon: 'Tickets' },
  { title: '运营指标即时可查', desc: '营收构成与医生工作量统计', icon: 'TrendCharts' }
]

const accounts = [
  { id: 'admin', role: '医院管理员' },
  { id: 'reg01', role: '挂号收费员' },
  { id: 'chenjx', role: '门诊医生' },
  { id: 'wangxm', role: '医技医生' },
  { id: 'pharm01', role: '药房操作员' },
  { id: 'fin01', role: '财务管理员' }
]

function pick(id) {
  form.userName = id
  form.password = '123456'
}

async function onSubmit() {
  if (!form.userName || !form.password) {
    ElMessage.warning('请输入账号与密码')
    return
  }
  loading.value = true
  try {
    const user = await store.login(form.userName, form.password)
    ElMessage.success(`欢迎，${user.realName}`)
    router.push('/dashboard')
  } catch (e) {
    /* 错误提示已在请求层统一处理 */
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.gate {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(400px, 0.95fr);
  min-height: 100vh;
  background: var(--his-surface);
}

/* ---------- 左栏 ---------- */
.intro {
  display: flex;
  flex-direction: column;
  padding: 40px 48px;
  background: var(--his-primary-tint);
  border-right: 1px solid var(--his-primary-line);
}

.intro-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.intro-mark {
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
}

.intro-name {
  font-size: var(--his-font-base);
  font-weight: 600;
  color: var(--his-primary-text);
}

.intro-copy {
  margin-top: auto;
  max-width: 46ch;
}

.intro-copy h1 {
  margin: 0;
  font-size: var(--his-font-3xl);
  font-weight: 600;
  letter-spacing: -0.02em;
  color: #1a3524;
  line-height: 1.3;
}

.intro-copy p {
  margin: 12px 0 0 0;
  font-size: var(--his-font-base);
  line-height: 1.75;
  color: #3e6049;
}

.intro-points {
  margin: 28px 0 0 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.intro-points li {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.intro-point-icon {
  margin-top: 2px;
  font-size: var(--his-font-md);
  color: var(--his-primary);
  flex: none;
}

.intro-points span {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.intro-points b {
  font-size: var(--his-font-base);
  font-weight: 600;
  color: #22402c;
}

.intro-points i {
  font-size: var(--his-font-sm);
  font-style: normal;
  color: #52735c;
}

.intro-foot {
  margin-top: auto;
  padding-top: 32px;
  font-size: var(--his-font-xs);
  color: #5f8069;
  letter-spacing: 0.02em;
}

/* ---------- 右栏 ---------- */
.form-side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px 32px;
}

.form-box {
  width: 100%;
  max-width: 360px;
}

.form-box h2 {
  margin: 0;
  font-size: var(--his-font-xl);
  font-weight: 600;
  color: var(--his-text);
  letter-spacing: -0.01em;
}

.form-hint {
  margin: 6px 0 24px 0;
  font-size: var(--his-font-sm);
  color: var(--his-text-sub);
}

.field {
  display: block;
  margin-bottom: 16px;
}

.field-label {
  display: block;
  margin-bottom: 6px;
  font-size: var(--his-font-sm);
  color: var(--his-text-sub);
}

.submit {
  width: 100%;
  margin-top: 4px;
}

.demo {
  margin-top: 28px;
  padding-top: 18px;
  border-top: 1px solid var(--his-border);
}

.demo-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: var(--his-font-sm);
  color: var(--his-text-sub);
}

.demo-head i {
  font-style: normal;
  color: var(--his-text-muted);
}

.demo-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.demo-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
  padding: 8px 10px;
  border: 1px solid var(--his-border);
  border-radius: var(--his-radius);
  background: var(--his-surface);
  cursor: pointer;
  text-align: left;
  font-family: inherit;
  transition: border-color 160ms ease, background-color 160ms ease;
}

.demo-item:hover {
  border-color: var(--his-border-strong);
  background: var(--his-hover);
}

.demo-item.is-on {
  border-color: var(--his-primary-line);
  background: var(--his-primary-tint);
}

.demo-item b {
  font-size: var(--his-font-sm);
  font-weight: 600;
  color: var(--his-text);
}

.demo-item i {
  font-size: var(--his-font-xs);
  font-style: normal;
  color: var(--his-text-muted);
  font-family: 'SF Mono', Consolas, monospace;
}

.copyright {
  margin: 28px 0 0 0;
  font-size: var(--his-font-xs);
  color: var(--his-text-muted);
}

@media (max-width: 900px) {
  .gate {
    grid-template-columns: 1fr;
  }

  .intro {
    display: none;
  }
}
</style>
