package fr.loganH.myufrplanning.data.repository

import fr.loganH.myufrplanning.data.datasource.PlanningRemoteDataSource
import fr.loganH.myufrplanning.model.PlanningItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanningRepository @Inject constructor(
    private val planningRemoteDataSource: PlanningRemoteDataSource
) {

    suspend fun fetchPlanning(groupIds: List<Int>, nbDays: Int): List<PlanningItem> {
        val planning = planningRemoteDataSource.fetchPlanning(groupIds, nbDays)

        return planning.removePrefix("\r\n").removeSuffix("\n\r\n").split("\n").map {
            if (it.startsWith("*date*")) {
                PlanningItem(
                    it.split(";")[1],
                    null,
                    null,
                    null,
                    true
                )
            } else {
                val firstSplit = it.split(" : ")
                val secondSplit = firstSplit[1].split(";")

                val hours = firstSplit[0]
                val title = secondSplit[0]
                val room = if (secondSplit.size >= 2) secondSplit[1] else null
                val teacher = if (secondSplit.size == 3) secondSplit[2] else null

                PlanningItem(
                    title,
                    hours,
                    room,
                    teacher,
                    false
                )
            }
        }
    }

    companion object {
        @Volatile private var instance: PlanningRepository? = null

        fun getInstance(planningRemoteDataSource: PlanningRemoteDataSource): PlanningRepository = instance ?: synchronized(this) {
            instance ?: PlanningRepository(planningRemoteDataSource).also { instance = it }
        }
    }
}