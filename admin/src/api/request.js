import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: '/',
  timeout: 15000
})

/** 请求拦截：附加 JWT */
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('his_admin_token')
  if (token) config.headers.Authorization = token
  return config
})

/** 响应拦截：拆解统一返回体 */
http.interceptors.response.use(
  (res) => {
    const body = res.data || {}
    if (body.result) return body.data
    ElMessage.error(body.errMsg || '请求失败')
    return Promise.reject(new Error(body.errMsg || '请求失败'))
  },
  (err) => {
    const status = err.response && err.response.status
    if (status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('his_admin_token')
      localStorage.removeItem('his_admin_user')
      setTimeout(() => (window.location.hash = '#/login'), 600)
    } else {
      ElMessage.error((err.response && err.response.data && err.response.data.errMsg) || '网络异常')
    }
    return Promise.reject(err)
  }
)

export default http
