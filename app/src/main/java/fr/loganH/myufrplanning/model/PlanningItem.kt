package fr.loganH.myufrplanning.model

data class PlanningItem(
    val title: String,
    val hours: String?,
    val teacher: String?,
    val isDate: Boolean
)