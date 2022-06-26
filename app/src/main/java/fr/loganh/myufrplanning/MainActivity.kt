package fr.loganh.myufrplanning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.loganh.myufrplanning.ui.components.MyUFRPlanningAppBar
import fr.loganh.myufrplanning.ui.overview.OverviewScreen
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
                val navController = rememberNavController()

                MyUFRPlanningAppBar(title = "My UFR Planning") {
                    NavHost(
                        navController = navController,
                        startDestination = "overview_screen"
                    ) {
                        composable("overview_screen") {
                            OverviewScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}