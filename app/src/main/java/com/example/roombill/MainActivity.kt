package com.example.roombill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roombill.ui.bill.BillCalculatorScreen
import com.example.roombill.ui.config.ConfigScreen
import com.example.roombill.ui.history.BillHistoryScreen
import com.example.roombill.ui.theme.RoomBillTheme
import com.example.roombill.ui.viewmodel.BillCalculatorViewModel
import com.example.roombill.ui.viewmodel.BillHistoryViewModel
import com.example.roombill.ui.viewmodel.ConfigViewModel
import com.example.roombill.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as RoomBillApplication
        val factory = ViewModelFactory(app.repository)

        setContent {
            RoomBillTheme {
                MainAppScreen(factory = factory)
            }
        }
    }
}

@Composable
fun MainAppScreen(factory: ViewModelFactory) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val configViewModel: ConfigViewModel = viewModel(factory = factory)
    val billCalculatorViewModel: BillCalculatorViewModel = viewModel(factory = factory)
    val billHistoryViewModel: BillHistoryViewModel = viewModel(factory = factory)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                    label = { Text("Tính hóa đơn") },
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null) },
                    label = { Text("Lịch sử") },
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Cấu hình giá") },
                )
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            0 -> BillCalculatorScreen(
                viewModel = billCalculatorViewModel,
                modifier = Modifier.padding(innerPadding),
            )
            1 -> BillHistoryScreen(
                viewModel = billHistoryViewModel,
                modifier = Modifier.padding(innerPadding),
            )
            else -> ConfigScreen(
                viewModel = configViewModel,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
