/** 全局配置 */

// 后端地址：H5 走 vite 代理，小程序需填完整地址
// #ifdef H5
export const BASE_URL = ''
// #endif
// #ifndef H5
export const BASE_URL = 'http://localhost:8081'
// #endif

export const STORAGE_TOKEN = 'his_token'
export const STORAGE_USER = 'his_user'

/** 角色定义（与后端 /api/auth/roles 保持一致） */
export const ROLES = [
  { useType: 1, name: '医院管理员', desc: '系统设置与基础数据维护', demoAccount: 'admin', path: '/pages/system/workbench' },
  { useType: 2, name: '挂号收费员', desc: '现场挂号、收费结算与退费', demoAccount: 'reg01', path: '/pages/reg/workbench' },
  { useType: 3, name: '门诊医生', desc: '接诊、书写病历、开立处方', demoAccount: 'chenjx', path: '/pages/doc/workbench' },
  { useType: 4, name: '医技医生', desc: '检验检查执行与结果录入', demoAccount: 'wangxm', path: '/pages/tech/workbench' },
  { useType: 5, name: '药房操作员', desc: '处方审核发药与药品管理', demoAccount: 'pharm01', path: '/pages/pharm/dispense' },
  { useType: 6, name: '财务管理员', desc: '营收统计与费用科目管理', demoAccount: 'fin01', path: '/pages/finance/workload' }
]

/** 按角色取首页 */
export function homeOf(useType) {
  const r = ROLES.find((x) => x.useType === Number(useType))
  return r ? r.path : '/pages/login/index'
}
