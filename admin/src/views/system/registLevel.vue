<template>
  <CrudTable
    title="挂号级别"
    desc="普通号 / 专家号 / 专科号及挂号费、号源上限"
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
  { prop: 'registCode', label: '级别编码', width: 140 },
  { prop: 'registName', label: '级别名称', width: 160 },
  { prop: 'registFee', label: '挂号费', width: 130, type: 'money' },
  { prop: 'registQuota', label: '号源上限', width: 130, align: 'right' },
  { prop: 'sequenceNo', label: '排序号', width: 110, align: 'right' }
]

const formFields = [
  { prop: 'registCode', label: '级别编码' },
  { prop: 'registName', label: '级别名称' },
  { prop: 'registFee', label: '挂号费', type: 'number', min: 0 },
  { prop: 'registQuota', label: '号源上限', type: 'number' },
  { prop: 'sequenceNo', label: '排序号', type: 'number' }
]

const blank = { registCode: '', registName: '', registFee: 10, registQuota: 30, sequenceNo: 9 }

const fetch = () => systemApi.registLevels()
const save = (data) => systemApi.saveRegistLevel(data)
const remove = (id) => systemApi.deleteRegistLevel(id)
</script>
