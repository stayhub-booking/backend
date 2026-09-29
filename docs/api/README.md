# Tài liệu API Contracts - StayHub

Thư mục này là **hợp đồng API** dùng để triển khai Backend và tích hợp Frontend cho StayHub.

Khi thay đổi URL, request, response, HTTP status hoặc business rule, phải cập nhật tài liệu tương ứng trước hoặc cùng lúc với code. Tài liệu luôn phân biệt rõ API mới ở mức thiết kế với API đã được triển khai và kiểm thử.

## 1. Danh mục module

| Module | Bảng dữ liệu chính | Trách nhiệm | Tài liệu | Trạng thái tài liệu |
|---|---|---|---|---|
| Identity & User Administration | `users`, `roles`, `permissions`, `user_roles`, `role_permissions` | Đăng ký, đăng nhập, hồ sơ cá nhân, trạng thái tài khoản và phân quyền | [identity-api.md](./identity-api.md) | Register đang triển khai; các API khác đã có contract |
| Catalog | `cities`, `hotels`, `room_types`, `amenities`, `hotel_amenities`, `room_type_amenities` | Danh mục và thông tin khách sạn | Chưa tạo | Planned |
| Pricing & Inventory | `room_rates`, `room_inventory`, `inventory_reservations` | Giá theo ngày, phòng trống và giữ tồn kho | Chưa tạo | Planned |
| Booking | `bookings`, `booking_items`, `booking_night_prices` | Tạo và quản lý đặt phòng | Chưa tạo | Planned |
| Payments | `payment_attempts`, `payment_webhook_events` | Thanh toán, đối soát và webhook | Chưa tạo | Planned |

## 2. Trạng thái endpoint

Mỗi endpoint phải có một trạng thái:

| Trạng thái | Ý nghĩa |
|---|---|
| `PLANNED` | Mới có contract, chưa có endpoint chạy được |
| `IMPLEMENTING` | Đang triển khai, contract chưa được xác nhận bằng test |
| `IMPLEMENTED` | Đã có code và compile thành công |
| `TESTED` | Đã có integration test xác nhận contract |

Không được xem endpoint `PLANNED` là chức năng đã tồn tại trong ứng dụng.

## 3. Quy chuẩn chung

### Base URL

```text
/api/v1
```

Ví dụ:

```text
POST /api/v1/auth/register
GET  /api/v1/users/me
GET  /api/v1/admin/users
```

### Authentication

Endpoint yêu cầu đăng nhập sử dụng Bearer token:

```http
Authorization: Bearer <access-token>
```

Phân loại quyền truy cập:

| Giá trị | Ý nghĩa |
|---|---|
| `Anonymous` | Không yêu cầu đăng nhập |
| `Authenticated` | Yêu cầu access token hợp lệ |
| `CUSTOMER` | Yêu cầu role khách hàng |
| `HOTEL_OWNER` | Yêu cầu role chủ khách sạn |
| `ADMIN` | Yêu cầu role quản trị viên |

### Success response

StayHub sử dụng `ApiResponse<T>`:

```json
{
  "success": true,
  "code": "USER_200_001",
  "message": "User retrieved successfully",
  "data": {},
  "timestamp": "2026-09-28T08:30:00Z"
}
```

### Error response

Lỗi nghiệp vụ dùng `ErrorCode` và được xử lý tập trung bởi `GlobalExceptionHandler`:

```json
{
  "success": false,
  "code": "USER_404_001",
  "message": "User not found",
  "data": null,
  "timestamp": "2026-09-28T08:30:00Z"
}
```

Lỗi validation trả chi tiết theo tên field trong `data`:

```json
{
  "success": false,
  "code": "COMMON_400_001",
  "message": "Request data is invalid",
  "data": {
    "email": "EMAIL_INVALID",
    "password": "PASSWORD_TOO_SHORT"
  },
  "timestamp": "2026-09-28T08:30:00Z"
}
```

### Quy ước dữ liệu

| Dữ liệu | Quy ước |
|---|---|
| ID | UUID dạng chuỗi |
| Timestamp | ISO 8601 UTC, ví dụ `2026-09-28T08:30:00Z` |
| Date | ISO 8601, ví dụ `2026-12-24` |
| Tiền | Số nguyên theo đơn vị tiền tệ nhỏ nhất; VND giữ nguyên giá trị đồng |
| Currency | ISO 4217, ví dụ `VND`, `USD` |
| Enum | Chuỗi `UPPER_SNAKE_CASE` |
| JSON field | `camelCase` |

### Phân trang

Request dùng chỉ số trang bắt đầu từ `0` để phù hợp với Spring Data:

```text
?page=0&size=20&sort=createdAt,desc
```

Response phân trang thống nhất:

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

Giới hạn chung:

- `page >= 0`.
- `1 <= size <= 100`.
- Field dùng để sort phải nằm trong danh sách cho phép của endpoint.

## 4. Quy tắc triển khai

- Controller chỉ nhận request, gọi service và trả response; không chứa nghiệp vụ chính.
- Controller không `try-catch` các lỗi nghiệp vụ thông thường.
- Service ném `AppException` với `ErrorCode` tương ứng.
- Request DTO thực hiện validation hình thức; service kiểm tra business rule.
- Entity không được trả trực tiếp ra API.
- Password không được log hoặc trả về response.
- Endpoint quản trị phải kiểm tra role ở tầng Spring Security và kiểm tra thêm các quy tắc nghiệp vụ trong service.
- Mỗi endpoint chỉ được chuyển sang `TESTED` khi integration test xác nhận method, URL, authorization, request, response và lỗi chính.

## 5. Thứ tự triển khai hiện tại

1. Hoàn thiện validation và integration test cho API đăng ký.
2. Sửa kiểu ID của `UserRepository` từ `Long` thành `UUID`.
3. Hoàn thiện Security/JWT và phản hồi `401`/`403` theo `ApiResponse`.
4. Triển khai API đăng nhập.
5. Triển khai nhóm hồ sơ người dùng hiện tại.
6. Triển khai nhóm quản trị người dùng và role.
7. Viết integration test và cập nhật trạng thái endpoint.

