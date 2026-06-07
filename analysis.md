# PHẦN 1: PHÂN TÍCH LOGIC

## 1. Nguyên nhân lỗi (Vì sao 30 giây đã bị đăng xuất?)
Nhìn vào dòng code cấu hình thời gian trong lớp `TokenService` cũ:
`private final long ACCESS_TOKEN_EXPIRATION_MS = TimeUnit.SECONDS.toMillis(30);`

* **Sai lệch cấu hình:** Đoạn code trên đang đổi **30 giây** thành **30,000 miligiây**. Do đó, mã Access Token cấp cho người dùng chỉ có tuổi thọ đúng 30 giây kể từ khi đăng nhập.
* **Yêu cầu nghiệp vụ:** Theo tài liệu thiết kế (Lesson 01), thời gian sống chuẩn của token phải là **15 phút**. Việc người viết code nhầm lẫn giữa đơn vị **Giây (Seconds)** và **Phút (Minutes)** chính là nguyên nhân gốc rễ của lỗi này.

## 2. Tác hại đối với trải nghiệm người dùng (UX)
* **Gián đoạn hành vi:** Các thao tác như lướt xem sản phẩm, đọc chi tiết, thêm vào giỏ hàng luôn mất nhiều hơn 30 giây. Khi token hết hạn ngầm, hệ thống sẽ chặn request và báo lỗi `401 Unauthorized`.
* **Gây ức chế cho khách hàng:** Vì ứng dụng chưa có cơ chế tự động làm mới mã (Refresh Token), hệ thống buộc phải đẩy người dùng quay lại màn hình Đăng nhập một cách thụ động. Việc phải nhập lại mật khẩu liên tục sau vài phút lướt app gây phiền toái lớn, khiến khách hàng dễ rời bỏ ứng dụng.

## 3. Giải pháp khắc phục
Thay đổi đơn vị đo lường thời gian từ Giây sang Phút để đưa thời hạn token về đúng 15 phút ($15 \times 60 \times 1000 = 900,000 \text{ ms}$):
`private final long ACCESS_TOKEN_EXPIRATION_MS = TimeUnit.MINUTES.toMillis(15);`