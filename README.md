# 东软云 HIS · 智慧医院全栈项目

一套可运行的医院信息系统（HIS）全栈示例：**Spring Boot 后端 + Vue3 管理后台 + uni-app 移动端**，覆盖挂号收费、门诊医生、医技管理、药房管理、财务管理、系统管理六大角色分支的业务闭环。

界面依据 Ardot 设计稿「智慧医院小程序-界面设计」（12 屏）落地，前后端与设计稿三者视觉与结构一致。

---

## 一、项目结构

```
his-cloud/
├── server/                 # 后端服务（Spring Boot 2.7 + MyBatis-Plus + JWT）
│   ├── src/main/java/com/neuedu/his/
│   │   ├── common/         # 统一返回体、JWT、全局异常、CORS
│   │   ├── config/         # MyBatis-Plus、拦截器配置
│   │   ├── controller/     # 7 个 REST 控制器（约 60 个接口）
│   │   ├── entity/         # 22 个实体
│   │   ├── mapper/         # 22 个 Mapper
│   │   └── service/        # 业务服务（认证/挂号/医生/药房/医技/系统/财务）
│   └── src/main/resources/
│       ├── application.properties       # 默认：H2 内存库，开箱即用
│       ├── application-mysql.properties # 可选：连本地 MySQL
│       ├── schema.sql                   # 24 张表建表脚本
│       └── data.sql                     # 种子数据（账号/科室/药品/诊断/今日业务流水）
├── admin/                  # 管理后台（Vue3 + Vite + Element Plus）
│   └── src/
│       ├── api/            # 接口封装
│       ├── components/     # 通用 CRUD 表格组件
│       ├── layout/         # 侧边栏 + 顶栏布局
│       ├── router/         # 路由与登录守卫
│       └── views/          # 12 个业务页面
├── mobile/                 # 移动端（uni-app + Vue3，可编译微信小程序 / H5）
│   └── src/
│       ├── api/            # 请求封装 + 接口定义
│       ├── components/     # 状态栏 / 导航栏 / 胶囊标签栏
│       ├── pages/          # 12 个页面（与设计稿一一对应）
│       ├── store/          # Pinia 登录态
│       ├── styles/         # 设计令牌 tokens.scss
│       └── utils/          # 内联 SVG 图标库
└── docs/                   # 接口清单与验证记录
```

---

## 二、环境要求

| 组件 | 版本 | 说明 |
|---|---|---|
| JDK | 17 或 21 | 后端编译与运行 |
| Maven | 3.8+ | 首次运行需联网拉取依赖 |
| Node.js | 18+（推荐 20/22） | 前端构建 |

> 数据库无需额外安装：默认使用 **H2 内存库**，启动时自动建表并灌入种子数据。

---

## 三、启动方式

### 1. 启动后端（必须先启动）

```bash
cd his-cloud/server
mvn spring-boot:run
```

- 服务地址：`http://localhost:8081`
- H2 控制台：`http://localhost:8081/h2-console`（JDBC URL 见 `application.properties`，用户名 `sa`，密码为空）
- **若启动报 `Port xxxxx is already in use`**：本机环境会**覆盖** `server.port`（实测被注入为随机端口），
  此时必须显式指定端口（命令行参数优先级最高）：
  ```bash
  mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
  ```

**切换到 MySQL**（可选）：

```bash
# 1) 建库 + 建表 + 灌种子数据（单文件，自带 CREATE DATABASE，可重复执行）
mysql -uroot -p < hisdb_24表_完整脚本.sql

# 2) 按需修改 application-mysql.properties 中的账号密码（默认 root / 123456）

# 3) 带 profile 启动（同样需要显式指定端口，原因见上）
mvn spring-boot:run -Dspring-boot.run.profiles=mysql \
  -Dspring-boot.run.arguments=--server.port=8081
```

- 库名须与 `application-mysql.properties` 中 JDBC URL 的库名（`hisdb`）**完全一致**。
- `schema.sql` / `data.sql` 已改为 H2 与 MySQL **双端通用**语法（`TIMESTAMPADD(MINUTE, n, CURRENT_DATE)`），
  故也可手工导入：先建库 `hisdb`，再依次执行 `schema.sql`、`data.sql`。

### 2. 启动管理后台

```bash
cd his-cloud/admin
npm install
npm run dev
```

访问 `http://localhost:5174`（已配置 `/api` 代理到 8081）。

### 3. 启动移动端

```bash
cd his-cloud/mobile
npm install

# 微信小程序（产物在 dist/dev/mp-weixin，用微信开发者工具导入）
npm run dev:mp-weixin

# 或 H5 预览
npm run dev:h5
```

> 小程序端需将 `src/api/config.js` 中的 `BASE_URL` 指向后端地址；微信开发者工具中请开启「不校验合法域名」。

---

## 四、演示账号

密码统一为 `123456`。

| 角色 | 账号 | 姓名 | 可访问模块 |
|---|---|---|---|
| 医院管理员 | `admin` | 系统管理员 | 系统管理、用户/字典/排班维护 |
| 挂号收费员 | `reg01` | 收费员小周 | 工作台、现场挂号、收费结算、退号 |
| 门诊医生 | `chenjx` | 陈景行 | 候诊接诊、门诊病历、成药处方、医技申请 |
| 医技医生 | `wangxm` | 王雪梅 | 检验/检查/处置执行与结果录入 |
| 药房操作员 | `pharm01` | 药房小何 | 处方审核发药、药品与库存管理 |
| 财务管理员 | `fin01` | 财务小郑 | 营收统计、工作量统计、费用科目 |

---

## 五、核心业务闭环

```
挂号收费员                门诊医生                 医技医生           药房操作员
    │                        │                        │                  │
 现场挂号 ──▶ 候诊队列 ──▶ 接诊 / 写病历              │                  │
    │                        │                        │                  │
    │                        ├─ 开立医技申请 ────────▶ 执行 / 录结果      │
    │                        │                        │                  │
    │                        └─ 开立处方（自动生成费用）│                  │
    │                        │                        │                  │
 收费结算 ◀──────────────────┘                        │                  │
（生成发票 + 费用流水）                                 │                  │
    │                                                                   │
    └──────────────────────── 待发药处方 ──────────────────────────────▶ 确认发药
                                                                            │
                                                                       扣减库存
```

财务管理员可随时查看营收总览、近 7 日趋势、费用构成与医生工作量。

---

## 六、关键设计说明

### 数据层
- `schema.sql` 24 张表，覆盖 `user` / `department` / `registlevel` / `settlecategory` / `constanttype` / `constantitem` / `disease` / `fmeditem` / `drugs` / `medicalcard` / `register` / `scheduling` / `medicalrecord` / `prescription` / `prescriptiondetailed` / `patientcosts` / `invoice` / `checkapply` 等。
- `data.sql` 中的业务日期基于 `CURRENT_DATE` 动态生成，**任何一天启动都能看到"今日"的挂号与处方流水**。

### 后端
- 统一返回体 `JsonResult`：`{ result, errMsg, data, token }`，前端按 `result` 判定成败。
- JWT 鉴权：登录下发 token，拦截器校验（`/api/auth/login` 与只读字典接口放行）。
- 业务规则内置：库存不足拦截、号源上限校验、重复挂号校验、发药扣减库存、收费生成发票与费用流水。

### 前端
- 移动端严格复用设计令牌（`src/styles/tokens.scss`）：背景 `#F9F9F9`、卡片白底圆角 12、主蓝 `#014DB2`、深蓝渐变 hero、语义三色、Noto Sans SC。
- 图标全部为**内联 SVG**（`src/utils/icons.js`），不使用 emoji 或图标字体，H5 与小程序双端一致。
- 管理后台通过通用 `CrudTable` 组件承载 8 个字典/资料页，新增、编辑、删除、搜索开箱可用。

---

## 七、接口清单

见 [`docs/API.md`](docs/API.md)。

---

## 八、验证记录

见 [`docs/VERIFY.md`](docs/VERIFY.md)，包含后端编译、启动、端到端接口调用与前端构建的实际结果。
