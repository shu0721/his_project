/**
 * 图标库（内联 SVG → data URI，H5 与小程序通用）
 * 统一 1.7px 描边、圆角端点，线性风格，不依赖图标字体。
 * 默认描边色为品牌绿 #007a3f。
 */

const P = '#007a3f' // 品牌绿
const S = '#5c6a60' // 次要
const M = '#8a978e' // 弱化
const D = '#1f2b24' // 深色

const svg = (body, size = 24) =>
  `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">${body}</svg>`

const g = (body, color = P, w = 1.7) =>
  `<g stroke="${color}" stroke-width="${w}" stroke-linecap="round" stroke-linejoin="round" fill="none">${body}</g>`

const rect = (x, y, w, h, r = 2) => `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${r}"/>`

export const ICONS = {
  /* ---------- 通用 ---------- */
  back: svg(g('<path d="M15 5.5L8.5 12L15 18.5"/>', D, 1.8)),
  arrowRight: svg(g('<path d="M9 6L15 12L9 18"/>', M)),
  plus: svg(g('<path d="M12 5.5V18.5M5.5 12H18.5"/>', P, 1.8)),
  search: svg('<circle cx="10.5" cy="10.5" r="6.2" stroke="' + M + '" stroke-width="1.7"/>' + g('<path d="M15.2 15.2L19.5 19.5"/>', M)),
  check: svg(g('<path d="M5 12.5L9.5 17L19 7"/>', '#ffffff', 2)),
  close: svg(g('<path d="M6 6L18 18M18 6L6 18"/>', S, 1.8)),
  clock: svg('<circle cx="12" cy="12" r="8.2" stroke="' + P + '" stroke-width="1.7"/>' + g('<path d="M12 7.5V12L15 14"/>')),
  bell: svg(g('<path d="M6.5 10a5.5 5.5 0 0 1 11 0c0 4 1.5 5.5 1.5 5.5H5S6.5 14 6.5 10Z"/>' + '<path d="M10.5 18.5a2 2 0 0 0 3 0"/>')),
  user: svg('<circle cx="12" cy="8.5" r="3.5" stroke="' + P + '" stroke-width="1.7"/>' + g('<path d="M5.5 19.5c0-3.3 2.9-5.5 6.5-5.5s6.5 2.2 6.5 5.5"/>')),
  refresh: svg(g('<path d="M19 12a7 7 0 1 1-2.1-5"/>' + '<path d="M19 4.5V9h-4.5"/>')),
  more: svg(g('<path d="M6 12h.01M12 12h.01M18 12h.01"/>', S, 2.4)),

  /* ---------- 挂号收费 ---------- */
  calendar: svg(rect(4, 5.5, 16, 14.5, 2.5) + g('<path d="M4 10h16M8.5 3.5V7M15.5 3.5V7"/>')),
  cash: svg(rect(3.5, 6.5, 17, 11, 2.5) + g('<path d="M3.5 10.5h17"/>') + `<circle cx="12" cy="14" r="1.5" stroke="${P}" stroke-width="1.7" fill="none"/>`),
  card: svg(rect(3, 5.5, 18, 13, 2.5) + g('<path d="M3 9.5h18M6.5 14H10"/>')),
  ticket: svg(g('<path d="M4 8.5V6.5h16v2a2 2 0 0 0 0 4v2H4v-2a2 2 0 0 0 0-4Z"/>')),
  refund: svg(g('<path d="M5 9.5h10.5a4 4 0 0 1 0 8H9"/>' + '<path d="M8 6.5L5 9.5L8 12.5"/>')),
  list: svg(g('<path d="M8 7h12M8 12h12M8 17h12"/>' + '<path d="M4.5 7h.01M4.5 12h.01M4.5 17h.01"/>', P, 1.9)),

  /* ---------- 医生站 ---------- */
  file: svg(g('<path d="M6.5 3.5H14L18.5 8v12.5h-12V3.5Z"/>' + '<path d="M13.5 3.5V8.5h5"/>')),
  stethoscope: svg(
    g('<path d="M6 4v5a4 4 0 0 0 8 0V4"/>' + '<path d="M10 13v3.5a4 4 0 0 0 8 0V13"/>') +
      `<circle cx="18" cy="11" r="1.5" stroke="${P}" stroke-width="1.7" fill="none"/>`
  ),
  flask: svg(g('<path d="M9.5 3.5h5M10.5 3.5V9l-4.2 7.7c-.5.9.2 1.8 1.2 1.8h9c1 0 1.7-.9 1.2-1.8L13.5 9V3.5"/>')),
  scissors: svg(
    `<circle cx="7" cy="18" r="2.4" stroke="${P}" stroke-width="1.7"/>` +
      `<circle cx="7" cy="6" r="2.4" stroke="${P}" stroke-width="1.7"/>` +
      g('<path d="M9 7.5L19.5 18M9 16.5L19.5 6"/>')
  ),
  pill: svg(
    `<rect x="3.2" y="9" width="17.6" height="7" rx="3.5" stroke="${P}" stroke-width="1.7" transform="rotate(-45 12 12.5)"/>` +
      g('<path d="M9.5 9.5l5 5"/>')
  ),
  leaf: svg(g('<path d="M19 5c0 8-4.5 12.5-11 12.5 0-7 4.5-11 11-12.5Z"/>' + '<path d="M8 19.5C11 15 14 11.5 18 8"/>')),
  layers: svg(g('<path d="M12 3.5L20.5 8L12 12.5L3.5 8L12 3.5Z"/>' + '<path d="M3.5 12.5L12 17L20.5 12.5"/>')),
  wallet: svg(
    rect(3.5, 6, 17, 12.5, 2.5) +
      g('<path d="M3.5 10.5H17"/>') +
      `<circle cx="17" cy="14.5" r="1.3" stroke="${P}" stroke-width="1.7" fill="none"/>`
  ),

  /* ---------- 医技 / 药房 ---------- */
  microscope: svg(
    g('<path d="M10 20.5h8M12.5 4l3.5 3.5-6 6L6.5 10 12.5 4Z"/>' + '<path d="M9 13.5l3 3.5M6 20.5c0-3 2-5 5-5"/>')
  ),
  scan: svg(
    g(
      '<path d="M4 8.5v-2a2 2 0 0 1 2-2h2M20 8.5v-2a2 2 0 0 0-2-2h-2M4 15.5v2a2 2 0 0 0 2 2h2M20 15.5v2a2 2 0 0 1-2 2h-2"/>' +
        '<path d="M4 12h16"/>'
    )
  ),
  box: svg(g('<path d="M3.5 8L12 4l8.5 4v8L12 20l-8.5-4V8Z"/>' + '<path d="M3.5 8L12 12l8.5-4M12 12v8"/>')),
  truck: svg(
    g('<path d="M3.5 7.5h9v9h-9v-9Z"/>' + '<path d="M12.5 11h4l3 3v2.5h-7V11Z"/>') +
      `<circle cx="7" cy="17.5" r="1.7" stroke="${P}" stroke-width="1.7" fill="none"/>` +
      `<circle cx="16.5" cy="17.5" r="1.7" stroke="${P}" stroke-width="1.7" fill="none"/>`
  ),

  /* ---------- 财务 / 系统 ---------- */
  chart: svg(g('<path d="M4 20h16"/>' + '<path d="M7.5 20V12M12 20V6M16.5 20V14.5"/>')),
  trend: svg(g('<path d="M4 16.5L9.5 11L13 14.5L20 7.5"/>' + '<path d="M15 7.5H20V12.5"/>')),
  gear: svg(
    `<circle cx="12" cy="12" r="2.8" stroke="${P}" stroke-width="1.7" fill="none"/>` +
      g('<path d="M12 3.5v2M12 18.5v2M4.9 7.2l1.7 1.2M17.4 15.6l1.7 1.2M4.9 16.8l1.7-1.2M17.4 8.4l1.7-1.2"/>')
  ),
  shield: svg(g('<path d="M12 3.5l7 2.5v6c0 4-3 7-7 8.5-4-1.5-7-4.5-7-8.5v-6l7-2.5Z"/>' + '<path d="M9 12l2 2 4-4"/>')),
  key: svg(
    `<circle cx="8.5" cy="12" r="3.4" stroke="${P}" stroke-width="1.7" fill="none"/>` +
      g('<path d="M12 12h8.5M17 12v3M20 12v2.5"/>')
  ),
  grid: svg(rect(4, 4, 7, 7, 1.8) + rect(13, 4, 7, 7, 1.8) + rect(4, 13, 7, 7, 1.8) + rect(13, 13, 7, 7, 1.8), 24),
  ledger: svg(g('<path d="M5 4.5h11.5a2 2 0 0 1 2 2v13H7a2 2 0 0 1-2-2v-13Z"/>' + '<path d="M9 8.5h6M9 12.5h6"/>')),
  building: svg(g('<path d="M5 20.5V6.5l7-3 7 3v14"/>' + '<path d="M5 20.5H19M10 20.5v-5h4v5"/>')),
  tag: svg(
    g('<path d="M4 11V5.5A1.5 1.5 0 0 1 5.5 4H11l9 9-7 7-9-9Z"/>') +
      `<circle cx="8" cy="8.5" r="1.3" stroke="${P}" stroke-width="1.7" fill="none"/>`
  ),
  logout: svg(g('<path d="M15 8.5V6a2 2 0 0 0-2-2H6.5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2H13a2 2 0 0 0 2-2v-2.5"/>' + '<path d="M10 12h10M17 9l3 3-3 3"/>'))
}

/* grid 的 rect 组合需要显式注入描边色 */
ICONS.grid = svg(g(rect(4, 4, 7, 7, 1.8) + rect(13, 4, 7, 7, 1.8) + rect(4, 13, 7, 7, 1.8) + rect(13, 13, 7, 7, 1.8)))

export const ICON_COLORS = {
  primary: P,
  sub: S,
  muted: M,
  dark: D,
  white: '#ffffff'
}

/** 取图标的 data URI */
export function iconUrl(name) {
  const s = ICONS[name]
  if (!s) return ''
  // #ifdef H5
  return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(s)
  // #endif
  // #ifndef H5
  return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(s)))
  // #endif
}

export default ICONS
