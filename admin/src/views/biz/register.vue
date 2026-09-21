<template>
  <div class="his-page">
    <div class="his-head">
      <div class="his-head-titles">
        <div class="his-head-title">挂号记录</div>
        <div class="his-head-desc">当日挂号流水，可按就诊状态筛选并处理退号</div>
      </div>
      <div class="his-head-actions">
        <el-date-picker
          v-model="date"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择日期"
          clearable
          @change="load"
        />
        <el-select v-model="visitState" placeholder="全部状态" clearable class="state-sel" @change="load">
          <el-option label="待接诊" :value="1" />
          <el-option label="就诊中" :value="2" />
          <el-option label="已诊毕" :value="3" />
        </el-select>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
    </div>

    <div class="his-metrics">
      <div class="his-metric">
        <div class="his-metric-label">记录总数</div>
        <div class="his-metric-value num">{{ rows.length }}</div>
        <div class="his-metric-foot">当前筛选结果</div>
      </div>
      <div class="his-metric">
        <div class="his-metric-label">待接诊</div>
        <div class="his-metric-value num">{{ countOf(1) }}</div>
        <div class="his-metric-foot">尚未开始就诊</div>
      </div>
      <div class="his-metric">
        <div class="his-metric-label">就诊中</div>
        <div class="his-metric-value num">{{ countOf(2) }}</div>
        <div class="his-metric-foot">医生已接诊</div>
      </div>
      <div class="his-metric">
        <div class="his-metric-label">已诊毕</div>
        <div class="his-metric-value num">{{ countOf(3) }}</div>
        <div class="his-metric-foot">可进入收费结算</div>
      </div>
    </div>

    <div class="his-panel his-panel--flush">
      <el-table :data="rows" v-loading="loading" style="width: 100%">
        <el-table-column prop="caseNumber" label="就诊号" width="168">
          <template #default="{ row }">
            <span class="num case-no">{{ row.caseNumber }}</span>
          </template>
        </el-table-column>

        <el-table-column label="患者" min-width="130">
          <template #default="{ row }">
            <span class="p-name">{{ row.realName }}</span>
            <span class="p-meta">{{ row.genderText }} · {{ row.age }}岁</span>
          </template>
        </el-table-column>

        <el-table-column prop="deptName" label="就诊科室" min-width="112" show-overflow-tooltip />
        <el-table-column prop="doctorName" label="接诊医生" min-width="100" show-overflow-tooltip />

        <el-table-column label="挂号级别" width="110">
          <template #default="{ row }">
            <span class="muted">{{ row.registName || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="结算类别" width="110">
          <template #default="{ row }">
            <span class="muted">{{ row.settleName || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="noon" label="午别" width="76" align="center" />

        <el-table-column label="就诊状态" width="104">
          <template #default="{ row }">
            <span class="cell-tag" :class="stateClass(row.visitState)">{{ row.visitStateText }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="88" align="right" header-align="right" fixed="right">
          <template #default="{ row }">
            <button v-if="row.visitState === 1" type="button" class="op op-danger" @click="refund(row)">
              退号
            </button>
            <span v-else class="op-none">—</span>
          </template>
        </el-table-column>

        <template #empty>
          <div class="his-empty">该筛选条件下没有挂号记录</div>
        </template>
      </el-table>

      <div class="table-foot">
        <span>共 {{ rows.length }} 条记录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { registerApi } from '@/api'

const rows = ref([])
const loading = ref(false)
const date = ref('')
const visitState = ref(null)

function countOf(s) {
  return rows.value.filter((r) => r.visitState === s).length
}

function stateClass(s) {
  if (s === 1) return 'is-warning'
  if (s === 2) return 'is-primary'
  if (s === 3) return 'is-success'
  return ''
}

async function load() {
  loading.value = true
  try {
    const params = {}
    if (date.value) params.date = date.value
    if (visitState.value) params.visitState = visitState.value
    rows.value = await registerApi.list(params)
  } catch (e) {
    rows.value = []
  } finally {
    loading.value = false
  }
}

async function refund(row) {
  try {
    await ElMessageBox.confirm(
      `将对【${row.realName}】${row.caseNumber} 执行退号，退号后该就诊记录不再可用。`,
      '退号确认',
      { type: 'warning', confirmButtonText: '确认退号', cancelButtonText: '取消' }
    )
  } catch (e) {
    return
  }
  try {
    await registerApi.refund(row.id)
    ElMessage.success('已退号')
    load()
  } catch (e) {}
}

onMounted(load)
</script>

<style scoped>
.state-sel {
  width: 132px;
}

.case-no {
  font-size: var(--his-font-sm);
  color: var(--his-text);
}

.p-name {
  font-weight: 500;
  color: var(--his-text);
}

.p-meta {
  margin-left: 8px;
  font-size: var(--his-font-sm);
  color: var(--his-text-muted);
}

.muted {
  color: var(--his-text-sub);
}

.cell-tag {
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 7px;
  border-radius: var(--his-radius-sm);
  font-size: var(--his-font-xs);
  font-weight: 500;
  background: var(--his-info-tint);
  color: var(--his-info);
}

.cell-tag.is-primary {
  background: var(--his-primary-tint);
  color: var(--his-primary-text);
}

.cell-tag.is-success {
  background: var(--his-success-tint);
  color: var(--his-success);
}

.cell-tag.is-warning {
  background: var(--his-warn-tint);
  color: var(--his-warn);
}

.op {
  padding: 3px 7px;
  border: none;
  border-radius: var(--his-radius-sm);
  background: transparent;
  font-size: var(--his-font-sm);
  font-family: inherit;
  cursor: pointer;
  transition: background-color 150ms ease;
}

.op-danger {
  color: var(--his-danger);
}

.op-danger:hover {
  background: var(--his-danger-tint);
}

.op-none {
  color: var(--his-text-muted);
  font-size: var(--his-font-sm);
}

.table-foot {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 10px 16px;
  border-top: 1px solid var(--his-border);
  background: var(--his-surface-sunken);
  font-size: var(--his-font-sm);
  color: var(--his-text-sub);
}
</style>
