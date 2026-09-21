<template>
  <div class="his-page">
    <div class="his-head">
      <div class="his-head-titles">
        <div class="his-head-title">营收统计</div>
        <div class="his-head-desc">按日查看营收构成与医生工作量，支持切换统计日期</div>
      </div>
      <div class="his-head-actions">
        <el-date-picker
          v-model="date"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择统计日期"
          clearable
          @change="load"
        />
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
    </div>

    <!-- 概览 -->
    <div class="overview">
      <div class="ov-main">
        <div class="ov-label">{{ date ? date + ' 实收合计' : '今日实收合计' }}</div>
        <div class="ov-value num">¥{{ money(revenue.todayRevenue) }}</div>
        <div class="ov-foot">
          <span>挂号 <b class="num">{{ revenue.registCount || 0 }}</b> 人次</span>
          <span>处方 <b class="num">{{ revenue.prescriptionCount || 0 }}</b> 张</span>
          <span v-if="byType.length">科目 <b class="num">{{ byType.length }}</b> 项</span>
        </div>
      </div>

      <div class="ov-side">
        <div v-for="b in byType" :key="b.name" class="ov-row">
          <span class="ov-row-name">{{ b.name }}</span>
          <span class="ov-row-track"><i :style="{ width: shareOf(b.money) }"></i></span>
          <span class="ov-row-money num">¥{{ money(b.money) }}</span>
        </div>
        <div v-if="!byType.length" class="his-empty">暂无费用构成数据</div>
      </div>
    </div>

    <!-- 走势 -->
    <section class="his-panel">
      <div class="his-panel-head">
        <div>
          <div class="his-panel-title">近 7 日营收趋势</div>
          <div class="panel-sub">单位：元</div>
        </div>
        <div class="panel-total">
          <span class="num">¥{{ money(weekTotal) }}</span>
          <i>7 日合计</i>
        </div>
      </div>

      <div class="chart">
        <div v-for="(t, i) in trend" :key="i" class="col">
          <div class="col-value num">¥{{ money(t.money) }}</div>
          <div class="col-track">
            <div class="col-bar" :class="{ 'is-peak': isPeak(t) }" :style="{ height: barH(t.money) }"></div>
          </div>
          <div class="col-label num">{{ t.label }}</div>
        </div>
        <div v-if="!trend.length" class="his-empty" style="flex: 1">暂无营收数据</div>
      </div>
    </section>

    <!-- 医生工作量 -->
    <section class="his-panel his-panel--flush">
      <div class="his-panel-head">
        <div class="his-panel-title">医生工作量统计</div>
        <div class="his-panel-meta">共 {{ workload.length }} 位医生</div>
      </div>

      <el-table :data="workload" v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="#" width="52" align="center" header-align="center" />
        <el-table-column prop="doctorName" label="医生" min-width="110" />
        <el-table-column prop="deptName" label="所属科室" min-width="140" show-overflow-tooltip />

        <el-table-column label="挂号人次" width="110" align="right" header-align="right">
          <template #default="{ row }">
            <span class="num">{{ row.registerCount || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="已诊人次" width="110" align="right" header-align="right">
          <template #default="{ row }">
            <span class="num">{{ row.visitedCount || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="诊毕率" width="200">
          <template #default="{ row }">
            <div class="rate">
              <span class="rate-track"><i :style="{ width: rateOf(row) }"></i></span>
              <span class="rate-num num">{{ rateOf(row) }}</span>
            </div>
          </template>
        </el-table-column>

        <template #empty>
          <div class="his-empty">暂无医生工作量数据</div>
        </template>
      </el-table>

      <div class="table-foot">
        <span>统计口径：挂号人次与已诊人次按当日流水归属</span>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { financeApi } from '@/api'

const revenue = ref({})
const workload = ref([])
const loading = ref(false)
const date = ref('')

const trend = computed(() => revenue.value.trend || [])
const byType = computed(() => revenue.value.byType || [])
const weekTotal = computed(() => trend.value.reduce((s, t) => s + Number(t.money || 0), 0))
const maxMoney = computed(() => trend.value.reduce((m, t) => Math.max(m, Number(t.money || 0)), 0))

function money(v) {
  return Number(v || 0)
    .toFixed(2)
    .replace(/\B(?=(\d{3})+(?!\d))/g, ',')
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
  return (pct < 4 ? 4 : pct) + '%'
}

function rateOf(row) {
  const reg = Number(row.registerCount || 0)
  const vis = Number(row.visitedCount || 0)
  if (!reg) return '—'
  return Math.round((vis / reg) * 100) + '%'
}

async function load() {
  loading.value = true
  const params = date.value ? { date: date.value } : {}
  try {
    revenue.value = await financeApi.revenue(params)
  } catch (e) {}
  try {
    workload.value = await financeApi.doctorWorkload(params)
  } catch (e) {}
  loading.value = false
}

onMounted(load)
</script>

<style scoped>
/* ---------- 概览 ---------- */
.overview {
  display: grid;
  grid-template-columns: minmax(240px, 0.72fr) minmax(0, 1.28fr);
  gap: 0;
  background: var(--his-surface);
  border: 1px solid var(--his-border);
  border-radius: var(--his-radius-lg);
  overflow: hidden;
}

@media (max-width: 940px) {
  .overview {
    grid-template-columns: minmax(0, 1fr);
  }
}

.ov-main {
  padding: 18px 20px;
  border-right: 1px solid var(--his-border);
}

@media (max-width: 940px) {
  .ov-main {
    border-right: none;
    border-bottom: 1px solid var(--his-border);
  }
}

.ov-label {
  font-size: var(--his-font-sm);
  color: var(--his-text-sub);
}

.ov-value {
  margin-top: 8px;
  font-size: var(--his-font-4xl);
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--his-primary-text);
  line-height: 1.1;
}

.ov-foot {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 12px;
  font-size: var(--his-font-sm);
  color: var(--his-text-sub);
}

.ov-foot b {
  font-weight: 600;
  color: var(--his-text);
}

.ov-side {
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ov-row {
  display: grid;
  grid-template-columns: minmax(72px, max-content) minmax(60px, 1fr) max-content;
  align-items: center;
  gap: 14px;
  font-size: var(--his-font-base);
}

.ov-row-name {
  color: var(--his-text-sub);
  white-space: nowrap;
}

.ov-row-track {
  height: 5px;
  border-radius: 3px;
  background: var(--his-surface-sunken);
  overflow: hidden;
}

.ov-row-track i {
  display: block;
  height: 100%;
  border-radius: 3px;
  background: var(--his-primary-line);
}

.ov-row-money {
  font-weight: 600;
  color: var(--his-text);
  white-space: nowrap;
}

/* ---------- 走势 ---------- */
.panel-sub {
  margin-top: 2px;
  font-size: var(--his-font-xs);
  color: var(--his-text-muted);
}

.panel-total {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  line-height: 1.3;
}

.panel-total span {
  font-size: var(--his-font-md);
  font-weight: 600;
  color: var(--his-text);
}

.panel-total i {
  font-size: var(--his-font-xs);
  font-style: normal;
  color: var(--his-text-muted);
}

.chart {
  display: flex;
  align-items: stretch;
  gap: 10px;
  height: 200px;
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
  font-size: var(--his-font-xs);
  color: var(--his-text-sub);
  margin-bottom: 6px;
  white-space: nowrap;
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
  width: 58%;
  max-width: 34px;
  border-radius: 3px 3px 0 0;
  background: var(--his-primary-line);
  transition: background-color 180ms ease;
}

.col:hover .col-bar {
  background: #a3cbb5;
}

.col-bar.is-peak {
  background: var(--his-primary);
}

.col:hover .col-bar.is-peak {
  background: var(--his-primary-hover);
}

.col-label {
  margin-top: 6px;
  font-size: var(--his-font-xs);
  color: var(--his-text-muted);
}

/* ---------- 诊毕率 ---------- */
.rate {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rate-track {
  flex: 1;
  height: 4px;
  border-radius: 2px;
  background: var(--his-surface-sunken);
  overflow: hidden;
}

.rate-track i {
  display: block;
  height: 100%;
  border-radius: 2px;
  background: var(--his-primary);
}

.rate-num {
  width: 38px;
  text-align: right;
  font-size: var(--his-font-sm);
  color: var(--his-text-sub);
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
