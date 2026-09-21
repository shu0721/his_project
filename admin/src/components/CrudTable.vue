<template>
  <div class="his-page">
    <!-- 页头 -->
    <div class="his-head">
      <div class="his-head-titles">
        <div class="his-head-title">{{ title }}</div>
        <div v-if="desc" class="his-head-desc">{{ desc }}</div>
      </div>

      <div class="his-head-actions">
        <el-input
          v-if="searchable"
          v-model="keyword"
          :placeholder="searchPlaceholder"
          clearable
          class="search"
          @keyup.enter="load"
          @clear="load"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button v-if="searchable" @click="load">查询</el-button>
        <el-button v-if="savable" type="primary" :icon="Plus" @click="openCreate">新增</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="his-panel his-panel--flush">
      <el-table :data="rows" v-loading="loading" style="width: 100%" :row-key="rowKey">
        <el-table-column type="index" label="#" width="52" align="center" header-align="center" />

        <el-table-column
          v-for="c in columns"
          :key="c.prop"
          :prop="c.prop"
          :label="c.label"
          :width="c.width"
          :min-width="c.minWidth"
          :align="c.align || 'left'"
          :header-align="c.align || 'left'"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span v-if="c.type === 'money'" class="num">¥{{ Number(row[c.prop] || 0).toFixed(2) }}</span>
            <span v-else-if="c.type === 'date'" class="num">{{ String(row[c.prop] || '').slice(0, 10) }}</span>
            <span v-else-if="c.type === 'tag'" class="cell-tag" :class="tagClass(c, row)">
              {{ row[c.prop] }}
            </span>
            <span v-else-if="c.type === 'muted'" class="cell-muted">{{ row[c.prop] || '—' }}</span>
            <span v-else>{{ row[c.prop] }}</span>
          </template>
        </el-table-column>

        <el-table-column
          v-if="savable || removable || $slots.actions"
          label="操作"
          width="168"
          align="right"
          header-align="right"
          fixed="right"
        >
          <template #default="{ row }">
            <div class="ops">
              <button v-if="savable" type="button" class="op" @click="openEdit(row)">编辑</button>
              <slot name="actions" :row="row" />
              <button v-if="removable" type="button" class="op op-danger" @click="onDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>

        <template #empty>
          <div class="his-empty">暂无数据</div>
        </template>
      </el-table>

      <div class="table-foot">
        <span>共 {{ rows.length }} 条记录</span>
      </div>
    </div>

    <!-- 表单抽屉式弹窗 -->
    <el-dialog v-model="dialog" :title="editing ? `编辑${title}` : `新增${title}`" width="520px" :close-on-click-modal="false">
      <el-form :model="form" label-width="104px" label-position="right">
        <el-form-item v-for="f in formFields" :key="f.prop" :label="f.label">
          <el-select v-if="f.type === 'select'" v-model="form[f.prop]" placeholder="请选择" style="width: 100%">
            <el-option v-for="o in f.options" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
          <el-input-number
            v-else-if="f.type === 'number'"
            v-model="form[f.prop]"
            :min="f.min != null ? f.min : 0"
            controls-position="right"
            style="width: 100%"
          />
          <el-input v-else v-model="form[f.prop]" :placeholder="f.placeholder || '请输入'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const props = defineProps({
  title: { type: String, required: true },
  desc: { type: String, default: '' },
  columns: { type: Array, required: true },
  formFields: { type: Array, default: () => [] },
  fetch: { type: Function, required: true },
  save: { type: Function, default: null },
  remove: { type: Function, default: null },
  blank: { type: Object, default: () => ({}) },
  searchable: { type: Boolean, default: false },
  searchPlaceholder: { type: String, default: '输入关键字搜索' },
  filterKey: { type: String, default: 'keyword' },
  parentParams: { type: Object, default: () => ({}) },
  rowKey: { type: String, default: 'id' }
})

const rows = ref([])
const loading = ref(false)
const saving = ref(false)
const dialog = ref(false)
const editing = ref(false)
const keyword = ref('')
const form = reactive({})

const savable = computed(() => !!props.save)
const removable = computed(() => !!props.remove)

watch(
  () => props.parentParams,
  () => load(),
  { deep: true }
)

function tagClass(c, row) {
  const t = typeof c.tagType === 'function' ? c.tagType(row) : 'primary'
  return `is-${t}`
}

async function load() {
  loading.value = true
  try {
    const params = {}
    if (props.searchable && keyword.value) params[props.filterKey] = keyword.value
    const data = await props.fetch({ ...params, ...props.parentParams })
    rows.value = Array.isArray(data) ? data : data.records || []
  } catch (e) {
    rows.value = []
  } finally {
    loading.value = false
  }
}

function resetForm() {
  Object.keys(form).forEach((k) => delete form[k])
  Object.assign(form, JSON.parse(JSON.stringify(props.blank)))
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
  if (!props.save) return
  saving.value = true
  try {
    await props.save({ ...form })
    ElMessage.success('保存成功')
    dialog.value = false
    load()
  } catch (e) {
    /* 错误提示已在请求层统一处理 */
  } finally {
    saving.value = false
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm('删除后不可恢复，确认删除该条记录？', '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  try {
    await props.remove(row[props.rowKey])
    ElMessage.success('已删除')
    load()
  } catch (e) {}
}

defineExpose({ load, rows })
onMounted(load)
</script>

<style scoped>
.search {
  width: 208px;
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
  font-size: 12px;
  font-family: inherit;
  cursor: pointer;
  transition: background-color 150ms ease, color 150ms ease;
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

.cell-tag {
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 7px;
  border-radius: var(--his-radius-sm);
  font-size: 11px;
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

.cell-tag.is-danger {
  background: var(--his-danger-tint);
  color: var(--his-danger);
}

.cell-muted {
  color: var(--his-text-muted);
}

.table-foot {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 10px 16px;
  border-top: 1px solid var(--his-border);
  background: var(--his-surface-sunken);
  font-size: 12px;
  color: var(--his-text-sub);
}
</style>
