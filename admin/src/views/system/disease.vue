<template>
  <CrudTable
    title="诊断目录"
    desc="ICD-10 诊断库维护，供门诊病历与处方关联使用"
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
  { prop: 'diseaseCode', label: '诊断编码', width: 150 },
  { prop: 'diseaseName', label: '诊断名称', minWidth: 220 },
  { prop: 'diseaseIcd', label: 'ICD 编码', width: 150 },
  { prop: 'diseCategoryId', label: '类别 ID', width: 110, align: 'center' }
]

const formFields = [
  { prop: 'diseaseCode', label: '诊断编码' },
  { prop: 'diseaseName', label: '诊断名称' },
  { prop: 'diseaseIcd', label: 'ICD 编码' },
  { prop: 'diseCategoryId', label: '类别 ID', type: 'number' }
]

const blank = { diseaseCode: '', diseaseName: '', diseaseIcd: '', diseCategoryId: 1 }

const fetch = (params) => systemApi.diseases(params)
const save = (data) => systemApi.saveDisease(data)
const remove = (id) => systemApi.deleteDisease(id)
</script>
