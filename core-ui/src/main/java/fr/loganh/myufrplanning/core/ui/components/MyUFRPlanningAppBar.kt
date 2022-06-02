package fr.loganh.myufrplanning.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fr.loganh.myufrplanning.core.ui.R

@ExperimentalMaterial3Api
@Composable
fun MyUFRPlanningAppBar(title: String, content: @Composable () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold (
        topBar = {
            SmallTopAppBar (
                title = { Text(text = title) },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_desc_app_bar_menu)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.settings)) },
                            onClick = { /*TODO*/ }
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