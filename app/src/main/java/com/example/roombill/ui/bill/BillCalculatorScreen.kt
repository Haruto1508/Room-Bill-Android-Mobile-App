package com.example.roombill.ui.bill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roombill.ui.theme.RoomBillTheme
import com.example.roombill.ui.viewmodel.BillCalculatorUiState
import com.example.roombill.ui.viewmodel.BillCalculatorViewModel
import com.example.roombill.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillCalculatorScreen(
    viewModel: BillCalculatorViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearUserMessage()
        }
    }

    BillCalculatorScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onMonthChange = viewModel::updateMonth,
        onYearChange = viewModel::updateYear,
        onPrevElectricityChange = viewModel::updatePrevElectricityReading,
        onCurrElectricityChange = viewModel::updateCurrElectricityReading,
        onPrevWaterChange = viewModel::updatePrevWaterReading,
        onCurrWaterChange = viewModel::updateCurrWaterReading,
        onRoomRentChange = viewModel::updateRoomRentCost,
        onInternetFeeChange = viewModel::updateInternetCost,
        onGarbageFeeChange = viewModel::updateGarbageCost,
        onExtraFeeChange = viewModel::updateExtraCost,
        onExtraFeeDescriptionChange = viewModel::updateExtraCostDescription,
        onSaveClick = viewModel::saveBill,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillCalculatorScreenContent(
    uiState: BillCalculatorUiState,
    snackbarHostState: SnackbarHostState,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit,
    onPrevElectricityChange: (String) -> Unit,
    onCurrElectricityChange: (String) -> Unit,
    onPrevWaterChange: (String) -> Unit,
    onCurrWaterChange: (String) -> Unit,
    onRoomRentChange: (String) -> Unit,
    onInternetFeeChange: (String) -> Unit,
    onGarbageFeeChange: (String) -> Unit,
    onExtraFeeChange: (String) -> Unit,
    onExtraFeeDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tính tiền phòng hàng tháng") },
                navigationIcon = {
                    onNavigateBack?.let { navigateBack ->
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isAutoPopulated) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Đã tự động lấy chỉ số cũ từ hóa đơn gần nhất.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // Month and Year selector
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Thời gian tính tiền",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var expandedMonth by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expandedMonth,
                            onExpandedChange = { expandedMonth = !expandedMonth },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = "Tháng ${uiState.month}",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tháng") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonth) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMonth,
                                onDismissRequest = { expandedMonth = false }
                            ) {
                                (1..12).forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text("Tháng $m") },
                                        onClick = {
                                            onMonthChange(m)
                                            expandedMonth = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = uiState.year.toString(),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { onYearChange(it) }
                            },
                            label = { Text("Năm") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Electricity Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Chỉ số điện (Đơn giá: ${FormatUtils.formatVnd(uiState.electricityUnitPrice)}/kWh)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.prevElectricityReading,
                            onValueChange = onPrevElectricityChange,
                            label = { Text("Chỉ số cũ") },
                            suffix = { Text("kWh") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = uiState.currElectricityReading,
                            onValueChange = onCurrElectricityChange,
                            label = { Text("Chỉ số mới") },
                            suffix = { Text("kWh") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tiêu thụ: ${FormatUtils.formatNumber(uiState.electricityUsage)} kWh",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = FormatUtils.formatVnd(uiState.electricityCost),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Water Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Chỉ số nước (Đơn giá: ${FormatUtils.formatVnd(uiState.waterUnitPrice)}/m³)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.prevWaterReading,
                            onValueChange = onPrevWaterChange,
                            label = { Text("Chỉ số cũ") },
                            suffix = { Text("m³") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = uiState.currWaterReading,
                            onValueChange = onCurrWaterChange,
                            label = { Text("Chỉ số mới") },
                            suffix = { Text("m³") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tiêu thụ: ${FormatUtils.formatNumber(uiState.waterUsage)} m³",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = FormatUtils.formatVnd(uiState.waterCost),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Fixed and Extra Fees
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tiền phòng & Phí cố định",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.roomRentCost,
                        onValueChange = onRoomRentChange,
                        label = { Text("Tiền thuê phòng") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                        suffix = { Text("VNĐ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.internetCost,
                            onValueChange = onInternetFeeChange,
                            label = { Text("Internet") },
                            leadingIcon = { Icon(Icons.Default.Wifi, contentDescription = null) },
                            suffix = { Text("VNĐ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = uiState.garbageCost,
                            onValueChange = onGarbageFeeChange,
                            label = { Text("Rác") },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            suffix = { Text("VNĐ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.extraCost,
                            onValueChange = onExtraFeeChange,
                            label = { Text("Phí bổ sung") },
                            leadingIcon = { Icon(Icons.Default.MoreHoriz, contentDescription = null) },
                            suffix = { Text("VNĐ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = uiState.extraCostDescription,
                            onValueChange = onExtraFeeDescriptionChange,
                            label = { Text("Mô tả phí bổ sung") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Real-time Summary Card
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "BẢNG TỔNG HỢP HÓA ĐƠN THÁNG ${uiState.month}/${uiState.year}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                    SummaryRow(
                        label = "Tiền thuê phòng",
                        amount = uiState.roomRent
                    )

                    SummaryRow(
                        label = "Tiền điện (${FormatUtils.formatNumber(uiState.electricityUsage)} kWh)",
                        amount = uiState.electricityCost
                    )

                    SummaryRow(
                        label = "Tiền nước (${FormatUtils.formatNumber(uiState.waterUsage)} m³)",
                        amount = uiState.waterCost
                    )

                    SummaryRow(
                        label = "Tiền Internet",
                        amount = uiState.internet
                    )

                    SummaryRow(
                        label = "Tiền rác",
                        amount = uiState.garbage
                    )

                    if (uiState.extra > 0) {
                        SummaryRow(
                            label = if (uiState.extraCostDescription.isNotBlank()) "Phí bổ sung (${uiState.extraCostDescription})" else "Phí bổ sung",
                            amount = uiState.extra
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TỔNG CỘNG:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = FormatUtils.formatVnd(uiState.totalAmount),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSaveClick,
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = if (uiState.isSaved) Icons.Default.Check else Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isSaved) "Đã lưu hóa đơn" else "Lưu hóa đơn",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = FormatUtils.formatVnd(amount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BillCalculatorScreenPreview() {
    RoomBillTheme {
        BillCalculatorScreenContent(
            uiState = BillCalculatorUiState(
                month = 3,
                year = 2025,
                prevElectricityReading = "100",
                currElectricityReading = "180",
                electricityUnitPrice = 3500.0,
                prevWaterReading = "20",
                currWaterReading = "28",
                waterUnitPrice = 20000.0,
                roomRentCost = "3000000",
                internetCost = "100000",
                garbageCost = "30000",
                extraCost = "50000",
                extraCostDescription = "Sửa khóa",
                isAutoPopulated = true
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onMonthChange = {},
            onYearChange = {},
            onPrevElectricityChange = {},
            onCurrElectricityChange = {},
            onPrevWaterChange = {},
            onCurrWaterChange = {},
            onRoomRentChange = {},
            onInternetFeeChange = {},
            onGarbageFeeChange = {},
            onExtraFeeChange = {},
            onExtraFeeDescriptionChange = {},
            onSaveClick = {}
        )
    }
}
