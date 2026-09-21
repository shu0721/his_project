/**
 * 统一请求封装
 * 后端返回体约定：{ result: boolean, errMsg?: string, data?: T, token?: string }
 */
import { BASE_URL, STORAGE_TOKEN, STORAGE_USER } from './config'

function getToken() {
  try {
    return uni.getStorageSync(STORAGE_TOKEN) || ''
  } catch (e) {
    return ''
  }
}

function buildUrl(url) {
  if (/^https?:\/\//.test(url)) return url
  return BASE_URL + url
}

function toast(msg) {
  uni.showToast({ title: msg || '请求失败', icon: 'none', duration: 2000 })
}

/**
 * 发起请求
 * @param {Object} options
 * @param {string} options.url    接口路径，如 /api/auth/login
 * @param {string} [options.method='GET']
 * @param {Object} [options.data]
 * @param {boolean} [options.silent] 为 true 时不自动弹错误提示
 * @param {boolean} [options.loading] 为 true 时显示 loading
 */
export function request(options) {
  const { url, method = 'GET', data, silent = false, loading = false } = options

  if (loading) uni.showLoading({ title: '处理中', mask: true })

  return new Promise((resolve, reject) => {
    uni.request({
      url: buildUrl(url),
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        Authorization: getToken()
      },
      success: (res) => {
        const body = res.data || {}
        if (res.statusCode === 200 && body.result) {
          resolve(body.data)
          return
        }
        if (res.statusCode === 401) {
          uni.removeStorageSync(STORAGE_TOKEN)
          uni.removeStorageSync(STORAGE_USER)
          if (!silent) toast('登录已过期，请重新登录')
          setTimeout(() => uni.reLaunch({ url: '/pages/login/index' }), 800)
          reject(new Error('unauthorized'))
          return
        }
        const msg = body.errMsg || `请求失败（${res.statusCode}）`
        if (!silent) toast(msg)
        reject(new Error(msg))
      },
      fail: (err) => {
        if (!silent) toast('网络异常，请确认后端已启动')
        reject(err)
      },
      complete: () => {
        if (loading) uni.hideLoading()
      }
    })
  })
}

export const get = (url, data, opts = {}) => request({ url, method: 'GET', data, ...opts })
export const post = (url, data, opts = {}) => request({ url, method: 'POST', data, ...opts })
export const put = (url, data, opts = {}) => request({ url, method: 'PUT', data, ...opts })
export const del = (url, data, opts = {}) => request({ url, method: 'DELETE', data, ...opts })

export default request
