package fr.loganh.myufrplanning.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalMaterial3Api
@Composable
fun CoursePlanningItem(
    course: String,
    hours: String,
    room: String,
    type: String,
    teacher: String
) {
    // create a rounded card containing two columns and two rows
    // First row: course, hours
    // Second row: room, type, teacher
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        // first row contains course only
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
        ) {
            Text(
                text = course,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }

        // separator line
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
        )

        // second row contains hours in bold on the left, room and type in regular on center and teacher in regular on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = hours,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$room - $type",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            )

            if (teacher.isNotEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = teacher,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
