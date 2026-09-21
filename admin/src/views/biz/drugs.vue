<template>
  <div class="his-page">
    <div class="his-head">
      <div class="his-head-titles">
        <div class="his-head-title">药品目录</div>
        <div class="his-head-desc">药品编码、规格、价格与库存维护，支持入库与库存预警查询</div>
      </div>
      <div class="his-head-actions">
        <el-input
          v-model="keyword"
          placeholder="搜索药品名称 / 编码"
          clearable
          class="search"
          @keyup.enter="load"
          @clear="load"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button @click="load">查询</el-button>
        <el-button type="primary" :icon="Plus" @click="openCreate">新增药品</el-button>
      </div>
    </div>

    <!-- 预警提示条 -->
    <div v-if="warnRows.length" class="alert">
      <div class="alert-head">
        <span class="alert-title">需要补货或临近效期</span>
        <span class="alert-count num">{{ warnRows.length }} 项</span>
      </div>
      <div class="alert-body">
        <span v-for="w in warnRows.slice(0, 8)" :key="w.key" class="alert-item" :class="w.cls">
          {{ w.drugsName }}
          <i>{{ w.note }}</i>
        </span>
        <span v-if="warnRows.length > 8" class="alert-more">另有 {{ warnRows.length - 8 }} 项…</span>
      </div>
    </div>

    <div class="his-panel his-panel--flush">
      <el-table :data="rows" v-loading="loading" style="width: 100%">
        <el-table-column prop="drugsCode" label="药品编码" width="164">
          <template #default="{ row }">
            <span class="num code">{{ row.drugsCode }}</span>
          </template>
        </el-table-column>

        <el-table-column label="药品名称" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="drug-name">{{ row.drugsName }}</span>
            <span v-if="row.manufacturer" class="drug-manu">{{ row.manufacturer }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="drugsFormat" label="规格" width="124" show-overflow-tooltip />

        <el-table-column prop="drugsUnit" label="单位" width="72" align="center" header-align="center">
          <template #default="{ row }">
            <span class="muted">{{ row.drugsUnit || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="单价" width="104" align="right" header-align="right">
          <template #default="{ row }">
            <span class="num">¥{{ Number(row.drugsPrice || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="库存" width="128" align="right" header-align="right">
          <template #default="{ row }">
            <div class="stock">
              <span class="num" :class="{ 'is-low': row.stock <= row.warnStock }">{{ row.stock ?? 0 }}</span>
              <span class="stock-warn">警戒 {{ row.warnStock ?? 0 }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="有效期至" width="118">
          <template #default="{ row }">
            <span class="num muted">{{ String(row.expiryDate || '').slice(0, 10) || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="right" header-align="right" fixed="right">
          <template #default="{ row }">
            <div class="ops">
              <button type="button" class="op" @click="openEdit(row)">编辑</button>
              <button type="button" class="op" @click="stockIn(row)">入库</button>
              <button type="button" class="op op-danger" @click="onDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>

        <template #empty>
          <div class="his-empty">没有匹配的药品</div>
        </template>
      </el-table>

      <div class="table-foot">
        <span>共 {{ rows.length }} 种药品</span>
      </div>
    </div>

    <!-- 新增 / 编辑 -->
    <el-dialog v-model="dialog" :title="editing ? '编辑药品' : '新增药品'" width="560px" :close-on-click-modal="false">
      <el-form :model="form" label-width="112px">
        <div class="form-grid">
          <el-form-item v-for="f in formFields" :key="f.prop" :label="f.label" :class="{ full: f.full }">
            <el-input-number
              v-if="f.type === 'number'"
              v-model="form[f.prop]"
              :min="f.min != null ? f.min : 0"
              controls-position="right"
              style="width: 100%"
            />
            <el-input v-else v-model="form[f.prop]" :placeholder="f.placeholder || '请输入'" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { pharmacyApi } from '@/api'

const rows = ref([])
const warnings = ref({ lowStock: [], nearExpiry: [] })
const loading = ref(false)
const saving = ref(false)
const dialog = ref(false)
const editing = ref(false)
const keyword = ref('')
const form = reactive({})

const formFields = [
  { prop: 'drugsCode', label: '药品编码' },
  { prop: 'drugsName', label: '药品名称' },
  { prop: 'drugsFormat', label: '规格' },
  { prop: 'drugsUnit', label: '单位' },
  { prop: 'manufacturer', label: '生产厂家', full: true },
  { prop: 'drugsPrice', label: '单价（元）', type: 'number', min: 0 },
  { prop: 'stock', label: '当前库存', type: 'number' },
  { prop: 'warnStock', label: '警戒库存', type: 'number' },
  { prop: 'expiryDate', label: '有效期至', placeholder: 'YYYY-MM-DD' },
  { prop: 'mnemonicCode', label: '助记码' },
  { prop: 'drugsTypeId', label: '药品类别 ID', type: 'number' },
  { prop: 'drugsDosageId', label: '剂型 ID', type: 'number' }
]

const blank = {
  drugsCode: '',
  drugsName: '',
  drugsFormat: '',
  drugsUnit: '盒',
  manufacturer: '',
  drugsPrice: 0,
  stock: 0,
  warnStock: 50,
  expiryDate: '',
  mnemonicCode: '',
  drugsTypeId: 1,
  drugsDosageId: 1
}

const warnRows = computed(() => [
  ...(warnings.value.lowStock || []).map((x, i) => ({
    key: 'low-' + i,
    drugsName: x.drugsName,
    note: `库存 ${x.stock}${x.unit || '盒'}`,
    cls: 'is-low'
  })),
  ...(warnings.value.nearExpiry || []).map((x, i) => ({
    key: 'exp-' + i,
    drugsName: x.drugsName,
    note: `${String(x.expiryDate || '').slice(0, 10)} 到期`,
    cls: 'is-exp'
  }))
])

async function load() {
  loading.value = true
  try {
    rows.value = await pharmacyApi.drugs(keyword.value ? { keyword: keyword.value } : {})
  } catch (e) {
    rows.value = []
  } finally {
    loading.value = false
  }
}

async function loadWarnings() {
  try {
    warnings.value = await pharmacyApi.warnings()
  } catch (e) {}
}

function resetForm() {
  Object.keys(form).forEach((k) => delete form[k])
  Object.assign(form, JSON.parse(JSON.stringify(blank)))
}

function openCreate() {
  editing.value = false
  resetForm()
  dialog.value = true
}

function openEdit(row) {
  editing.value = true
  resetForm()
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialog.value = true
}

async function submit() {
  if (!form.drugsName) return ElMessage.warning('请填写药品名称')
  saving.value = true
  try {
    await pharmacyApi.saveDrug({ ...form })
    ElMessage.success('保存成功')
    dialog.value = false
    load()
    loadWarnings()
  } catch (e) {
  } finally {
    saving.value = false
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确认从药品目录中删除【${row.drugsName}】？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  try {
    await pharmacyApi.deleteDrug(row.id)
    ElMessage.success('已删除')
    load()
    loadWarnings()
  } catch (e) {}
}

async function stockIn(row) {
  let value
  try {
    const res = await ElMessageBox.prompt(
      `为【${row.drugsName}】录入入库数量，当前库存 ${row.stock ?? 0}${row.drugsUnit || '盒'}。`,
      '药品入库',
      {
        inputPattern: /^\d+$/,
        inputErrorMessage: '请输入正整数',
        confirmButtonText: '确认入库',
        cancelButtonText: '取消'
      }
    )
    value = res.value
  } catch (e) {
    return
  }
  try {
    await pharmacyApi.stockIn(row.id, Number(value))
    ElMessage.success(`已入库 ${value} ${row.drugsUnit || '盒'}`)
    load()
    loadWarnings()
  } catch (e) {}
}

onMounted(() => {
  load()
  loadWarnings()
})
</script>

<style scoped>
.search {
  width: 214px;
}

/* ---------- 预警条 ---------- */
.alert {
  padding: 12px 16px;
  background: var(--his-warn-tint);
  border: 1px solid #eddcb8;
  border-radius: var(--his-radius-lg);
}

.alert-head {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 8px;
}

.alert-title {
  font-size: var(--his-font-base);
  font-weight: 600;
  color: var(--his-warn);
}

.alert-count {
  font-size: var(--his-font-sm);
  color: var(--his-warn);
}

.alert-body {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 8px;
}

.alert-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 3px 8px;
  border-radius: var(--his-radius-sm);
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid #eedfbe;
  font-size: var(--his-font-sm);
  color: var(--his-text);
}

.alert-item i {
  font-style: normal;
  color: var(--his-text-sub);
  font-size: var(--his-font-xs);
}

.alert-item.is-low {
  border-color: #eec9c7;
}

.alert-more {
  align-self: center;
  font-size: var(--his-font-sm);
  color: var(--his-warn);
}

/* ---------- 表格 ---------- */
.code {
  font-size: var(--his-font-sm);
  color: var(--his-text);
}

.drug-name {
  font-weight: 500;
  color: var(--his-text);
}

.drug-manu {
  display: block;
  margin-top: 1px;
  font-size: var(--his-font-xs);
  color: var(--his-text-muted);
}

.muted {
  color: var(--his-text-sub);
}

.stock {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 1px;
  line-height: 1.2;
}

.stock .is-low {
  color: var(--his-danger);
  font-weight: 600;
}

.stock-warn {
  font-size: var(--his-font-xs);
  line-height: 1.2;
  color: var(--his-text-muted);
}

.ops {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 2px;
}

.op {
  padding: 3px 7px;
  border: none;
  border-radius: var(--his-radius-sm);
  background: transparent;
  color: var(--his-primary-text);
  font-size: var(--his-font-sm);
  font-family: inherit;
  cursor: pointer;
  transition: background-color 150ms ease;
}

.op:hover {
  background: var(--his-primary-tint);
}

.op-danger {
  color: var(--his-danger);
}

.op-danger:hover {
  background: var(--his-danger-tint);
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

/* ---------- 表单 ---------- */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 14px;
}

.form-grid :deep(.full) {
  grid-column: 1 / -1;
}

.form-grid :deep(.el-form-item) {
  margin-bottom: 16px;
}
</style>
