package com.example.roombill.ui.config

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roombill.ui.theme.RoomBillTheme
import com.example.roombill.ui.viewmodel.ConfigUiState
import com.example.roombill.ui.viewmodel.ConfigViewModel
import com.example.roombill.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    viewModel: ConfigViewModel,
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

    ConfigScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRoomRentChange = viewModel::updateRoomRent,
        onElectricityPriceChange = viewModel::updateElectricityUnitPrice,
        onWaterPriceChange = viewModel::updateWaterUnitPrice,
        onInternetFeeChange = viewModel::updateInternetFee,
        onGarbageFeeChange = viewModel::updateGarbageFee,
        onExtraFeeChange = viewModel::updateExtraFee,
        onSaveClick = viewModel::saveConfig,
        modifier = modifier,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreenContent(
    uiState: ConfigUiState,
    snackbarHostState: SnackbarHostState,
    onRoomRentChange: (String) -> Unit,
    onElectricityPriceChange: (String) -> Unit,
    onWaterPriceChange: (String) -> Unit,
    onInternetFeeChange: (String) -> Unit,
    onGarbageFeeChange: (String) -> Unit,
    onExtraFeeChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cấu hình bảng giá mặc định") },
                navigationIcon = {
                    onNavigateBack?.let { navigateBack ->
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                        }
                    }
                },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Thiết lập đơn giá và các khoản phí mặc định. Giá trị này sẽ được tự động điền khi tạo hóa đơn mới.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            ConfigInputField(
                label = "Tiền thuê phòng hàng tháng",
                value = uiState.roomRent,
                onValueChange = onRoomRentChange,
                icon = Icons.Default.Home,
                suffix = "VNĐ"
            )

            ConfigInputField(
                label = "Đơn giá điện (trên 1 kWh)",
                value = uiState.electricityUnitPrice,
                onValueChange = onElectricityPriceChange,
                icon = Icons.Default.Bolt,
                suffix = "VNĐ/kWh"
            )

            ConfigInputField(
                label = "Đơn giá nước (trên 1 m³)",
                value = uiState.waterUnitPrice,
                onValueChange = onWaterPriceChange,
                icon = Icons.Default.WaterDrop,
                suffix = "VNĐ/m³"
            )

            ConfigInputField(
                label = "Tiền Internet mặc định",
                value = uiState.internetFee,
                onValueChange = onInternetFeeChange,
                icon = Icons.Default.Wifi,
                suffix = "VNĐ"
            )

            ConfigInputField(
                label = "Tiền rác mặc định",
                value = uiState.garbageFee,
                onValueChange = onGarbageFeeChange,
                icon = Icons.Default.Delete,
                suffix = "VNĐ"
            )

            ConfigInputField(
                label = "Phí bổ sung mặc định",
                value = uiState.extraFee,
                onValueChange = onExtraFeeChange,
                icon = Icons.Default.MoreHoriz,
                suffix = "VNĐ"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lưu cấu hình",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun ConfigInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    suffix: String,
    modifier: Modifier = Modifier
) {
    val numericValue = FormatUtils.parseFormattedDouble(value)
    val formattedPreview = FormatUtils.formatVnd(numericValue)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        suffix = { Text(suffix) },
        supportingText = {
            if (value.isNotBlank()) {
                Text(
                    text = "Định dạng: $formattedPreview",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun ConfigScreenPreview() {
    RoomBillTheme {
        ConfigScreenContent(
            uiState = ConfigUiState(
                roomRent = "3000000",
                electricityUnitPrice = "3500",
                waterUnitPrice = "20000",
                internetFee = "100000",
                garbageFee = "30000",
                extraFee = "0"
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRoomRentChange = {},
            onElectricityPriceChange = {},
            onWaterPriceChange = {},
            onInternetFeeChange = {},
            onGarbageFeeChange = {},
            onExtraFeeChange = {},
            onSaveClick = {}
        )
    }
}
