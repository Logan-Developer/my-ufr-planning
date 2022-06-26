package fr.loganh.myufrplanning.data.datasource

import fr.loganh.myufrplanning.api.SednaService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanningRemoteDataSource @Inject constructor (
    private val sednaService: SednaService,
) {

    /**
     * Fetches the latest planning from the remote API, and returns the result.
     * This execution is performed in the IO dispatcher.
     * @param groupIds:   List<Int> the list of group ids to fetch the planning for
     * @param nbDays: Int           the number of days to fetch
     */
    suspend fun fetchPlanning(groupIds: List<Int>, nbDays: Int): String {
        return sednaService.fetchPlanning(groupIds, nbDays)
    }
}