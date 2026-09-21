/**
 * 用户登录态
 */
import { defineStore } from 'pinia'
import { authApi } from '@/api'
import { STORAGE_TOKEN, STORAGE_USER, homeOf } from '@/api/config'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: uni.getStorageSync(STORAGE_TOKEN) || '',
    user: uni.getStorageSync(STORAGE_USER) || null
  }),

  getters: {
    /** 是否已登录 */
    logged: (s) => !!s.token,
    /** 真实姓名 */
    realName: (s) => (s.user ? s.user.realName : ''),
    /** 角色类别 1-6 */
    useType: (s) => (s.user ? Number(s.user.useType) : 0),
    /** 角色名 */
    roleName: (s) => (s.user ? s.user.useTypeName : ''),
    /** 科室名 */
    deptName: (s) => (s.user ? s.user.deptName || '' : ''),
    /** 用户 ID */
    userId: (s) => (s.user ? s.user.id : 0),
    /** 首页路径 */
    home: (s) => homeOf(s.user ? s.user.useType : 0)
  },

  actions: {
    /** 从本地缓存同步登录态（页面 onShow 时调用，避免拿到过期的 store 快照） */
    sync() {
      const token = uni.getStorageSync(STORAGE_TOKEN) || ''
      const user = uni.getStorageSync(STORAGE_USER) || null
      if (token !== this.token) this.token = token
      if (user !== this.user) this.user = user
      return this.user
    },

    async login(userName, password) {
      const data = await authApi.login(userName, password)
      this.token = data.token
      this.user = data.user
      uni.setStorageSync(STORAGE_TOKEN, data.token)
      uni.setStorageSync(STORAGE_USER, data.user)
      return data.user
    },

    logout() {
      this.token = ''
      this.user = null
      uni.removeStorageSync(STORAGE_TOKEN)
      uni.removeStorageSync(STORAGE_USER)
      uni.reLaunch({ url: '/pages/login/index' })
    }
  }
})
