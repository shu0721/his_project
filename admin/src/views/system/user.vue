<template>
  <CrudTable
    title="用户管理"
    desc="医院工作人员的账号、角色与所属科室维护"
    :columns="columns"
    :form-fields="formFields"
    :fetch="fetch"
    :save="save"
    :blank="blank"
    searchable
    filter-key="keyword"
  >
    <template #actions="{ row }">
      <el-button link type="warning" @click="reset(row)">重置密码</el-button>
    </template>
  </CrudTable>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import CrudTable from '@/components/CrudTable.vue'
import { authApi } from '@/api'

const roleOptions = [
  { value: 1, label: '医院管理员' },
  { value: 2, label: '挂号收费员' },
  { value: 3, label: '门诊医生' },
  { value: 4, label: '医技医生' },
  { value: 5, label: '药房操作员' },
  { value: 6, label: '财务管理员' }
]

const columns = [
  { prop: 'userName', label: '登录账号', width: 130 },
  { prop: 'realName', label: '姓名', width: 110 },
  { prop: 'useTypeName', label: '角色', width: 120 },
  { prop: 'deptName', label: '所属科室', width: 130 },
  { prop: 'docTitleName', label: '职称', width: 110 },
  { prop: 'isScheduling', label: '参与排班', width: 100, align: 'center' }
]

const formFields = [
  { prop: 'userName', label: '登录账号' },
  { prop: 'realName', label: '姓名' },
  { prop: 'password', label: '密码', placeholder: '不修改请留空' },
  { prop: 'useType', label: '角色', type: 'select', options: roleOptions },
  { prop: 'deptId', label: '科室 ID', type: 'number' },
  { prop: 'isScheduling', label: '是否排班', type: 'select', options: [
    { value: '是', label: '是' },
    { value: '否', label: '否' }
  ] }
]

const blank = {
  userName: '',
  realName: '',
  password: '123456',
  useType: 3,
  deptId: 1,
  isScheduling: '是'
}

const fetch = (params) => authApi.users(params)
const save = (data) => (data.id ? authApi.updateUser(data) : authApi.addUser(data))

async function reset(row) {
  try {
    await ElMessageBox.confirm(`将【${row.realName}】的密码重置为 123456？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await authApi.resetPassword(row.id)
  ElMessage.success('密码已重置为 123456')
}
</script>
