package fr.loganh.myufrplanning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import fr.loganh.myufrplanning.core.ui.components.MyUFRPlanningAppBar
import fr.loganh.myufrplanning.core.ui.theme.MyUFRPlanningTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyUFRPlanningTheme {
                MyUFRPlanningAppBar(title = "My UFR Planning") {

                }
            }
        }
    }
}