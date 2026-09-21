<template>
  <view class="page-root">
    <StatusBar />
    <NavBar title="药品管理" />

    <view class="page-body tabbed">
      <!-- 搜索 -->
      <view class="search">
        <image class="search-icon" :src="searchIcon" />
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索药品名称或编码"
          placeholder-class="ph"
          confirm-type="search"
          @confirm="loadDrugs"
        />
        <text v-if="keyword" class="search-clear" @tap="clearKeyword">清除</text>
      </view>

      <!-- 预警 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">库存与效期预警</text>
          <text class="chip" :class="warnings.length ? 'danger' : 'success'">
            {{ warnings.length ? warnings.length + ' 项' : '正常' }}
          </text>
        </view>

        <view v-if="!warnings.length" class="empty"><text>库存与效期均在正常范围</text></view>

        <view v-for="(w, i) in warnings" :key="i" class="warn">
          <view class="col">
            <text class="warn-name">{{ w.drugsName }} {{ w.drugsFormat }}</text>
            <text class="warn-desc">{{ warnDesc(w) }}</text>
          </view>
          <text class="chip" :class="w.level === 'danger' ? 'danger' : 'warn'">{{ w.warnType }}</text>
        </view>
      </view>

      <!-- 药品库存 -->
      <view class="card">
        <view class="card-head">
          <text class="card-title">药品库存</text>
          <text class="card-meta">共 {{ drugs.length }} 种 · 点击可入库</text>
        </view>

        <view v-if="!drugs.length" class="empty"><text>没有匹配的药品</text></view>

        <view v-for="d in drugs" :key="d.id" class="drug" @tap="onStockIn(d)">
          <view class="col">
            <text class="drug-name">{{ d.drugsName }} {{ d.drugsFormat }}</text>
            <text class="drug-sub num">{{ d.drugsUnit }} · ¥{{ Number(d.drugsPrice).toFixed(2) }}</text>
          </view>
          <view class="drug-stock">
            <text class="stock-value num" :class="{ low: d.stock <= d.warnStock }">{{ d.stock }}</text>
            <text class="stock-warn">警戒 {{ d.warnStock }}</text>
          </view>
        </view>
      </view>

      <view class="note">
        <text class="note-text">新增药品与目录维护请在管理后台「药品目录」中完成，此处提供库存核对与快速入库。</text>
      </view>
    </view>

    <AppTabBar :current="1" :items="tabs" />
  </view>
</template>

<script>
import StatusBar from '@/components/StatusBar.vue'
import NavBar from '@/components/NavBar.vue'
import AppTabBar from '@/components/AppTabBar.vue'
import { pharmacyApi } from '@/api'
import { iconUrl } from '@/utils/icons'

export default {
  components: { StatusBar, NavBar, AppTabBar },
  data() {
    return {
      keyword: '',
      drugs: [],
      warnings: [],
      tabs: [
        { text: '发药', icon: 'pill', path: '/pages/pharm/dispense' },
        { text: '库存', icon: 'grid' },
        { text: '我的', icon: 'user' }
      ]
    }
  },
  computed: {
    searchIcon() {
      return iconUrl('search')
    }
  },
  onShow() {
    this.loadDrugs()
    this.loadWarnings()
  },
  methods: {
    warnDesc(w) {
      if (w.warnType === '近效期' || w.expiryDate) {
        return `有效期至 ${String(w.expiryDate || '').slice(0, 10)}`
      }
      return `现有 ${w.stock}${w.unit || '盒'} · 低于警戒库存 ${w.warnStock}`
    },
    clearKeyword() {
      this.keyword = ''
      this.loadDrugs()
    },
    async loadDrugs() {
      try {
        this.drugs = await pharmacyApi.drugs(this.keyword ? { keyword: this.keyword } : {})
      } catch (e) {}
    },
    async loadWarnings() {
      try {
        const data = await pharmacyApi.warnings()
        const low = (data.lowStock || []).map((x) => ({ ...x, warnType: '需补货' }))
        const exp = (data.nearExpiry || []).map((x) => ({ ...x, warnType: '近效期' }))
        this.warnings = [...low, ...exp]
      } catch (e) {}
    },
    onStockIn(d) {
      uni.showModal({
        title: `${d.drugsName} 入库`,
        editable: true,
        placeholderText: `当前库存 ${d.stock}，请输入入库数量`,
        success: async (res) => {
          if (!res.confirm) return
          const count = Number(res.content)
          if (!count || count <= 0) return uni.showToast({ title: '数量不合法', icon: 'none' })
          try {
            await pharmacyApi.stockIn(d.id, count)
            uni.showToast({ title: `已入库 ${count}`, icon: 'none' })
            this.loadDrugs()
            this.loadWarnings()
          } catch (e) {}
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/tokens.scss';

.page-body.tabbed {
  padding-bottom: 200rpx;
}

/* ---------- 搜索 ---------- */
.search {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 14rpx;
  height: 88rpx;
  padding: 0 24rpx;
  background-color: $color-card;
  border: 2rpx solid $color-border;
  border-radius: $radius-input;
  box-sizing: border-box;
}

.search-icon {
  width: 32rpx;
  height: 32rpx;
  flex: none;
}

.search-input {
  flex: 1;
  font-size: $font-md;
  color: $color-text;
}

.search-clear {
  font-size: $font-sm;
  color: $color-primary-text;
  flex: none;
}

/* ---------- 预警 ---------- */
.warn {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 18rpx 0;
  border-top: 2rpx solid $color-divider;
}

.warn-name {
  font-size: $font-md;
  color: $color-text;
}

.warn-desc {
  font-size: $font-xs;
  color: $color-text-sub;
}

/* ---------- 库存 ---------- */
.drug {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: $gap-sm;
  padding: 18rpx 0;
  border-top: 2rpx solid $color-divider;
}

.drug-name {
  font-size: $font-md;
  color: $color-text;
}

.drug-sub {
  font-size: $font-xs;
  color: $color-text-sub;
}

.drug-stock {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2rpx;
  flex: none;
}

.stock-value {
  font-size: $font-md;
  font-weight: 600;
  color: $color-text;
}

.stock-value.low {
  color: $color-danger;
}

.stock-warn {
  font-size: $font-xs;
  color: $color-text-muted;
}

.note {
  padding: 18rpx 22rpx;
  border-radius: $radius-sm;
  background-color: $color-sunken;
  border: 2rpx solid $color-border;
}

.note-text {
  font-size: $font-xs;
  color: $color-text-sub;
  line-height: 1.6;
}
</style>
