<template>
  <CrudTable
    title="结算类别"
    desc="自费 / 医保 / 商业保险等结算方式"
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
  { prop: 'settleCode', label: '类别编码', width: 160 },
  { prop: 'settleName', label: '类别名称', minWidth: 200 },
  { prop: 'sequenceNo', label: '排序号', width: 120, align: 'right' }
]

const formFields = [
  { prop: 'settleCode', label: '类别编码' },
  { prop: 'settleName', label: '类别名称' },
  { prop: 'sequenceNo', label: '排序号', type: 'number' }
]

const blank = { settleCode: '', settleName: '', sequenceNo: 9 }

const fetch = () => systemApi.settleCategories()
const save = (data) => systemApi.saveSettleCategory(data)
const remove = (id) => systemApi.deleteSettleCategory(id)
</script>
