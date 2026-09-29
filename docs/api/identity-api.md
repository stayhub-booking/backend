# API Contract - Identity & User Administration

Tài liệu định nghĩa API cho đăng ký, đăng nhập, hồ sơ người dùng và quản trị tài khoản của StayHub.

## 1. Trạng thái triển khai

Ngày rà soát contract: `2026-09-28`.

### Nền tảng đã có

- Entity `User`, `Role`, `Permission` và `UserRole`.
- Migration `V1__create_identity_tables.sql` cho `users`, `roles`, `permissions`, `user_roles`
  và `role_permissions`.
- Seed ba role hệ thống và bốn permission quản trị; các permission hiện được gán cho `ADMIN`.
- Các role dự kiến: `CUSTOMER`, `HOTEL_OWNER`, `ADMIN`.
- Các trạng thái user: `ACTIVE`, `INACTIVE`, `PENDING`, `BANNED`.
- `ApiResponse<T>`, `ResponseCode`, `ErrorCode`, `AppException` và `GlobalExceptionHandler`.
- `PasswordEncoder` dùng delegating password encoder của Spring Security.
- `RoleRepository` có truy vấn tìm role đang hoạt động theo `roleKey`.
- MapStruct mapper chuyển `User` thành `UserResponse` và chuyển `UserRole` thành role key.

### Đang triển khai

- `POST /api/v1/auth/register` đã có controller, service, repository, mapper và response.
- Luồng hiện tại chuẩn hóa email, kiểm tra trùng email/phone, lấy role `CUSTOMER`, hash password,
  tạo user trạng thái `ACTIVE` và lưu `UserRole` bằng cascade.
- Validation hiện chưa đáp ứng đầy đủ contract, vì vậy endpoint vẫn ở trạng thái `IMPLEMENTING`.
- Spring Security đang tắt CSRF và `permitAll()` toàn bộ request để phục vụ giai đoạn phát triển.
  Đây chưa phải cấu hình bảo mật hoàn chỉnh.

### Chưa triển khai

- Đăng nhập, JWT và phân quyền endpoint theo role/permission.
- API hồ sơ người dùng và quản trị người dùng.
- DTO cho các endpoint ngoài đăng ký.
- `AuthenticationEntryPoint` và `AccessDeniedHandler` trả lỗi `401`/`403` theo `ApiResponse`.
- Chưa có integration test cho contract.

Vì vậy, chỉ endpoint đăng ký có trạng thái `IMPLEMENTING`; các endpoint còn lại là `PLANNED`.

## 2. Điểm cần đồng bộ trước khi code

Mô hình Java và migration hiện tại đã thống nhất theo cấu trúc:

```text
users
roles ──< role_permissions >── permissions
user_roles(id, user_id, role_id, assigned_at)
```

Các permission quản trị được seed hiện tại:

```text
USER_READ
USER_STATUS_UPDATE
USER_ROLE_UPDATE
ROLE_READ
```

Các permission trên hiện chỉ được gán cho role `ADMIN`. Response user chỉ trả danh sách role key;
không trả toàn bộ permission để tránh payload lớn và phụ thuộc trực tiếp vào chi tiết phân quyền nội bộ.

`User.id` là `UUID` nhưng `UserRepository` vẫn khai báo `JpaRepository<User, Long>`. Phải đổi
sang `UUID` trước khi triển khai các API truy vấn user theo ID.

## 3. Trách nhiệm module

- Đăng ký và xác thực người dùng.
- Cung cấp thông tin tài khoản hiện tại.
- Cập nhật hồ sơ cơ bản và mật khẩu.
- Quản lý trạng thái tài khoản.
- Quản lý role của người dùng.
- Ngăn người dùng tự cấp quyền quản trị.

Module này không quản lý thông tin chi tiết của khách sạn, phòng, booking hoặc thanh toán.

## 4. Quy tắc chung

### Chuẩn hóa email và số điện thoại

- Email phải được `trim` và chuyển về chữ thường trước khi kiểm tra trùng và lưu.
- Số điện thoại phải được chuẩn hóa trước khi kiểm tra trùng.
- So sánh email đăng nhập không phân biệt hoa thường.

### Password

- Từ 8 đến 72 ký tự.
- Có ít nhất một chữ hoa, một chữ thường, một chữ số và một ký tự đặc biệt.
- Chỉ lưu password hash; không lưu hoặc log password gốc.
- Khi đăng nhập sai, response không được tiết lộ email có tồn tại hay không.

### Role

| Role key | Ý nghĩa |
|---|---|
| `CUSTOMER` | Khách hàng tìm và đặt phòng |
| `HOTEL_OWNER` | Chủ khách sạn quản lý khách sạn và phòng |
| `ADMIN` | Quản trị người dùng và dữ liệu hệ thống |

Đăng ký công khai chỉ tạo role `CUSTOMER`. Role `HOTEL_OWNER` và `ADMIN` chỉ được gán bởi Admin trong phạm vi contract hiện tại.

### User status

| Status | Ý nghĩa | Có thể đăng nhập |
|---|---|---|
| `PENDING` | Tài khoản đang chờ kích hoạt hoặc xét duyệt | Không |
| `ACTIVE` | Tài khoản hoạt động bình thường | Có |
| `INACTIVE` | Tài khoản bị tạm ngừng | Không |
| `BANNED` | Tài khoản bị cấm | Không |

Trong giai đoạn MVP chưa có email verification, đăng ký khách hàng thành công tạo tài khoản ở trạng thái `ACTIVE`. `PENDING` được giữ cho luồng duyệt chủ khách sạn trong tương lai.

### Access token

- API bảo vệ sử dụng JWT Bearer token.
- Access token dự kiến có thời hạn 15 phút.
- Claim tối thiểu: `sub` là user ID, `roles` là danh sách role key.
- Tài liệu này chưa định nghĩa refresh token hoặc logout phía server vì schema hiện chưa có nơi lưu refresh token/revocation. Khi bổ sung tính năng, phải cập nhật database và contract trước.

## 5. Danh sách endpoint

| # | Method | URL | Quyền | Mô tả | Trạng thái |
|---:|---|---|---|---|---|
| 1 | `POST` | `/api/v1/auth/register` | Anonymous | Đăng ký tài khoản khách hàng | `IMPLEMENTING` |
| 2 | `POST` | `/api/v1/auth/login` | Anonymous | Đăng nhập và nhận access token | `PLANNED` |
| 3 | `GET` | `/api/v1/users/me` | Authenticated | Lấy tài khoản hiện tại | `PLANNED` |
| 4 | `PATCH` | `/api/v1/users/me` | Authenticated | Cập nhật hồ sơ cơ bản | `PLANNED` |
| 5 | `POST` | `/api/v1/users/me/password` | Authenticated | Đổi mật khẩu | `PLANNED` |
| 6 | `GET` | `/api/v1/admin/users` | ADMIN | Danh sách người dùng | `PLANNED` |
| 7 | `GET` | `/api/v1/admin/users/{userId}` | ADMIN | Chi tiết người dùng | `PLANNED` |
| 8 | `PATCH` | `/api/v1/admin/users/{userId}/status` | ADMIN | Cập nhật trạng thái | `PLANNED` |
| 9 | `PUT` | `/api/v1/admin/users/{userId}/roles` | ADMIN | Thay thế danh sách role | `PLANNED` |
| 10 | `GET` | `/api/v1/admin/roles` | ADMIN | Danh sách role có thể gán | `PLANNED` |

---

## 6. Auth APIs

### 6.1. Đăng ký tài khoản khách hàng

- **Method:** `POST`
- **URL:** `/api/v1/auth/register`
- **Quyền:** `Anonymous`
- **Trạng thái:** `IMPLEMENTING`
- **Mô tả:** Tạo tài khoản mới với role mặc định `CUSTOMER` và trạng thái `ACTIVE`.

#### Request body

| Field | Type | Required | Validation |
|---|---|---:|---|
| `email` | string | Có | `@NotBlank`, `@Email`; service trim và chuyển về chữ thường |
| `password` | string | Có | `@NotBlank`, tối thiểu 8 ký tự |
| `fullName` | string | Có | `@NotBlank`, tối đa 100 ký tự |
| `phone` | string | Có | Service trim và kiểm tra trùng; validation DTO chưa hoàn thiện |
| `avatarUrl` | string hoặc null | Không | URL ảnh đại diện; tối đa 255 ký tự theo database |

```json
{
  "email": "customer@example.com",
  "password": "StayHub@123",
  "fullName": "Nguyễn Văn An",
  "phone": "+84901234567",
  "avatarUrl": null
}
```

Request không nhận `role` hoặc `status` để tránh tự cấp quyền.

#### Response - `201 Created`

```json
{
  "success": true,
  "code": "USER_201_001",
  "message": "User created successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "customer@example.com",
    "fullName": "Nguyễn Văn An",
    "phone": "+84901234567",
    "avatarUrl": null,
    "status": "ACTIVE",
    "roles": ["CUSTOMER"],
    "createdAt": "2026-09-28T08:30:00Z"
  },
  "timestamp": "2026-09-28T08:30:00Z"
}
```

#### Error responses

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `400` | `COMMON_400_001` | Request vi phạm validation |
| `404` | `ROLE_404_001` | Không tìm thấy role `CUSTOMER` đang hoạt động |
| `409` | `USER_409_001` | Email đã tồn tại |
| `409` | `USER_409_002` | Số điện thoại đã tồn tại |

#### Business rules

1. Chuẩn hóa email và phone trước khi kiểm tra trùng.
2. Hash password trước khi tạo entity.
3. Tạo `User` và `UserRole(CUSTOMER)` trong cùng transaction.
4. Không trả `passwordHash` trong bất kỳ response nào.

#### Khoảng cách giữa code hiện tại và contract mục tiêu

- `phone` chưa có `@NotBlank` và pattern; gửi `null` hiện có thể gây lỗi trong service khi gọi
  `trim()`.
- Password mới chỉ kiểm tra tối thiểu 8 ký tự; chưa kiểm tra tối đa 72 ký tự, chữ hoa, chữ
  thường, chữ số và ký tự đặc biệt.
- `fullName` chưa được trim và chưa kiểm tra tối thiểu 2 ký tự.
- `avatarUrl` chưa có validation URL hoặc `@Size(max = 255)`.
- Chưa có integration test xác nhận transaction, cascade `UserRole` và response thực tế.

---

### 6.2. Đăng nhập

- **Method:** `POST`
- **URL:** `/api/v1/auth/login`
- **Quyền:** `Anonymous`
- **Trạng thái:** `PLANNED`
- **Mô tả:** Xác thực email/password và cấp JWT access token.

#### Request body

| Field | Type | Required | Validation |
|---|---|---:|---|
| `email` | string | Có | Email hợp lệ |
| `password` | string | Có | Không được để trống |

```json
{
  "email": "customer@example.com",
  "password": "StayHub@123"
}
```

#### Response - `200 OK`

```json
{
  "success": true,
  "code": "AUTH_200_001",
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJSUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "customer@example.com",
      "fullName": "Nguyễn Văn An",
      "status": "ACTIVE",
      "roles": ["CUSTOMER"]
    }
  },
  "timestamp": "2026-09-28T08:35:00Z"
}
```

#### Error responses

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `400` | `COMMON_400_001` | Request vi phạm validation |
| `401` | `AUTH_401_001` | Email hoặc password không đúng |
| `403` | `AUTH_403_001` | Tài khoản không ở trạng thái `ACTIVE` |

Response đăng nhập sai luôn dùng thông báo chung `Invalid email or password`.

#### Business rules

1. Chuẩn hóa email trước khi truy vấn.
2. Kiểm tra password bằng `PasswordEncoder.matches`.
3. Không phân biệt lỗi email không tồn tại và password sai.
4. Chỉ user `ACTIVE` được cấp token.
5. Token lấy role từ quan hệ `user_roles`; không tin role do client gửi lên.

---

## 7. Current user APIs

### 7.1. Lấy thông tin tài khoản hiện tại

- **Method:** `GET`
- **URL:** `/api/v1/users/me`
- **Quyền:** `Authenticated`
- **Trạng thái:** `PLANNED`

#### Response - `200 OK`

```json
{
  "success": true,
  "code": "USER_200_001",
  "message": "User retrieved successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "customer@example.com",
    "fullName": "Nguyễn Văn An",
    "phone": "+84901234567",
    "status": "ACTIVE",
    "roles": ["CUSTOMER"],
    "createdAt": "2026-09-28T08:30:00Z",
    "updatedAt": "2026-09-28T08:30:00Z"
  },
  "timestamp": "2026-09-28T08:40:00Z"
}
```

#### Error responses

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `401` | `AUTH_401_002` | Access token thiếu, hết hạn hoặc không hợp lệ |
| `404` | `USER_404_001` | Token hợp lệ nhưng user không còn tồn tại |

---

### 7.2. Cập nhật hồ sơ cơ bản

- **Method:** `PATCH`
- **URL:** `/api/v1/users/me`
- **Quyền:** `Authenticated`
- **Trạng thái:** `PLANNED`
- **Mô tả:** Cập nhật `fullName` và/hoặc `phone`. Email không được đổi bởi endpoint này.

#### Request body

Ít nhất một field phải xuất hiện.

| Field | Type | Required | Validation |
|---|---|---:|---|
| `fullName` | string | Không | Sau khi trim còn 2-100 ký tự |
| `phone` | string | Không | 9-15 chữ số, có thể bắt đầu bằng `+` |

```json
{
  "fullName": "Nguyễn Văn An Updated",
  "phone": "+84907654321"
}
```

#### Response - `200 OK`

Sử dụng cùng schema user trong endpoint `GET /api/v1/users/me` với:

```json
{
  "success": true,
  "code": "USER_200_002",
  "message": "User updated successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "customer@example.com",
    "fullName": "Nguyễn Văn An Updated",
    "phone": "+84907654321",
    "status": "ACTIVE",
    "roles": ["CUSTOMER"],
    "createdAt": "2026-09-28T08:30:00Z",
    "updatedAt": "2026-09-28T09:00:00Z"
  },
  "timestamp": "2026-09-28T09:00:00Z"
}
```

#### Error responses

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `400` | `COMMON_400_001` | Không có field cập nhật hoặc field không hợp lệ |
| `401` | `AUTH_401_002` | Access token không hợp lệ |
| `409` | `USER_409_002` | Số điện thoại mới đã được sử dụng |

---

### 7.3. Đổi mật khẩu

- **Method:** `POST`
- **URL:** `/api/v1/users/me/password`
- **Quyền:** `Authenticated`
- **Trạng thái:** `PLANNED`

#### Request body

| Field | Type | Required | Validation |
|---|---|---:|---|
| `currentPassword` | string | Có | Không được để trống |
| `newPassword` | string | Có | Đáp ứng password policy |

```json
{
  "currentPassword": "StayHub@123",
  "newPassword": "NewStayHub@456"
}
```

#### Response - `200 OK`

```json
{
  "success": true,
  "code": "USER_200_003",
  "message": "Password changed successfully",
  "data": null,
  "timestamp": "2026-09-28T09:10:00Z"
}
```

#### Error responses

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `400` | `COMMON_400_001` | Password mới vi phạm validation |
| `400` | `USER_400_001` | Password hiện tại không đúng |
| `401` | `AUTH_401_002` | Access token không hợp lệ |

#### Business rules

1. Password mới không được giống password hiện tại.
2. Sau khi đổi phải hash password mới bằng `PasswordEncoder`.
3. Khi có cơ chế refresh token, đổi mật khẩu phải thu hồi toàn bộ refresh token của user.

---

## 8. Admin user APIs

Toàn bộ endpoint trong phần này yêu cầu role `ADMIN`.

Phản hồi chung:

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `401` | `AUTH_401_002` | Chưa đăng nhập hoặc access token không hợp lệ |
| `403` | `AUTH_403_002` | Đã đăng nhập nhưng không có role `ADMIN` |

### 8.1. Danh sách người dùng

- **Method:** `GET`
- **URL:** `/api/v1/admin/users`
- **Quyền:** `ADMIN`
- **Trạng thái:** `PLANNED`

#### Query parameters

| Parameter | Type | Default | Quy tắc |
|---|---|---:|---|
| `page` | integer | `0` | Lớn hơn hoặc bằng 0 |
| `size` | integer | `20` | Từ 1 đến 100 |
| `keyword` | string | Không có | Tìm theo email, full name hoặc phone; tối đa 100 ký tự |
| `status` | string | Không có | Một giá trị thuộc `UserStatus` |
| `role` | string | Không có | Một role key đang tồn tại |
| `sort` | string | `createdAt,desc` | Cho phép `createdAt`, `updatedAt`, `email`, `fullName` |

Ví dụ:

```text
GET /api/v1/admin/users?page=0&size=20&status=ACTIVE&role=CUSTOMER&sort=createdAt,desc
```

#### Response - `200 OK`

```json
{
  "success": true,
  "code": "USER_200_004",
  "message": "Users retrieved successfully",
  "data": {
    "items": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "email": "customer@example.com",
        "fullName": "Nguyễn Văn An",
        "phone": "+84901234567",
        "status": "ACTIVE",
        "roles": ["CUSTOMER"],
        "createdAt": "2026-09-28T08:30:00Z",
        "updatedAt": "2026-09-28T08:30:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  },
  "timestamp": "2026-09-28T09:20:00Z"
}
```

#### Error responses bổ sung

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `400` | `COMMON_400_001` | Pagination, filter hoặc sort không hợp lệ |

---

### 8.2. Chi tiết người dùng

- **Method:** `GET`
- **URL:** `/api/v1/admin/users/{userId}`
- **Quyền:** `ADMIN`
- **Trạng thái:** `PLANNED`

#### Path parameter

| Parameter | Type | Required |
|---|---|---:|
| `userId` | UUID | Có |

#### Response - `200 OK`

```json
{
  "success": true,
  "code": "USER_200_001",
  "message": "User retrieved successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "customer@example.com",
    "fullName": "Nguyễn Văn An",
    "phone": "+84901234567",
    "status": "ACTIVE",
    "roles": ["CUSTOMER"],
    "createdAt": "2026-09-28T08:30:00Z",
    "updatedAt": "2026-09-28T08:30:00Z"
  },
  "timestamp": "2026-09-28T09:25:00Z"
}
```

#### Error responses bổ sung

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `400` | `COMMON_400_002` | `userId` không đúng định dạng UUID |
| `404` | `USER_404_001` | Không tìm thấy user |

---

### 8.3. Cập nhật trạng thái người dùng

- **Method:** `PATCH`
- **URL:** `/api/v1/admin/users/{userId}/status`
- **Quyền:** `ADMIN`
- **Trạng thái:** `PLANNED`

#### Request body

| Field | Type | Required | Validation |
|---|---|---:|---|
| `status` | string | Có | `PENDING`, `ACTIVE`, `INACTIVE` hoặc `BANNED` |
| `reason` | string | Có | Sau khi trim còn 5-255 ký tự |

```json
{
  "status": "BANNED",
  "reason": "Repeated fraudulent booking attempts"
}
```

#### Response - `200 OK`

```json
{
  "success": true,
  "code": "USER_200_005",
  "message": "User status updated successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "status": "BANNED",
    "updatedAt": "2026-09-28T09:30:00Z"
  },
  "timestamp": "2026-09-28T09:30:00Z"
}
```

#### Allowed transitions

| Trạng thái hiện tại | Trạng thái tiếp theo |
|---|---|
| `PENDING` | `ACTIVE`, `BANNED` |
| `ACTIVE` | `INACTIVE`, `BANNED` |
| `INACTIVE` | `ACTIVE`, `BANNED` |
| `BANNED` | `ACTIVE` |

#### Error responses bổ sung

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `404` | `USER_404_001` | Không tìm thấy user |
| `409` | `USER_409_004` | Chuyển trạng thái không hợp lệ |
| `409` | `USER_409_005` | Admin cố vô hiệu hóa hoặc ban chính mình |

#### Business rules

1. Admin không được đổi chính mình sang `INACTIVE` hoặc `BANNED`.
2. Không cập nhật database nếu status mới giống status hiện tại.
3. `reason` phải được ghi log audit. Schema hiện chưa có bảng audit; phải bổ sung persistence hoặc chốt phương án logging trước khi triển khai endpoint.
4. Khi có refresh token, chuyển user khỏi `ACTIVE` phải thu hồi toàn bộ phiên.

---

### 8.4. Thay thế danh sách role của người dùng

- **Method:** `PUT`
- **URL:** `/api/v1/admin/users/{userId}/roles`
- **Quyền:** `ADMIN`
- **Trạng thái:** `PLANNED`
- **Mô tả:** Thay thế toàn bộ role hiện tại bằng tập `roleKeys` trong request.

#### Request body

| Field | Type | Required | Validation |
|---|---|---:|---|
| `roleKeys` | array of string | Có | Từ 1 phần tử, không trùng lặp, role phải tồn tại và đang active |

```json
{
  "roleKeys": ["CUSTOMER", "HOTEL_OWNER"]
}
```

#### Response - `200 OK`

```json
{
  "success": true,
  "code": "USER_200_006",
  "message": "User roles updated successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "roles": ["CUSTOMER", "HOTEL_OWNER"],
    "updatedAt": "2026-09-28T09:40:00Z"
  },
  "timestamp": "2026-09-28T09:40:00Z"
}
```

#### Error responses bổ sung

| HTTP | Code | Khi xảy ra |
|---:|---|---|
| `400` | `COMMON_400_001` | `roleKeys` rỗng hoặc có phần tử trùng |
| `404` | `USER_404_001` | Không tìm thấy user |
| `404` | `ROLE_404_001` | Có role key không tồn tại |
| `409` | `ROLE_409_001` | Có role đang inactive |
| `409` | `ROLE_409_002` | Thao tác sẽ loại bỏ `ADMIN` cuối cùng đang hoạt động |

#### Business rules

1. User luôn phải có ít nhất một role.
2. Không tạo `UserRole` trùng cặp `(user_id, role_id)`.
3. Phải thêm và xóa role qua collection `User.roles` để cascade/orphan removal hoạt động đúng.
4. Không được loại bỏ role `ADMIN` khỏi admin active cuối cùng của hệ thống.
5. Role trong access token cũ chưa tự thay đổi. Khi có token revocation/security stamp, cập nhật role phải vô hiệu hóa các phiên cũ.

---

### 8.5. Danh sách role có thể gán

- **Method:** `GET`
- **URL:** `/api/v1/admin/roles`
- **Quyền:** `ADMIN`
- **Trạng thái:** `PLANNED`

#### Query parameters

| Parameter | Type | Default | Ý nghĩa |
|---|---|---:|---|
| `activeOnly` | boolean | `true` | Chỉ trả role đang hoạt động |

#### Response - `200 OK`

```json
{
  "success": true,
  "code": "ROLE_200_001",
  "message": "Roles retrieved successfully",
  "data": [
    {
      "key": "CUSTOMER",
      "name": "Customer",
      "description": "Có thể tìm kiếm và đặt phòng khách sạn",
      "active": true
    },
    {
      "key": "HOTEL_OWNER",
      "name": "Hotel Owner",
      "description": "Có thể quản lý các khách sạn thuộc sở hữu",
      "active": true
    },
    {
      "key": "ADMIN",
      "name": "Administrator",
      "description": "Có thể quản trị hệ thống StayHub",
      "active": true
    }
  ],
  "timestamp": "2026-09-28T09:45:00Z"
}
```

## 9. ResponseCode cần có

Các code đã tồn tại được giữ nguyên; các code `PLANNED` phải được thêm khi triển khai endpoint tương ứng.

| Enum đề xuất | HTTP | Code | Message | Trạng thái hiện tại |
|---|---:|---|---|---|
| `USER_CREATED` | `201` | `USER_201_001` | `User created successfully` | Đã dùng bởi API đăng ký |
| `USER_RETRIEVED` | `200` | `USER_200_001` | `User retrieved successfully` | Đã có |
| `LOGIN_SUCCESS` | `200` | `AUTH_200_001` | `Login successful` | `PLANNED` |
| `USER_UPDATED` | `200` | `USER_200_002` | `User updated successfully` | `PLANNED` |
| `PASSWORD_CHANGED` | `200` | `USER_200_003` | `Password changed successfully` | `PLANNED` |
| `USERS_RETRIEVED` | `200` | `USER_200_004` | `Users retrieved successfully` | `PLANNED` |
| `USER_STATUS_UPDATED` | `200` | `USER_200_005` | `User status updated successfully` | `PLANNED` |
| `USER_ROLES_UPDATED` | `200` | `USER_200_006` | `User roles updated successfully` | `PLANNED` |
| `ROLES_RETRIEVED` | `200` | `ROLE_200_001` | `Roles retrieved successfully` | `PLANNED` |

## 10. ErrorCode cần có

| Enum đề xuất | HTTP | Code | Message | Trạng thái hiện tại |
|---|---:|---|---|---|
| `VALIDATION_ERROR` | `400` | `COMMON_400_001` | `Request data is invalid` | Đã có |
| `INVALID_REQUEST` | `400` | `COMMON_400_002` | `Invalid request` | Đã có |
| `USER_NOT_FOUND` | `404` | `USER_404_001` | `User not found` | Đã có |
| `EMAIL_ALREADY_EXISTS` | `409` | `USER_409_001` | `Email already exists` | Đã có |
| `PHONE_ALREADY_EXISTS` | `409` | `USER_409_002` | `Phone number already exists` | Đã có |
| `USER_ROLE_ALREADY_EXISTS` | `409` | `USER_409_003` | `User already has this role` | Đã có |
| `INVALID_CREDENTIALS` | `401` | `AUTH_401_001` | `Invalid email or password` | `PLANNED` |
| `UNAUTHENTICATED` | `401` | `AUTH_401_002` | `Authentication is required` | `PLANNED` |
| `ACCOUNT_NOT_ACTIVE` | `403` | `AUTH_403_001` | `Account is not active` | `PLANNED` |
| `ACCESS_DENIED` | `403` | `AUTH_403_002` | `You do not have permission to perform this action` | `PLANNED` |
| `CURRENT_PASSWORD_INVALID` | `400` | `USER_400_001` | `Current password is invalid` | `PLANNED` |
| `INVALID_USER_STATUS_TRANSITION` | `409` | `USER_409_004` | `User status transition is not allowed` | `PLANNED` |
| `SELF_DEACTIVATION_NOT_ALLOWED` | `409` | `USER_409_005` | `Administrator cannot deactivate own account` | `PLANNED` |
| `ROLE_NOT_FOUND` | `404` | `ROLE_404_001` | `Role not found` | Đã dùng bởi API đăng ký |
| `ROLE_NOT_ACTIVE` | `409` | `ROLE_409_001` | `Role is not active` | `PLANNED` |
| `LAST_ADMIN_ROLE_REQUIRED` | `409` | `ROLE_409_002` | `The last active administrator must keep the ADMIN role` | `PLANNED` |

Các lỗi `401` và `403` phát sinh trong Spring Security filter chain phải được chuẩn hóa bằng `AuthenticationEntryPoint` và `AccessDeniedHandler`; chúng không đi qua `GlobalExceptionHandler` thông thường.

## 11. DTO dự kiến

### Request DTO

- `UserRegisterRequest` - đã có, dùng cho API đăng ký.
- `LoginRequest`
- `UpdateMyProfileRequest`
- `ChangePasswordRequest`
- `UpdateUserStatusRequest`
- `ReplaceUserRolesRequest`

### Response DTO

- `UserResponse` - đã có, dùng cho API đăng ký.
- `LoginResponse`
- `CurrentUserResponse`
- `AdminUserSummaryResponse`
- `AdminUserDetailResponse`
- `UserStatusResponse`
- `UserRolesResponse`
- `RoleResponse`
- `PageResponse<T>`

Tên DTO có thể điều chỉnh theo convention package, nhưng field và JSON contract chỉ được thay đổi sau khi cập nhật tài liệu này.

## 12. Checklist triển khai

### Persistence foundation

- [x] Đồng bộ migration với `User`, `Role`, `Permission` và `UserRole`.
- [x] Seed ba role hệ thống.
- [x] Seed permission quản trị và gán cho `ADMIN`.
- [ ] Đổi ID generic của `UserRepository` sang `UUID`.
- [ ] Bổ sung query tìm user theo normalized email để phục vụ đăng nhập.
- [x] Bổ sung query kiểm tra email và phone trùng.
- [x] Bổ sung repository cho `Role` và truy vấn role đang hoạt động.

### Security foundation

- [ ] Chọn thuật toán và khóa ký JWT.
- [ ] Cấu hình JWT decoder/encoder.
- [ ] Cấu hình endpoint public và endpoint yêu cầu role.
- [ ] Tạo `AuthenticationEntryPoint` trả `ApiResponse` cho `401`.
- [ ] Tạo `AccessDeniedHandler` trả `ApiResponse` cho `403`.
- [ ] Không hard-code secret trong source code.
- [x] Tắt CSRF cho REST API trong giai đoạn phát triển.
- [ ] Thay cấu hình `anyRequest().permitAll()` bằng rule xác thực/phân quyền trước khi hoàn thiện JWT.

### Auth and current user

- [ ] Hoàn thiện validation và integration test để chuyển register từ `IMPLEMENTING` sang `TESTED`.
- [ ] Implement login.
- [ ] Implement get current user.
- [ ] Implement update current user.
- [ ] Implement change password.

### Admin

- [ ] Implement user list/filter/page.
- [ ] Implement user detail.
- [ ] Implement status transition.
- [ ] Chốt nơi lưu audit reason trước khi implement status update.
- [ ] Implement replace roles.
- [ ] Implement role list.

### Verification

- [ ] Integration test cho anonymous/authenticated/admin authorization.
- [ ] Integration test cho duplicate email và phone.
- [ ] Integration test cho inactive/banned login.
- [ ] Integration test cho status transition.
- [ ] Integration test ngăn xóa admin cuối cùng.
- [ ] Cập nhật trạng thái endpoint từ `PLANNED` sang trạng thái thực tế.

