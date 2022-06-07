package fr.loganh.myufrplanning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import dagger.hilt.android.AndroidEntryPoint
import fr.loganh.myufrplanning.ui.components.MyUFRPlanningAppBar
import fr.loganh.myufrplanning.ui.overview.OverviewViewModel
import fr.loganh.myufrplanning.ui.theme.MyUFRPlanningTheme

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: OverviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyUFRPlanningTheme {
                MyUFRPlanningAppBar(title = "My UFR Planning") {
                    Text(text = viewModel.status.value?.toString() ?: "")
                }
            }
        }
    }
}