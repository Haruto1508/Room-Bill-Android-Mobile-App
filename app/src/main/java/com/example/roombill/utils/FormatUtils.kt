package com.example.roombill.utils

import com.example.roombill.data.local.entity.BillEntity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object FormatUtils {

    fun formatVnd(amount: Double): String {
        val locale = Locale.forLanguageTag("vi-VN")
        val symbols = DecimalFormatSymbols(locale).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0 ₫", symbols)
        return formatter.format(amount)
    }

    fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) {
            String.format(Locale.US, "%.0f", value)
        } else {
            String.format(Locale.US, "%.2f", value)
        }
    }

    fun parseFormattedDouble(input: String): Double {
        val cleaned = input.trim().replace(",", ".").replace(" ", "")
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    fun buildBillShareText(bill: BillEntity): String {
        val elecUsage = maxOf(0.0, bill.currElectricityReading - bill.prevElectricityReading)
        val elecUnitPrice = if (elecUsage > 0) bill.electricityCost / elecUsage else 0.0

        val waterUsage = maxOf(0.0, bill.currWaterReading - bill.prevWaterReading)
        val waterUnitPrice = if (waterUsage > 0) bill.waterCost / waterUsage else 0.0

        val sb = StringBuilder()
        sb.appendLine("🧾 HÓA ĐƠN TIỀN PHÒNG THÁNG ${bill.month}/${bill.year}")
        sb.appendLine("----------------------------------------")
        sb.appendLine("1. Tiền phòng: ${formatVnd(bill.roomRentCost)}")

        sb.appendLine("2. Tiền điện:")
        sb.appendLine("   - Chỉ số: ${formatNumber(bill.prevElectricityReading)} -> ${formatNumber(bill.currElectricityReading)} (${formatNumber(elecUsage)} kWh)")
        if (elecUsage > 0) {
            sb.appendLine("   - Tính tiền: ${formatNumber(elecUsage)} kWh x ${formatVnd(elecUnitPrice)} = ${formatVnd(bill.electricityCost)}")
        } else {
            sb.appendLine("   - Thành tiền: ${formatVnd(bill.electricityCost)}")
        }

        sb.appendLine("3. Tiền nước:")
        sb.appendLine("   - Chỉ số: ${formatNumber(bill.prevWaterReading)} -> ${formatNumber(bill.currWaterReading)} (${formatNumber(waterUsage)} m³)")
        if (waterUsage > 0) {
            sb.appendLine("   - Tính tiền: ${formatNumber(waterUsage)} m³ x ${formatVnd(waterUnitPrice)} = ${formatVnd(bill.waterCost)}")
        } else {
            sb.appendLine("   - Thành tiền: ${formatVnd(bill.waterCost)}")
        }

        sb.appendLine("4. Tiền mạng: ${formatVnd(bill.internetCost)}")
        sb.appendLine("5. Tiền rác: ${formatVnd(bill.garbageCost)}")

        if (bill.extraCost > 0 || bill.extraCostDescription.isNotBlank()) {
            val extraDesc = if (bill.extraCostDescription.isNotBlank()) " (${bill.extraCostDescription})" else ""
            sb.appendLine("6. Phí phát sinh$extraDesc: ${formatVnd(bill.extraCost)}")
        }

        sb.appendLine("----------------------------------------")
        sb.appendLine("👉 TỔNG CỘNG: ${formatVnd(bill.totalAmount)}")
        return sb.toString().trimEnd()
    }
}
