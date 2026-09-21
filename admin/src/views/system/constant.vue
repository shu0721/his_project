<template>
  <div>
    <div class="page-card">
      <div class="page-toolbar">
        <div>
          <div class="page-title">常数类别</div>
          <div class="page-desc">选择类别查看其下的常数项字典</div>
        </div>
      </div>

      <el-table :data="types" border stripe highlight-current-row style="width: 100%" @current-change="pick">
        <el-table-column prop="constantTypeCode" label="类别编码" width="200" />
        <el-table-column prop="constantTypeName" label="类别名称" min-width="220" />
      </el-table>
    </div>

    <div class="page-card" style="margin-top: 16px">
      <div class="page-toolbar">
        <div class="page-title">
          常数项{{ current ? ` · ${current.constantTypeName}` : '' }}
        </div>
      </div>

      <el-table :data="items" v-loading="loading" border stripe style="width: 100%">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="constantCode" label="编码" width="160" />
        <el-table-column prop="constantName" label="名称" min-width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { systemApi } from '@/api'

const types = ref([])
const items = ref([])
const current = ref(null)
const loading = ref(false)

async function loadItems() {
  if (!current.value) return
  loading.value = true
  try {
    items.value = await systemApi.constantItems({ typeId: current.value.id })
  } catch (e) {
    items.value = []
  } finally {
    loading.value = false
  }
}

function pick(row) {
  if (!row) return
  current.value = row
  loadItems()
}

onMounted(async () => {
  try {
    types.value = await systemApi.constantTypes()
    if (types.value.length) pick(types.value[0])
  } catch (e) {}
})
</script>
