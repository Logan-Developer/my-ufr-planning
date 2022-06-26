package fr.loganh.myufrplanning.model

data class PlanningItem(
    val title: String,
    val hours: String?,
    val room: String?,
    val type: String?,
    val teacher: String?,
    val isDate: Boolean
)
