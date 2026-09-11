package com.example.roombill.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.ui.theme.RoomBillTheme
import com.example.roombill.ui.viewmodel.BillHistoryUiState
import com.example.roombill.ui.viewmodel.BillHistoryViewModel
import com.example.roombill.utils.FormatUtils
import java.util.Locale

@Composable
fun BillHistoryScreen(
    viewModel: BillHistoryViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearUserMessage()
        }
    }

    BillHistoryScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onMonthSelect = viewModel::updateSelectedMonth,
        onYearSelect = viewModel::updateSelectedYear,
        onClearFilters = viewModel::clearFilters,
        onBillClick = viewModel::selectBillForDetail,
        onDeleteRequest = viewModel::onDeleteBillRequested,
        onConfirmDelete = viewModel::confirmDeleteBill,
        onDismissDelete = viewModel::cancelDelete,
        onDismissDetail = { viewModel.selectBillForDetail(null) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BillHistoryScreenContent(
    uiState: BillHistoryUiState,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChange: (String) -> Unit,
    onMonthSelect: (Int?) -> Unit,
    onYearSelect: (Int?) -> Unit,
    onClearFilters: () -> Unit,
    onBillClick: (BillEntity) -> Unit,
    onDeleteRequest: (BillEntity) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onDismissDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var monthMenuExpanded by remember { mutableStateOf(value = false) }
    var yearMenuExpanded by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử hóa đơn") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Tìm theo tháng, năm, ghi chú...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Xóa tìm kiếm",
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Filter Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Month Dropdown Filter Chip
                Box {
                    val monthSelected = (uiState.selectedMonth != null) && (uiState.selectedMonth != 0)
                    val monthText = if (monthSelected) {
                        "Tháng ${uiState.selectedMonth}"
                    } else {
                        "Tất cả tháng"
                    }
                    FilterChip(
                        selected = monthSelected,
                        onClick = { monthMenuExpanded = true },
                        label = { Text(monthText) },
                    )
                    DropdownMenu(
                        expanded = monthMenuExpanded,
                        onDismissRequest = { monthMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tất cả tháng") },
                            onClick = {
                                onMonthSelect(null)
                                monthMenuExpanded = false
                            },
                        )
                        (1..12).forEach { month ->
                            DropdownMenuItem(
                                text = { Text("Tháng $month") },
                                onClick = {
                                    onMonthSelect(month)
                                    monthMenuExpanded = false
                                },
                            )
                        }
                    }
                }

                // Year Dropdown Filter Chip
                Box {
                    val yearSelected = (uiState.selectedYear != null) && (uiState.selectedYear != 0)
                    val yearText = if (yearSelected) {
                        "Năm ${uiState.selectedYear}"
                    } else {
                        "Tất cả năm"
                    }
                    FilterChip(
                        selected = yearSelected,
                        onClick = { yearMenuExpanded = true },
                        label = { Text(yearText) },
                    )
                    DropdownMenu(
                        expanded = yearMenuExpanded,
                        onDismissRequest = { yearMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tất cả năm") },
                            onClick = {
                                onYearSelect(null)
                                yearMenuExpanded = false
                            },
                        )
                        uiState.availableYears.forEach { year ->
                            DropdownMenuItem(
                                text = { Text("Năm $year") },
                                onClick = {
                                    onYearSelect(year)
                                    yearMenuExpanded = false
                                },
                            )
                        }
                    }
                }

                // Clear Filter Button
                val isFilterActive = uiState.searchQuery.isNotEmpty() ||
                        ((uiState.selectedMonth != null) && (uiState.selectedMonth != 0)) ||
                        ((uiState.selectedYear != null) && (uiState.selectedYear != 0))

                if (isFilterActive) {
                    FilterChip(
                        selected = false,
                        onClick = onClearFilters,
                        label = { Text("Xóa lọc") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterListOff,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                    )
                }
            }

            // Count and Sum Summary
            val totalSum = uiState.filteredBills.sumOf { it.totalAmount }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Hóa đơn (${uiState.filteredBills.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (uiState.filteredBills.isNotEmpty()) {
                    Text(
                        text = "Tổng: ${FormatUtils.formatVnd(totalSum)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // List or Empty State
            if (uiState.filteredBills.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline,
                        )
                        Text(
                            text = if (uiState.bills.isEmpty()) "Chưa có hóa đơn nào được lưu" else "Không tìm thấy hóa đơn phù hợp",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = uiState.filteredBills,
                        key = { it.id },
                    ) { bill ->
                        BillHistoryCard(
                            bill = bill,
                            onClick = { onBillClick(bill) },
                            onDeleteClick = { onDeleteRequest(bill) },
                        )
                    }
                }
            }
        }

        // Delete confirmation dialog
        uiState.billToDelete?.let { bill ->
            AlertDialog(
                onDismissRequest = onDismissDelete,
                title = { Text("Xác nhận xóa hóa đơn") },
                text = {
                    Text("Bạn có chắc chắn muốn xóa hóa đơn Tháng ${bill.month}/${bill.year} (${FormatUtils.formatVnd(bill.totalAmount)}) không? Hành động này không thể hoàn tác.")
                },
                confirmButton = {
                    TextButton(
                        onClick = onConfirmDelete,
                    ) {
                        Text(
                            text = "Xóa",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDelete) {
                        Text("Hủy")
                    }
                },
            )
        }

        // Bill Detail BottomSheet
        uiState.selectedBillForDetail?.let { bill ->
            BillDetailModalBottomSheet(
                bill = bill,
                onDismissRequest = onDismissDetail,
            )
        }
    }
}

@Composable
fun BillHistoryCard(
    bill: BillEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val elecUsage = maxOf(0.0, bill.currElectricityReading - bill.prevElectricityReading)
    val waterUsage = maxOf(0.0, bill.currWaterReading - bill.prevWaterReading)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Row 1: Month/Year badge & Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = "Tháng ${String.format(Locale.US, "%02d", bill.month)}/${bill.year}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = FormatUtils.formatVnd(bill.totalAmount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa hóa đơn",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            // Row 2: Summary electricity & water consumption
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Electricity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "${FormatUtils.formatNumber(elecUsage)} kWh (${FormatUtils.formatVnd(bill.electricityCost)})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Water
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "${FormatUtils.formatNumber(waterUsage)} m³ (${FormatUtils.formatVnd(bill.waterCost)})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Row 3: Notes if available
            if (bill.extraCostDescription.isNotBlank()) {
                Text(
                    text = "Ghi chú: ${bill.extraCostDescription}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BillHistoryScreenPreview() {
    val sampleBills = listOf(
        BillEntity(
            id = 1,
            month = 5,
            year = 2025,
            prevElectricityReading = 100.0,
            currElectricityReading = 120.0,
            electricityCost = 70000.0,
            prevWaterReading = 10.0,
            currWaterReading = 15.0,
            waterCost = 100000.0,
            roomRentCost = 3000000.0,
            internetCost = 100000.0,
            garbageCost = 30000.0,
            extraCost = 50000.0,
            extraCostDescription = "Sửa vòi nước",
            totalAmount = 3350000.0,
        ),
    )

    RoomBillTheme {
        BillHistoryScreenContent(
            uiState = BillHistoryUiState(
                bills = sampleBills,
                filteredBills = sampleBills,
                availableYears = listOf(2025),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onSearchQueryChange = {},
            onMonthSelect = {},
            onYearSelect = {},
            onClearFilters = {},
            onBillClick = {},
            onDeleteRequest = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onDismissDetail = {},
        )
    }
}
