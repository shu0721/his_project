<template>
  <CrudTable
    title="非药品收费项目"
    desc="检验 / 检查 / 处置等项目的编码与定价"
    :columns="columns"
    :form-fields="formFields"
    :fetch="fetch"
    :save="save"
    :remove="remove"
    :blank="blank"
    searchable
    filter-key="keyword"
  />
</template>

<script setup>
import { systemApi } from '@/api'
import CrudTable from '@/components/CrudTable.vue'

const columns = [
  { prop: 'itemCode', label: '项目编码', width: 140 },
  { prop: 'itemName', label: '项目名称', minWidth: 200 },
  { prop: 'format', label: '规格', width: 110 },
  { prop: 'price', label: '单价', width: 120, type: 'money' },
  { prop: 'recordType', label: '类型', width: 100, align: 'center' },
  { prop: 'mnemonicCode', label: '助记码', width: 120 }
]

const formFields = [
  { prop: 'itemCode', label: '项目编码' },
  { prop: 'itemName', label: '项目名称' },
  { prop: 'format', label: '规格' },
  { prop: 'price', label: '单价', type: 'number', min: 0 },
  { prop: 'expClassId', label: '费用科目 ID', type: 'number' },
  { prop: 'deptId', label: '执行科室 ID', type: 'number' },
  { prop: 'recordType', label: '类型（1检查 2检验 4处置）', type: 'number' },
  { prop: 'mnemonicCode', label: '助记码' }
]

const blank = {
  itemCode: '',
  itemName: '',
  format: '项',
  price: 0,
  expClassId: 3,
  deptId: 3,
  recordType: 2,
  mnemonicCode: ''
}

const fetch = (params) => systemApi.fmedItems(params)
const save = (data) => systemApi.saveFmedItem(data)
const remove = (id) => systemApi.deleteFmedItem(id)
</script>
