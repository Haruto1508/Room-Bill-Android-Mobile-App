# RoomBill

> Ứng dụng Android hỗ trợ tính và quản lý hóa đơn phòng trọ.

## Tính năng

- Cấu hình giá điện, nước và các khoản phí liên quan.
- Tính hóa đơn theo thông tin sử dụng của từng kỳ.
- Lưu và xem lại lịch sử hóa đơn.
- Lưu dữ liệu cục bộ trên thiết bị bằng Room Database.
- Giao diện Jetpack Compose, hỗ trợ Android từ API 26 trở lên.

## Yêu cầu môi trường

- Android Studio phiên bản có hỗ trợ Android Gradle Plugin 9.4.0.
- JDK 11.
- Android SDK Platform 37.
- Android SDK Build-Tools tương ứng.
- Thiết bị Android hoặc máy ảo có API 26 trở lên.

> Có thể mở dự án bằng Android Studio để IDE tự đề xuất cài các SDK và plugin còn thiếu.

## Tải mã nguồn

```bash
git clone https://github.com/Haruto1508/Room-Bill-Android-Mobile-App.git
cd Room-Bill-Android-Mobile-App
```

## Mở và chạy bằng Android Studio

1. Mở thư mục dự án bằng Android Studio.
2. Chờ Gradle Sync hoàn tất và cài các thành phần SDK được yêu cầu.
3. Chọn module `app` và một thiết bị Android hoặc emulator.
4. Nhấn **Run** để biên dịch và cài ứng dụng.

## Biên dịch bằng dòng lệnh

### Windows PowerShell

```powershell
.\gradlew.bat assembleDebug
```

### macOS / Linux

```bash
./gradlew assembleDebug
```

APK debug được tạo tại:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Để cài APK lên thiết bị đã bật USB debugging:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Cách sử dụng

1. Mở ứng dụng **RoomBill**.
2. Vào tab **Cấu hình giá** để nhập và lưu các mức giá áp dụng cho phòng.
3. Vào tab **Tính hóa đơn**, nhập các chỉ số hoặc khoản sử dụng của kỳ hiện tại.
4. Kiểm tra tổng tiền và lưu hóa đơn.
5. Vào tab **Lịch sử** để xem lại chi tiết các hóa đơn đã lưu.

Dữ liệu hóa đơn và cấu hình được lưu cục bộ trên thiết bị. Gỡ ứng dụng hoặc xóa dữ liệu ứng dụng có thể làm mất dữ liệu đã lưu.

## Kiểm thử

```powershell
.\gradlew.bat test
```

Các bài kiểm thử instrumented có thể chạy từ Android Studio trên thiết bị hoặc emulator:

```powershell
.\gradlew.bat connectedAndroidTest
```

## Cấu trúc chính

```text
app/src/main/java/com/example/roombill/
├── data/       # Room database, DAO, entity và repository
├── ui/bill/     # Màn hình tính hóa đơn
├── ui/config/   # Màn hình cấu hình giá
├── ui/history/  # Màn hình lịch sử hóa đơn
└── ui/viewmodel/
```

## Công nghệ sử dụng

- Kotlin
- Jetpack Compose và Material 3
- AndroidX Room
- Kotlin Coroutines
- Gradle Kotlin DSL