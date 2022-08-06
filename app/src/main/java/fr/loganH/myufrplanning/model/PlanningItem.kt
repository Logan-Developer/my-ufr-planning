package fr.loganH.myufrplanning.model

data class PlanningItem(
    val title: String,
    val hour: String?,
    val room: String?,
    val teacher: String?,
    val isDate: Boolean
)