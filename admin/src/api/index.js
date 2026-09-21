import http from './request'

export const authApi = {
  login: (userName, password) => http.post('/api/auth/login', { userName, password }),
  roles: () => http.get('/api/auth/roles'),
  users: (params) => http.get('/api/auth/users', { params }),
  addUser: (data) => http.post('/api/auth/users', data),
  updateUser: (data) => http.put('/api/auth/users', data),
  toggleUser: (id, enabled) => http.post(`/api/auth/users/${id}/toggle`, null, { params: { enabled } }),
  resetPassword: (id) => http.post(`/api/auth/users/${id}/reset-password`)
}

export const systemApi = {
  departments: () => http.get('/api/system/departments'),
  saveDepartment: (data) => http.post('/api/system/departments', data),
  deleteDepartment: (id) => http.delete(`/api/system/departments/${id}`),

  constantTypes: () => http.get('/api/system/constant-types'),
  constantItems: (params) => http.get('/api/system/constant-items', { params }),

  registLevels: () => http.get('/api/system/regist-levels'),
  saveRegistLevel: (data) => http.post('/api/system/regist-levels', data),
  deleteRegistLevel: (id) => http.delete(`/api/system/regist-levels/${id}`),

  settleCategories: () => http.get('/api/system/settle-categories'),
  saveSettleCategory: (data) => http.post('/api/system/settle-categories', data),
  deleteSettleCategory: (id) => http.delete(`/api/system/settle-categories/${id}`),

  diseases: (params) => http.get('/api/system/diseases', { params }),
  saveDisease: (data) => http.post('/api/system/diseases', data),
  deleteDisease: (id) => http.delete(`/api/system/diseases/${id}`),
  diseCategories: () => http.get('/api/system/dise-categories'),

  expenseClasses: () => http.get('/api/system/expense-classes'),
  fmedItems: (params) => http.get('/api/system/fmed-items', { params }),
  saveFmedItem: (data) => http.post('/api/system/fmed-items', data),
  deleteFmedItem: (id) => http.delete(`/api/system/fmed-items/${id}`),

  scheduling: (params) => http.get('/api/system/scheduling', { params }),
  saveScheduling: (data) => http.post('/api/system/scheduling', data),
  deleteScheduling: (id) => http.delete(`/api/system/scheduling/${id}`)
}

export const registerApi = {
  overview: () => http.get('/api/register/overview'),
  list: (params) => http.get('/api/register/list', { params }),
  refund: (id) => http.post(`/api/register/${id}/refund`)
}

export const pharmacyApi = {
  drugs: (params) => http.get('/api/pharmacy/drugs', { params }),
  saveDrug: (data) => http.post('/api/pharmacy/drugs', data),
  deleteDrug: (id) => http.delete(`/api/pharmacy/drugs/${id}`),
  warnings: () => http.get('/api/pharmacy/warnings')
}

export const financeApi = {
  revenue: (params) => http.get('/api/finance/revenue', { params }),
  doctorWorkload: (params) => http.get('/api/finance/doctor-workload', { params })
}

export const techApi = {
  applies: (params) => http.get('/api/tech/applies', { params })
}
