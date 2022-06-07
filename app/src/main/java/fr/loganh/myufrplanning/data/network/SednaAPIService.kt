package fr.loganh.myufrplanning.data.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://sedna.univ-fcomte.fr/jsp/custom/ufc/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface SednaAPIService {
    @GET("wmplanif.jsp?mode=2")
    suspend fun getPlanning(@Query("id") groupIds: List<Int>, @Query("jours") nbDays: Int): String
}

object SednaAPI {
    val retrofitService: SednaAPIService by lazy {
        retrofit.create(SednaAPIService::class.java)
    }
}