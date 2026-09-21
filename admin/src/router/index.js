import { createRouter, createWebHashHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  { path: '/login', component: () => import('@/views/login.vue'), meta: { public: true } },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/dashboard.vue'), meta: { title: '工作台', icon: 'Odometer' } },

      { path: 'system/user', component: () => import('@/views/system/user.vue'), meta: { title: '用户管理', icon: 'User' } },
      { path: 'system/scheduling', component: () => import('@/views/system/scheduling.vue'), meta: { title: '医生排班', icon: 'Calendar' } },
      { path: 'system/department', component: () => import('@/views/system/department.vue'), meta: { title: '科室管理', icon: 'OfficeBuilding' } },
      { path: 'system/constant', component: () => import('@/views/system/constant.vue'), meta: { title: '常数类别', icon: 'Collection' } },
      { path: 'system/regist-level', component: () => import('@/views/system/registLevel.vue'), meta: { title: '挂号级别', icon: 'Ticket' } },
      { path: 'system/settle-category', component: () => import('@/views/system/settleCategory.vue'), meta: { title: '结算类别', icon: 'Wallet' } },
      { path: 'system/disease', component: () => import('@/views/system/disease.vue'), meta: { title: '诊断目录', icon: 'FirstAidKit' } },
      { path: 'system/fmed-item', component: () => import('@/views/system/fmedItem.vue'), meta: { title: '非药品收费项目', icon: 'PriceTag' } },

      { path: 'biz/drugs', component: () => import('@/views/biz/drugs.vue'), meta: { title: '药品目录', icon: 'Box' } },
      { path: 'biz/register', component: () => import('@/views/biz/register.vue'), meta: { title: '挂号记录', icon: 'Tickets' } },
      { path: 'biz/finance', component: () => import('@/views/biz/finance.vue'), meta: { title: '营收统计', icon: 'TrendCharts' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('his_admin_token')
  if (to.meta.public) return next()
  if (!token) return next('/login')
  next()
})

export default router
