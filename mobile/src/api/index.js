/**
 * 接口定义（与后端 Controller 一一对应）
 */
import { get, post, put, del } from './request'

export const authApi = {
  login: (userName, password) => post('/api/auth/login', { userName, password }),
  roles: () => get('/api/auth/roles'),
  info: (userId) => get(`/api/auth/info/${userId}`),
  users: (params) => get('/api/auth/users', params),
  addUser: (data) => post('/api/auth/users', data),
  updateUser: (data) => put('/api/auth/users', data),
  toggleUser: (id, enabled) => post(`/api/auth/users/${id}/toggle?enabled=${enabled}`),
  resetPassword: (id) => post(`/api/auth/users/${id}/reset-password`),
  changePassword: (data) => post('/api/auth/change-password', data)
}

export const registerApi = {
  overview: () => get('/api/register/overview'),
  departments: (deptType) => get('/api/register/departments', deptType ? { deptType } : {}),
  registLevels: () => get('/api/register/regist-levels'),
  settleCategories: () => get('/api/register/settle-categories'),
  doctors: (params) => get('/api/register/doctors', params),
  list: (params) => get('/api/register/list', params),
  create: (data) => post('/api/register/create', data),
  refund: (id, operId = 2) => post(`/api/register/${id}/refund?operId=${operId}`),
  chargeList: (id) => get(`/api/register/${id}/charge-list`),
  pay: (data) => post('/api/register/pay', data)
}

export const doctorApi = {
  overview: (doctorId) => get('/api/doctor/overview', doctorId ? { doctorId } : {}),
  waitingList: (params) => get('/api/doctor/waiting-list', params),
  accept: (registId) => post(`/api/doctor/${registId}/accept`),
  medicalRecord: (registId) => get(`/api/doctor/medical-record/${registId}`),
  saveMedicalRecord: (data) => post('/api/doctor/medical-record', data),
  drugs: (keyword) => get('/api/doctor/drugs', keyword ? { keyword } : {}),
  fmedItems: (params) => get('/api/doctor/fmeditems', params),
  diseases: (keyword) => get('/api/doctor/diseases', keyword ? { keyword } : {}),
  prescriptions: (params) => get('/api/doctor/prescriptions', params),
  prescriptionDetail: (id) => get(`/api/doctor/prescriptions/${id}`),
  createPrescription: (data) => post('/api/doctor/prescriptions', data),
  deletePrescription: (id) => del(`/api/doctor/prescriptions/${id}`),
  createCheckApply: (data) => post('/api/doctor/check-applies', data)
}

export const pharmacyApi = {
  overview: () => get('/api/pharmacy/overview'),
  prescriptions: (params) => get('/api/pharmacy/prescriptions', params),
  dispense: (id) => post(`/api/pharmacy/prescriptions/${id}/dispense`),
  drugs: (params) => get('/api/pharmacy/drugs', params),
  drug: (id) => get(`/api/pharmacy/drugs/${id}`),
  saveDrug: (data) => post('/api/pharmacy/drugs', data),
  deleteDrug: (id) => del(`/api/pharmacy/drugs/${id}`),
  stockIn: (id, count) => post(`/api/pharmacy/drugs/${id}/stock-in?count=${count}`),
  warnings: (days) => get('/api/pharmacy/warnings', days ? { days } : {})
}

export const techApi = {
  overview: () => get('/api/tech/overview'),
  applies: (params) => get('/api/tech/applies', params),
  start: (id) => post(`/api/tech/applies/${id}/start`),
  result: (data) => post('/api/tech/applies/' + data.id + '/result', data)
}

export const financeApi = {
  revenue: (date) => get('/api/finance/revenue', date ? { date } : {}),
  doctorWorkload: (date) => get('/api/finance/doctor-workload', date ? { date } : {})
}

export const systemApi = {
  departments: () => get('/api/system/departments'),
  saveDepartment: (data) => post('/api/system/departments', data),
  deleteDepartment: (id) => del(`/api/system/departments/${id}`),

  constantTypes: () => get('/api/system/constant-types'),
  constantItems: (params) => get('/api/system/constant-items', params),

  registLevels: () => get('/api/system/regist-levels'),
  settleCategories: () => get('/api/system/settle-categories'),
  diseases: (keyword) => get('/api/system/diseases', keyword ? { keyword } : {}),
  diseCategories: () => get('/api/system/dise-categories'),
  expenseClasses: () => get('/api/system/expense-classes'),
  fmedItems: (params) => get('/api/system/fmed-items', params),
  scheduling: (params) => get('/api/system/scheduling', params),
  saveScheduling: (data) => post('/api/system/scheduling', data),
  deleteScheduling: (id) => del(`/api/system/scheduling/${id}`)
}

export default { authApi, registerApi, doctorApi, pharmacyApi, techApi, financeApi, systemApi }
