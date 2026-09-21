<template>
  <CrudTable
    title="医生排班"
    desc="医生出诊日期、午别与号源设置"
    :columns="columns"
    :form-fields="formFields"
    :fetch="fetch"
    :save="save"
    :remove="remove"
    :blank="blank"
  />
</template>

<script setup>
import { systemApi } from '@/api'
import CrudTable from '@/components/CrudTable.vue'

const columns = [
  { prop: 'schedDate', label: '排班日期', width: 130, type: 'date' },
  { prop: 'deptName', label: '科室', width: 140 },
  { prop: 'doctorName', label: '医生', width: 110 },
  { prop: 'noon', label: '午别', width: 90 },
  { prop: 'regNum', label: '已挂号', width: 90, align: 'right' }
]

const formFields = [
  { prop: 'schedDate', label: '排班日期', placeholder: 'YYYY-MM-DD' },
  { prop: 'deptId', label: '科室 ID', type: 'number' },
  { prop: 'userId', label: '医生 ID', type: 'number' },
  { prop: 'noon', label: '午别', type: 'select', options: [
    { value: '上午', label: '上午' },
    { value: '下午', label: '下午' }
  ] },
  { prop: 'ruleId', label: '规则 ID', type: 'number' }
]

const blank = { schedDate: '', deptId: 1, userId: 10, noon: '上午', ruleId: 1 }

const fetch = (params) => systemApi.scheduling(params)
const save = (data) => systemApi.saveScheduling(data)
const remove = (id) => systemApi.deleteScheduling(id)
</script>
