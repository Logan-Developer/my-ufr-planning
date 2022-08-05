package fr.loganH.myufrplanning.data.repository

import fr.loganH.myufrplanning.data.datasource.PlanningRemoteDataSource
import fr.loganH.myufrplanning.model.PlanningItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanningRepository @Inject constructor(
    private val planningRemoteDataSource: PlanningRemoteDataSource
) {

    @Throws(Exception::class)
    suspend fun fetchPlanning(groupIds: List<Int>, nbDays: Int): List<PlanningItem> {
        val planning = planningRemoteDataSource.fetchPlanning(groupIds, nbDays)

        return planning.removePrefix("\r\n").removeSuffix("\n\r\n").split("\n").map {
            if (it.startsWith("*date*")) {
                PlanningItem(
                    it.split(";")[1],
                    null,
                    null,
                    true
                )
            } else {
                val firstSplit = it.split(" : ")

                val hours = firstSplit[0]
                val title = firstSplit[1]

                PlanningItem(
                    title,
                    hours,
                    null,
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