package fr.loganh.myufrplanning.ui.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.loganh.myufrplanning.model.PlanningItem
import fr.loganh.myufrplanning.ui.components.CoursePlanningItem
import fr.loganh.myufrplanning.ui.components.DatePlanningItem

@ExperimentalMaterial3Api
@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Planning(viewModel)
    }
}

@ExperimentalMaterial3Api
@Composable
fun Planning(
    viewModel: OverviewViewModel
) {
    val planningItems = viewModel.planning.observeAsState().value

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (viewModel.status.value == SednaApiStatus.DONE) {
            for (planningItem: PlanningItem in planningItems!!) {
                if (planningItem.isDate) {
                    item {
                        DatePlanningItem(date = planningItem.title)
                    }
                }
                else {
                    item {
                        CoursePlanningItem(
                            course = planningItem.title,
                            hours = planningItem.hours ?: "",
                            room = planningItem.room ?: "",
                            type = planningItem.type ?: "",
                            teacher = planningItem.teacher ?: ""
                        )
                    }
                }
            }
        }
    }
}