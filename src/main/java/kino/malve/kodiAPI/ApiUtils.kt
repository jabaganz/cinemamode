package kino.malve.kodiAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtils {

    private val BASE_URL = "http://localhost:80/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val soService: MovieCenter
        get() = retrofit.create(MovieCenter::class.java)


}
