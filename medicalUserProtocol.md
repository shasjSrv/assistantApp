# Protocol

### 方法一:查询userID

### 使用post方法，接口的地址为'/QueryID'

### 机器人发送送端消息格式:

| name    | type  |
| ------- | ----- |
| user_id | int32 |

### 服务器返回消息格式

| name                | type      |
| ------------------- | :-------- |
| isSuccess           | int32     |
| userName            | String    |
| type                | int32     |
| patientNameArray    | JSONArray |
| patientIDArray      | JSONArray |
| patientRfIDArray    | JSONArray |
| medicineNameArray   | JSONArray |
| medicineCountArray  | JSONArray |
| medicineDosageArray | JSONArray |
| medicineIDArray     | JSONArray |





### 返回值对应的含义



| mean | ifSucc | userName | patientNameArray | patientIDArray |
| ---- | ------ | -------- | ---------------- | -------------- |
| 成功   | 1      | 姓名       | 病人姓名数组           | 评人ID号数组        |
| 失败   | 0      | null     | null             | null           |



| mean | type |
| ---- | ---- |
| 病人   | 0    |
| 护士   | 1    |

| mean | medicineNameArray | medicineCountArray | medicineDosageArray |
| ---- | ----------------- | ------------------ | ------------------- |
| 成功   | 药品名               | 药品数量               | 药品剂量                |
| 失败   | null              | null               | null                |



### 服务器访问地址为'http//118.89.57.249:5000'(IP should be changed by real IP)



### 方法二:更新送药信息

### 使用post方法，接口的地址为'/UpdateUIDMID'

### 机器人发送送端消息格式:

| name                  | type   |
| --------------------- | ------ |
| user_id               | String |
| medicine_id_arraylist | int32  |
| date_yyyy             | String |
| date_mm               | String |
| date_dd               | String |





### 返回值对应的含义

| mean | updateSuccess |
| ---- | ------------- |
| 失败   | 0             |
| 成功   | 1             |





