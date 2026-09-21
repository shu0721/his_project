import { defineStore } from 'pinia'
import { authApi } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('his_admin_token') || '',
    user: JSON.parse(localStorage.getItem('his_admin_user') || 'null')
  }),
  getters: {
    realName: (s) => (s.user ? s.user.realName : ''),
    roleName: (s) => (s.user ? s.user.useTypeName : '')
  },
  actions: {
    async login(userName, password) {
      const data = await authApi.login(userName, password)
      this.token = data.token
      this.user = data.user
      localStorage.setItem('his_admin_token', data.token)
      localStorage.setItem('his_admin_user', JSON.stringify(data.user))
      return data.user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('his_admin_token')
      localStorage.removeItem('his_admin_user')
    }
  }
})
