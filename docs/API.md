# 接口清单

- 基地址：`http://localhost:8081`
- 统一返回体：

```json
{ "result": true, "errMsg": null, "data": {}, "token": null }
```

`result=true` 时业务数据在 `data`；`result=false` 时错误信息在 `errMsg`。登录接口额外返回 `token`。

- 鉴权：请求头 `Authorization: <token>`。登录接口与只读字典接口无需 token。

---

## 一、认证与用户 `/api/auth`

| 方法 | 路径 | 说明 | 入参 |
|---|---|---|---|
| POST | `/api/auth/login` | 登录 | `{ userName, password }` |
| GET | `/api/auth/roles` | 登录页角色字典 | — |
| GET | `/api/auth/info/{userId}` | 用户信息 | — |
| GET | `/api/auth/users` | 用户列表 | `useType? keyword?` |
| POST | `/api/auth/users` | 新增用户 | User |
| PUT | `/api/auth/users` | 修改用户 | User |
| POST | `/api/auth/users/{id}/toggle?enabled=` | 启用/停用 | — |
| POST | `/api/auth/users/{id}/reset-password` | 重置密码为 123456 | — |
| POST | `/api/auth/change-password` | 修改密码 | `{ uid, oldPwd, newPwd }` |

## 二、挂号收费 `/api/register`

| 方法 | 路径 | 说明 | 入参 |
|---|---|---|---|
| GET | `/api/register/overview` | 工作台概览（今日实收等） | — |
| GET | `/api/register/departments` | 科室列表 | `deptType?` |
| GET | `/api/register/regist-levels` | 挂号级别 | — |
| GET | `/api/register/settle-categories` | 结算类别 | — |
| GET | `/api/register/doctors` | 出诊医生 | `deptId? date? noon?` |
| GET | `/api/register/list` | 挂号记录 | `date? visitState? userId? deptId? keyword?` |
| POST | `/api/register/create` | 现场挂号 | `{ deptId, userId, registLeId, settleId, realName, gender, age, idnumber, homeAddress, noon, channel }` |
| POST | `/api/register/{id}/refund` | 退号 | `operId?` |
| GET | `/api/register/{id}/charge-list` | 待收费清单 | — |
| POST | `/api/register/pay` | 收费结算 | `{ registId, settleId, payMethod }` |

## 三、门诊医生 `/api/doctor`

| 方法 | 路径 | 说明 | 入参 |
|---|---|---|---|
| GET | `/api/doctor/waiting-list` | 候诊列表 | `doctorId? visitState? date?` |
| GET | `/api/doctor/overview` | 医生站概览 | — |
| POST | `/api/doctor/{registId}/accept` | 接诊 | — |
| GET | `/api/doctor/medical-record/{registId}` | 读取病历 | — |
| POST | `/api/doctor/medical-record` | 保存病历 | `{ registId, doctorId, readme, present, history, physique, allergy, diagnosis, handling, diseaseIds[], caseState, finish }` |
| GET | `/api/doctor/drugs` | 药品检索 | `keyword?` |
| GET | `/api/doctor/fmeditems` | 医技项目 | `keyword? recordType?` |
| GET | `/api/doctor/diseases` | 诊断检索 | `keyword?` |
| GET | `/api/doctor/prescriptions` | 处方列表 | `registId?` |
| GET | `/api/doctor/prescriptions/{id}` | 处方详情 | — |
| POST | `/api/doctor/prescriptions` | 开立处方 | `{ registId, doctorId, prescriptionName, items:[{ drugsId, amount, days, frequency, drugsUsage, dosage }] }` |
| DELETE | `/api/doctor/prescriptions/{id}` | 作废处方 | — |
| POST | `/api/doctor/check-applies` | 医技申请 | `{ registId, doctorId, items:[{ itemId, num, objective?, position?, isUrgent? }] }` |

## 四、药房管理 `/api/pharmacy`

| 方法 | 路径 | 说明 | 入参 |
|---|---|---|---|
| GET | `/api/pharmacy/overview` | 药房概览 | — |
| GET | `/api/pharmacy/prescriptions` | 处方列表 | `state? keyword?` |
| POST | `/api/pharmacy/prescriptions/{id}/dispense` | 确认发药（扣减库存） | — |
| GET | `/api/pharmacy/drugs` | 药品列表 | `keyword? typeId?` |
| GET | `/api/pharmacy/drugs/{id}` | 药品详情 | — |
| POST | `/api/pharmacy/drugs` | 新增/修改药品 | Drugs |
| DELETE | `/api/pharmacy/drugs/{id}` | 停用药品 | — |
| POST | `/api/pharmacy/drugs/{id}/stock-in?count=` | 药品入库 | — |
| GET | `/api/pharmacy/warnings` | 库存与效期预警 | `days?` |

处方状态：`1` 待收费、`2` 已收费待发药、`3` 已发药。

## 五、医技管理 `/api/tech`

| 方法 | 路径 | 说明 | 入参 |
|---|---|---|---|
| GET | `/api/tech/overview` | 医技概览 | — |
| GET | `/api/tech/applies` | 申请列表 | `state? recordType?` |
| POST | `/api/tech/applies/{id}/start` | 开始执行 | — |
| POST | `/api/tech/applies/{id}/result` | 录入结果 | `{ result, ... }` |

申请状态：`0` 待执行、`1` 待执行、`2` 执行中、`3` 已完成。项目类型：`1` 检查、`2` 检验、`4` 处置。

## 六、财务管理 `/api/finance`

| 方法 | 路径 | 说明 | 入参 |
|---|---|---|---|
| GET | `/api/finance/revenue` | 营收总览（含近 7 日趋势与费用构成） | `date?` |
| GET | `/api/finance/doctor-workload` | 医生工作量统计 | `date?` |

## 七、系统管理 `/api/system`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET/POST | `/api/system/departments` | 科室列表 / 保存 |
| DELETE | `/api/system/departments/{id}` | 删除科室 |
| GET/POST | `/api/system/constant-types` | 常数类别 |
| GET/POST | `/api/system/constant-items` | 常数项（`typeId?`） |
| GET/POST | `/api/system/regist-levels` | 挂号级别 |
| GET/POST | `/api/system/settle-categories` | 结算类别 |
| GET/POST | `/api/system/diseases` | 诊断目录（`keyword?`） |
| GET | `/api/system/dise-categories` | 诊断类别 |
| GET/POST | `/api/system/expense-classes` | 费用科目 |
| GET/POST | `/api/system/fmed-items` | 非药品收费项目（`keyword? recordType?`） |
| GET/POST | `/api/system/scheduling` | 医生排班（`date? deptId?`） |
| DELETE | `/api/system/scheduling/{id}` | 删除排班 |
