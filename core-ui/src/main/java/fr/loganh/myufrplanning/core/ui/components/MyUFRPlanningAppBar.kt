package fr.loganh.myufrplanning.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@ExperimentalMaterial3Api
@Composable
fun MyUFRPlanningAppBar(title: String, content: @Composable() () -> Unit) {
    Scaffold (
        topBar = {
            SmallTopAppBar (
                title = { Text(text = title) },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                }
            )
        }
    ) {
        contentPadding ->
         Box(modifier = Modifier.padding(contentPadding)) {
            content()
         }
    }
}