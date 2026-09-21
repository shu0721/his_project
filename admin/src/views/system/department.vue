<template>
  <CrudTable
    title="科室管理"
    desc="门诊、医技与职能科室基础字典"
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
  { prop: 'deptCode', label: '科室编码', width: 140 },
  { prop: 'deptName', label: '科室名称', minWidth: 180 },
  { prop: 'deptType', label: '科室类型', width: 120, align: 'center' },
  { prop: 'deptCategoryId', label: '类别 ID', width: 110, align: 'center' }
]

const formFields = [
  { prop: 'deptCode', label: '科室编码' },
  { prop: 'deptName', label: '科室名称' },
  { prop: 'deptType', label: '科室类型（165 门诊 / 168 医技）', type: 'number' },
  { prop: 'deptCategoryId', label: '类别 ID', type: 'number' }
]

const blank = { deptCode: '', deptName: '', deptType: 165, deptCategoryId: 11 }

const fetch = () => systemApi.departments()
const save = (data) => systemApi.saveDepartment(data)
const remove = (id) => systemApi.deleteDepartment(id)
</script>
