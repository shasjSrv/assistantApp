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

| name                | success   | false |
| ------------------- | --------- | ----- |
| ifSucc              | 1         | 0     |
| userName            | 姓名        | null  |
| patientNameArray    | 病人姓名数组    | null  |
| patientIDArray      | 病人ID号数组   | null  |
| patientRfIDArray    | 病人RfID号数组 | null  |
| medicineNameArray   | 药品名数组     | null  |
| medicineCountArray  | 药品数量数组    | null  |
| medicineDosageArray | 药品剂量数组    | null  |
| medicineIDArray     | 药品ID数组    | null  |



| mean | type |
| ---- | ---- |
| 病人   | 0    |
| 护士   | 1    |



### 方法二:更新送药信息

### 使用post方法，接口的地址为'/UpdateUIDMID'

### 机器人发送送端消息格式:

| name                  | type      |
| --------------------- | --------- |
| user_id               | String    |
| medicine_id_arraylist | JSONArray |
| date_yyyy             | String    |
| date_mm               | String    |
| date_dd               | String    |





### 返回值对应的含义

| mean | updateSuccess |
| ---- | ------------- |
| 失败   | 0             |
| 成功   | 1             |





### 方法三:查询patient完整信息

### 使用post方法，接口的地址为'/QueryPatientInfo'

### 返回值对应的含义

| name      | type   | mean | success | false |
| --------- | ------ | ---- | ------- | ----- |
| user_id   | int32  | 用户ID | ID号     | null  |
| user_name | String | 用户名  | 用户姓名    | null  |
| age       | int32  | 年龄   | 用户年龄    | null  |
| gender    | String | 性别   | 男/女     | null  |
| rfid      | String | rfid | rfid号   | null  |
| roomNo    | int32  | 病房号  | 病房号数字   | null  |
| berthNo   | int32  | 床位号  | 床位号数字   | null  |





# IP

### 服务器访问地址为'http//118.89.57.249:5000'(IP should be changed by real IP)



