package fr.loganh.myufrplanning.api

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SednaService {
    @GET("wmplanif.jsp?mode=2")
    suspend fun fetchPlanning(@Query("id") groupIds: List<Int>, @Query("jours") nbDays: Int): String

    companion object {
        private const val BASE_URL = "https://sedna.univ-fcomte.fr/jsp/custom/ufc/"

        fun create(): SednaService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(SednaService::class.java)
        }
    }
}