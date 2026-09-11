package com.example.roombill.ui.history

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.roombill.data.local.entity.BillEntity
import com.example.roombill.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailModalBottomSheet(
    bill: BillEntity,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Chi tiết hóa đơn",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tháng ${bill.month}/${bill.year}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng"
                    )
                }
            }

            HorizontalDivider()

            // Itemized breakdown list
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Tiền phòng
                    DetailRowItem(
                        icon = Icons.Default.Home,
                        title = "Tiền phòng",
                        valueText = FormatUtils.formatVnd(bill.roomRentCost)
                    )

                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                    // 2. Tiền điện
                    val elecUsage = maxOf(0.0, bill.currElectricityReading - bill.prevElectricityReading)
                    val elecUnitPrice = if (elecUsage > 0) bill.electricityCost / elecUsage else 0.0
                    DetailRowItem(
                        icon = Icons.Default.Bolt,
                        title = "Tiền điện",
                        valueText = FormatUtils.formatVnd(bill.electricityCost),
                        subtitle = "Chỉ số: ${FormatUtils.formatNumber(bill.prevElectricityReading)} -> ${FormatUtils.formatNumber(bill.currElectricityReading)} (${FormatUtils.formatNumber(elecUsage)} kWh)",
                        formulaText = if (elecUsage > 0) "${FormatUtils.formatNumber(elecUsage)} kWh x ${FormatUtils.formatVnd(elecUnitPrice)} = ${FormatUtils.formatVnd(bill.electricityCost)}" else null
                    )

                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                    // 3. Tiền nước
                    val waterUsage = maxOf(0.0, bill.currWaterReading - bill.prevWaterReading)
                    val waterUnitPrice = if (waterUsage > 0) bill.waterCost / waterUsage else 0.0
                    DetailRowItem(
                        icon = Icons.Default.WaterDrop,
                        title = "Tiền nước",
                        valueText = FormatUtils.formatVnd(bill.waterCost),
                        subtitle = "Chỉ số: ${FormatUtils.formatNumber(bill.prevWaterReading)} -> ${FormatUtils.formatNumber(bill.currWaterReading)} (${FormatUtils.formatNumber(waterUsage)} m³)",
                        formulaText = if (waterUsage > 0) "${FormatUtils.formatNumber(waterUsage)} m³ x ${FormatUtils.formatVnd(waterUnitPrice)} = ${FormatUtils.formatVnd(bill.waterCost)}" else null
                    )

                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                    // 4. Tiền mạng
                    DetailRowItem(
                        icon = Icons.Default.Wifi,
                        title = "Tiền mạng (Internet)",
                        valueText = FormatUtils.formatVnd(bill.internetCost)
                    )

                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                    // 5. Tiền rác
                    DetailRowItem(
                        icon = Icons.Default.Delete,
                        title = "Tiền rác",
                        valueText = FormatUtils.formatVnd(bill.garbageCost)
                    )

                    // 6. Phí phát sinh (nếu có)
                    if ((bill.extraCost > 0) || bill.extraCostDescription.isNotBlank()) {
                        HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                        DetailRowItem(
                            icon = Icons.Default.MoreHoriz,
                            title = "Phí phát sinh",
                            valueText = FormatUtils.formatVnd(bill.extraCost),
                            subtitle = if (bill.extraCostDescription.isNotBlank()) "Ghi chú: ${bill.extraCostDescription}" else null
                        )
                    }
                }
            }

            // Total Amount Highlight Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TỔNG CỘNG",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = FormatUtils.formatVnd(bill.totalAmount),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Action Buttons: Copy & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareText = FormatUtils.buildBillShareText(bill)
                        clipboardManager.setText(AnnotatedString(shareText))
                        Toast.makeText(context, "Đã sao chép hóa đơn vào bộ nhớ tạm", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sao chép")
                }

                Button(
                    onClick = {
                        val shareText = FormatUtils.buildBillShareText(bill)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ hóa đơn")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chia sẻ")
                }
            }
        }
    }
}

@Composable
fun DetailRowItem(
    icon: ImageVector,
    title: String,
    valueText: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    formulaText: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!formulaText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formulaText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
