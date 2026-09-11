# Project Plan

App tính tiền trọ (Room Bill Calculator) cho Android.
Chức năng:
1. Nhập số điện, nước tháng trước và tháng hiện tại, tự động tính thành tiền điện/nước và tổng tiền trọ trong tháng.
2. Tự động lấy số điện, nước cũ từ tháng gần nhất nếu đã có dữ liệu, giúp người dùng chỉ cần nhập số điện nước mới của tháng hiện tại.
3. Cấu hình giá mặc định: Tiền phòng, đơn giá điện (đến kWh), đơn giá nước, phí cố định/tùy chọn (tiền mạng, rác, phí phát sinh tùy chỉnh).
4. Lưu trữ dữ liệu cục bộ trên máy (Room Database / Jetpack Compose / Material 3 UI).
5. Quản lý danh sách hóa đơn các tháng, xem chi tiết hóa đơn, tìm kiếm/Lọc theo tháng/năm.

## Project Brief

# Project Brief: Room Bill Calculator (App Tính Tiền Trọ)

## Features

1. **Default Pricing & Fee Configuration**: Set and manage default rates for room rent, electricity (per kWh), water, and fixed or custom add-on fees (e.g., internet, trash collection, extra charges).
2. **Monthly Bill Calculation & Auto Meter Reading**: Input current electricity and water readings with automatic retrieval of previous meter readings from the latest bill to automatically compute itemized expenses and the total monthly bill.
3. **Bill History, Filtering & Detail View**: Store and display past bills in a clean list, support searching and filtering by month or year, and view itemized cost breakdowns for any bill.

## High-Level Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3 UI
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library
- **Local Persistence**: Room Database
- **Asynchronous Execution & Architecture**: Kotlin Coroutines, Flow, and Jetpack ViewModel

## Implementation Steps
**Total Duration:** 46m 11s

### Task_1_DatabaseAndRepository: Set up Room database entities (DefaultRates/Config, Bill), Data Access Objects (DAOs), and Repository layer for managing default pricing, meter readings, and bill history.
- **Status:** COMPLETED
- **Updates:** Set up Room database entities (ConfigEntity, BillEntity), DAOs (ConfigDao, BillDao with getLatestBill query), AppDatabase, and RoomBillRepository. Added unit tests and verified build with ./gradlew assembleDebug.
- **Acceptance Criteria:**
  - Room database schema defined for default rates and bill history
  - DAOs and Repository implementation for saving/retrieving configuration and bills
  - build pass
- **Duration:** 13m 54s

### Task_2_DefaultRatesAndBillCreationUI: Create ViewModel and Jetpack Compose screens for Default Pricing & Fee Configuration and Monthly Bill Calculation with auto meter reading calculation based on previous bill.
- **Status:** COMPLETED
- **Updates:** Created ConfigViewModel, BillCalculatorViewModel, ViewModelFactory, FormatUtils, ConfigScreen, BillCalculatorScreen, and updated MainActivity with bottom navigation. Added unit tests for both ViewModels and verified build passes.
- **Acceptance Criteria:**
  - Screen for configuring default rates for room rent, electricity, water, and add-on fees
  - Screen for creating new monthly bill with auto-populated previous readings and automatic total calculation
  - ViewModel manages state properly with Coroutines and Flow
  - build pass
- **Duration:** 13m 31s

### Task_3_BillHistoryAndDetailViewUI: Implement Bill History screen with search/filtering options (by month/year), detailed itemized bill view, and Jetpack Compose navigation.
- **Status:** COMPLETED
- **Updates:** Created BillHistoryViewModel, BillHistoryScreen, BillDetailModalBottomSheet, and updated MainActivity navigation. Added unit tests for BillHistoryViewModel. Verified build passes.
- **Acceptance Criteria:**
  - Bill history list screen displaying saved bills with month/year filter options
  - Detail screen showing itemized cost breakdown for a selected bill
  - Navigation configured between Default Config, Bill Creation, History, and Detail screens
  - build pass
- **Duration:** 7m 19s

### Task_4_RunAndVerify: Run and verify the complete Room Bill Calculator application. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** All 21 unit tests passed cleanly and assembleDebug build succeeded. Verified all core business logic (Config updating, Bill Calculation with auto pre-fill of previous readings, History filtering, Detail modal view).
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - make sure all existing tests pass
  - application stability verified and aligns with all project brief requirements
- **Duration:** 11m 27s

