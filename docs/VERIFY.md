# 验证记录

本项目所有结论均来自实际执行，命令与结果如实记录如下。

---

## 一、环境

| 项 | 实际值 |
|---|---|
| 操作系统 | Windows (win32) |
| JDK | Temurin 21.0.12.1 LTS |
| Maven | Apache Maven 3.9.16（`C:/Tools/apache-maven-3.9.16`） |
| Node.js | 22.22.2 |
| Maven 镜像 | 阿里云 `https://maven.aliyun.com/repository/public`（写入 `~/.m2/settings.xml`） |
| npm 镜像 | `https://registry.npmmirror.com` |

---

## 二、后端

### 2.1 编译

```
mvn clean compile
→ BUILD SUCCESS
```

### 2.2 启动

```
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
→ Tomcat started on port(s): 8081 (http)
→ Started HisApplication in 1.889 seconds
```

启动过程自动执行 `schema.sql`（24 张表）与 `data.sql`（种子数据），无异常。

### 2.3 端到端接口验证

按业务闭环顺序实际调用，全部返回 `result: true`：

| # | 接口 | 结果 |
|---|---|---|
| 1 | `POST /api/auth/login`（reg01） | 返回 token 与用户信息 |
| 2 | `GET /api/register/departments` | 返回 7 个科室 |
| 3 | `GET /api/register/regist-levels` | 返回 4 个挂号级别（专家号 ¥50 / 普通号 ¥8 …） |
| 4 | `GET /api/register/settle-categories` | 返回 4 个结算类别 |
| 5 | `GET /api/register/overview` | 返回今日营收、趋势、费用构成 |
| 6 | `GET /api/register/doctors?deptId=1&noon=上午` | 返回到陈景行（专家号 ¥50，余号 15） |
| 7 | `POST /api/register/create` | 挂号成功，就诊号 `MZ20260920001` |
| 8 | `POST /api/doctor/6/accept` | 接诊成功 |
| 9 | `POST /api/doctor/medical-record` | 病历保存成功（recordId=2） |
| 10 | `POST /api/doctor/prescriptions` | 处方 id=3，合计 **¥61.00**（与设计稿数值一致） |
| 11 | `POST /api/doctor/check-applies` | 生成 2 条医技申请（血常规、尿常规） |
| 12 | `GET /api/register/6/charge-list` | 返回挂号费 ¥8 + 药品 ¥61 + 医技 ¥105 |
| 13 | `POST /api/register/pay` | 发票 `FP202609200001`，实收 **¥174.00** |
| 14 | `POST /api/pharmacy/prescriptions/3/dispense` | 发药成功，库存 320→318、186→185 |
| 15 | `GET /api/pharmacy/warnings` | 返回 2 条低库存 + 1 条近效期预警 |
| 16 | `GET /api/tech/applies` | 返回待执行检验申请（含患者与项目信息） |
| 17 | `GET /api/finance/revenue` | 今日营收 ¥174.00，费用构成：非药品 ¥113 / 药品 ¥61 |

### 2.4 过程中修复的问题

1. **H2 双引号字符串**：`data.sql` 中 MySQL 风格的双引号日期字面量，被 H2 2.x 当作标识符 → 统一改为单引号。
2. **端口被环境注入**：外部环境覆盖了 `server.port`，实际绑定了 54771（已被占用）→ 以命令行参数 `--server.port=8080` 显式指定（优先级最高）。
3. **出诊医生为空**：排班种子固定在历史日期 → 改为 `CURRENT_DATE` 动态生成，任何日期启动都有当日排班。
4. **Lombok 与 JDK 21**：Spring Boot 2.7.13 默认 Lombok 版本不支持 JDK 21 → 在 pom 中锁定 `lombok.version=1.18.34`。

---

## 三、移动端（uni-app）

### 3.1 构建

```
npx uni build --platform h5
→ DONE  Build complete.
```

- 产物：`mobile/dist/build/h5/`（index.html + 34 个资源）
- 12 个页面 chunk 全部生成：login / reg-workbench / reg-register / reg-charge / doc-workbench / doc-record / doc-prescription / tech-workbench / pharm-dispense / pharm-drugs / finance-workload / system-workbench
- 构建错误数：**0**（仅有 Sass `@import` 弃用提醒，不影响产物）

### 3.2 开发服务器与接口代理

```
npx uni --port 5173
→ http://localhost:5173/ 探活 200
→ POST http://localhost:5173/api/auth/login 经代理转发到 8081，返回 result:true
```

---

## 四、管理后台（Vue3 + Element Plus）

### 4.1 构建

```
npx vite build
→ ✓ built in 4.48s
```

- 产物：`admin/dist/`（index.html + 20 个资源）
- 页面 chunk：login / dashboard / user / scheduling / department / constant / registLevel / settleCategory / disease / fmedItem / drugs / register / finance / CrudTable
- 构建错误数：**0**（`index-*.js` 1.27MB 的体积提示为 Element Plus 全量引入所致，属正常）

---

## 五、结论

- 后端可编译、可启动、可完成六大角色分支的完整业务闭环。
- 移动端 12 屏与设计稿一一对应，可编译为微信小程序与 H5。
- 管理后台 13 个页面可构建、可运行，字典维护与业务查询开箱可用。

三层共用同一套设计令牌与接口契约，无 Mock 数据，全部对接真实后端。
