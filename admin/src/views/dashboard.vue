<template>
  <div class="his-page">
    <div class="his-head">
      <div class="his-head-titles">
        <div class="his-head-title">运营工作台</div>
        <div class="his-head-desc">今日门诊运行概况 · 数据来源于实时业务库</div>
      </div>
      <div class="his-head-actions">
        <span class="stamp">统计日期 {{ today }}</span>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
    </div>

    <!-- 指标条 -->
    <div class="his-metrics">
      <div v-for="m in metrics" :key="m.label" class="his-metric">
        <div class="his-metric-label">{{ m.label }}</div>
        <div class="his-metric-value num">{{ m.value }}</div>
        <div class="his-metric-foot">{{ m.foot }}</div>
      </div>
    </div>

    <div class="split">
      <!-- 营收走势 -->
      <section class="his-panel">
        <div class="his-panel-head">
          <div>
            <div class="his-panel-title">近 7 日营收</div>
            <div class="panel-sub">单位：元</div>
          </div>
          <div class="panel-total">
            <span class="num">¥{{ money(weekTotal) }}</span>
            <i>7 日合计</i>
          </div>
        </div>

        <div class="chart">
          <div v-for="(t, i) in trend" :key="i" class="col">
            <div class="col-value num">{{ short(t.money) }}</div>
            <div class="col-track">
              <div class="col-bar" :class="{ 'is-peak': isPeak(t) }" :style="{ height: barH(t.money) }"></div>
            </div>
            <div class="col-label num">{{ t.label }}</div>
          </div>
          <div v-if="!trend.length" class="his-empty" style="flex: 1">暂无营收数据</div>
        </div>
      </section>

      <!-- 费用构成 -->
      <section class="his-panel">
        <div class="his-panel-head">
          <div class="his-panel-title">费用构成</div>
          <div class="his-panel-meta">今日</div>
        </div>

        <table v-if="byType.length" class="mini">
          <tbody>
            <tr v-for="b in byType" :key="b.name">
              <td class="mini-name">{{ b.name }}</td>
              <td class="mini-bar">
                <span :style="{ width: shareOf(b.money) }"></span>
              </td>
              <td class="mini-money num">¥{{ money(b.money) }}</td>
            </tr>
          </tbody>
        </table>
        <div v-else class="his-empty">暂无费用数据</div>
      </section>
    </div>

    <div class="split split-even">
      <!-- 医生工作量 -->
      <section class="his-panel his-panel--flush">
        <div class="his-panel-head">
          <div class="his-panel-title">医生工作量</div>
          <div class="his-panel-meta">按挂号人次排序</div>
        </div>
        <el-table :data="workload" size="small" style="width: 100%" :show-header="true">
          <el-table-column prop="doctorName" label="医生" min-width="90" />
          <el-table-column prop="deptName" label="科室" min-width="110" />
          <el-table-column label="挂号" width="80" align="right">
            <template #default="{ row }">
              <span class="num">{{ row.registerCount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="已诊" width="80" align="right">
            <template #default="{ row }">
              <span class="num">{{ row.visitedCount || 0 }}</span>
            </template>
          </el-table-column>
          <template #empty>
            <div class="his-empty">暂无工作量数据</div>
          </template>
        </el-table>
      </section>

      <!-- 药品预警 -->
      <section class="his-panel his-panel--flush">
        <div class="his-panel-head">
          <div class="his-panel-title">药品库存预警</div>
          <router-link to="/biz/drugs" class="panel-link">前往药品目录</router-link>
        </div>
        <el-table :data="warnRows" size="small" style="width: 100%">
          <el-table-column prop="drugsName" label="药品" min-width="130" show-overflow-tooltip>
            <template #default="{ row }">
              <span>{{ row.drugsName }}</span>
              <i v-if="row.drugsFormat" class="spec">{{ row.drugsFormat }}</i>
            </template>
          </el-table-column>
          <el-table-column label="类型" width="86">
            <template #default="{ row }">
              <span class="cell-tag" :class="row.warnType === '库存不足' ? 'is-danger' : 'is-warning'">
                {{ row.warnType }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="说明" min-width="130" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="muted-cell">{{ row.note }}</span>
            </template>
          </el-table-column>
          <template #empty>
            <div class="his-empty">库存与效期均在正常范围</div>
          </template>
        </el-table>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { financeApi, pharmacyApi } from '@/api'

const revenue = ref({})
const workload = ref([])
const warnings = ref({ lowStock: [], nearExpiry: [] })

const trend = computed(() => revenue.value.trend || [])
const byType = computed(() => revenue.value.byType || [])
const weekTotal = computed(() => trend.value.reduce((s, t) => s + Number(t.money || 0), 0))
const maxMoney = computed(() => trend.value.reduce((m, t) => Math.max(m, Number(t.money || 0)), 0))

const warnRows = computed(() => [
  ...(warnings.value.lowStock || []).map((x) => ({
    ...x,
    warnType: '库存不足',
    note: `现有 ${x.stock}${x.unit || '盒'}，警戒 ${x.warnStock}`
  })),
  ...(warnings.value.nearExpiry || []).map((x) => ({
    ...x,
    warnType: '近效期',
    note: `有效期至 ${String(x.expiryDate || '').slice(0, 10)}`
  }))
])

const today = computed(() => {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`
})

const metrics = computed(() => [
  {
    label: '今日营收',
    value: '¥' + money(revenue.value.todayRevenue),
    foot: '含挂号费与药品费用'
  },
  {
    label: '今日挂号',
    value: (revenue.value.registCount || 0) + ' 人次',
    foot: '现场与预约合计'
  },
  {
    label: '今日处方',
    value: (revenue.value.prescriptionCount || 0) + ' 张',
    foot: '成药与草药处方'
  },
  {
    label: '待处理预警',
    value: warnRows.value.length + ' 项',
    foot: `库存不足 ${(warnings.value.lowStock || []).length} · 近效期 ${
      (warnings.value.nearExpiry || []).length
    }`
  }
])

function money(v) {
  return Number(v || 0)
    .toFixed(2)
    .replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function short(v) {
  const n = Number(v || 0)
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  return n.toFixed(0)
}

function barH(v) {
  const max = maxMoney.value || 1
  const h = Math.round((Number(v || 0) / max) * 100)
  return (h < 3 ? 3 : h) + '%'
}

function isPeak(t) {
  return Number(t.money || 0) === maxMoney.value && maxMoney.value > 0
}

function shareOf(v) {
  const total = byType.value.reduce((s, b) => s + Number(b.money || 0), 0) || 1
  const pct = Math.round((Number(v || 0) / total) * 100)
  return (pct < 3 ? 3 : pct) + '%'
}

async function load() {
  try {
    revenue.value = await financeApi.revenue()
  } catch (e) {}
  try {
    workload.value = await financeApi.doctorWorkload()
  } catch (e) {}
  try {
    warnings.value = await pharmacyApi.warnings()
  } catch (e) {}
}

onMounted(load)
</script>

<style scoped>
.stamp {
  font-size: 12px;
  color: var(--his-text-sub);
  padding-right: 4px;
}

.split {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(0, 1fr);
  gap: 14px;
}

.split-even {
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
}

@media (max-width: 1080px) {
  .split,
  .split-even {
    grid-template-columns: minmax(0, 1fr);
  }
}

.panel-sub {
  margin-top: 2px;
  font-size: 11px;
  color: var(--his-text-muted);
}

.panel-total {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  line-height: 1.3;
}

.panel-total span {
  font-size: 15px;
  font-weight: 600;
  color: var(--his-text);
}

.panel-total i {
  font-size: 11px;
  font-style: normal;
  color: var(--his-text-muted);
}

.panel-link {
  font-size: 12px;
  color: var(--his-primary-text);
  text-decoration: none;
}

.panel-link:hover {
  text-decoration: underline;
  text-underline-offset: 3px;
}

/* ---------- 柱状图 ---------- */
.chart {
  display: flex;
  align-items: stretch;
  gap: 10px;
  height: 186px;
  padding-top: 6px;
}

.col {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.col-value {
  font-size: 11px;
  color: var(--his-text-sub);
  margin-bottom: 6px;
}

.col-track {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  border-bottom: 1px solid var(--his-border);
}

.col-bar {
  width: 60%;
  max-width: 30px;
  border-radius: 3px 3px 0 0;
  background: var(--his-primary-line);
  transition: background-color 180ms ease;
}

.col:hover .col-bar {
  background: #a6c8e2;
}

.col-bar.is-peak {
  background: var(--his-primary);
}

.col:hover .col-bar.is-peak {
  background: var(--his-primary-hover);
}

.col-label {
  margin-top: 6px;
  font-size: 11px;
  color: var(--his-text-muted);
}

/* ---------- 费用构成 ---------- */
.mini {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.mini td {
  padding: 8px 0;
  border-bottom: 1px solid var(--his-border);
  vertical-align: middle;
}

.mini tr:last-child td {
  border-bottom: none;
}

.mini-name {
  color: var(--his-text-sub);
  white-space: nowrap;
  padding-right: 12px !important;
}

.mini-bar {
  width: 42%;
  padding: 0 12px !important;
}

.mini-bar span {
  display: block;
  height: 4px;
  border-radius: 2px;
  background: var(--his-primary-line);
}

.mini-money {
  text-align: right;
  font-weight: 600;
  color: var(--his-text);
  white-space: nowrap;
}

/* ---------- 表格内小元素 ---------- */
.spec {
  margin-left: 6px;
  font-size: 11px;
  font-style: normal;
  color: var(--his-text-muted);
}

.muted-cell {
  color: var(--his-text-sub);
  font-size: 12px;
}

.cell-tag {
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 7px;
  border-radius: var(--his-radius-sm);
  font-size: 11px;
  font-weight: 500;
}

.cell-tag.is-danger {
  background: var(--his-danger-tint);
  color: var(--his-danger);
}

.cell-tag.is-warning {
  background: var(--his-warn-tint);
  color: var(--his-warn);
}
</style>
