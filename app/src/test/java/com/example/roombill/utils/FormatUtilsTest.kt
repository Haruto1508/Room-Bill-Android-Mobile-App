package com.example.roombill.utils

import com.example.roombill.data.local.entity.BillEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatUtilsTest {

    @Test
    fun formatVnd_formatsCurrencyCorrectly() {
        val result = FormatUtils.formatVnd(3500000.0)
        // Expected format with dot grouping and ₫ symbol
        assertTrue(result.contains("3.500.000") || result.contains("3,500,000") || result.contains("3500000"))
        assertTrue(result.contains("₫"))
    }

    @Test
    fun formatNumber_formatsWholeAndDecimalNumbers() {
        assertEquals("150", FormatUtils.formatNumber(150.0))
        assertEquals("150.50", FormatUtils.formatNumber(150.5))
        assertEquals("0", FormatUtils.formatNumber(0.0))
    }

    @Test
    fun parseFormattedDouble_parsesVariousInputs() {
        assertEquals(3500000.0, FormatUtils.parseFormattedDouble("3500000"), 0.001)
        assertEquals(150.5, FormatUtils.parseFormattedDouble("150,5"), 0.001)
        assertEquals(150.5, FormatUtils.parseFormattedDouble(" 150.5 "), 0.001)
        assertEquals(0.0, FormatUtils.parseFormattedDouble("invalid"), 0.001)
        assertEquals(0.0, FormatUtils.parseFormattedDouble(""), 0.001)
    }

    @Test
    fun buildBillShareText_generatesFormattedSummaryText() {
        val bill = BillEntity(
            id = 1L,
            month = 3,
            year = 2025,
            prevElectricityReading = 100.0,
            currElectricityReading = 150.0,
            electricityCost = 175000.0,
            prevWaterReading = 50.0,
            currWaterReading = 60.0,
            waterCost = 200000.0,
            roomRentCost = 3000000.0,
            internetCost = 100000.0,
            garbageCost = 30000.0,
            extraCost = 50000.0,
            extraCostDescription = "Phí vệ sinh sân",
            totalAmount = 3555000.0,
        )

        val text = FormatUtils.buildBillShareText(bill)

        assertTrue(text.contains("HÓA ĐƠN TIỀN PHÒNG THÁNG 3/2025"))
        assertTrue(text.contains("1. Tiền phòng:"))
        assertTrue(text.contains("2. Tiền điện:"))
        assertTrue(text.contains("100 -> 150 (50 kWh)"))
        assertTrue(text.contains("3. Tiền nước:"))
        assertTrue(text.contains("50 -> 60 (10 m³)"))
        assertTrue(text.contains("4. Tiền mạng:"))
        assertTrue(text.contains("5. Tiền rác:"))
        assertTrue(text.contains("6. Phí phát sinh (Phí vệ sinh sân):"))
        assertTrue(text.contains("TỔNG CỘNG:"))
    }
}
