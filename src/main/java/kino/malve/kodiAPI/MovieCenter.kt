package kino.malve.kodiAPI

import kino.malve.kodiAPI.pojos.response.Response
import kino.malve.kodiAPI.pojos.response.Status
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieCenter {

    @GET("""jsonrpc?request={"jsonrpc": "2.0", "method": "VideoLibrary.GetMovies", "id": "libMovies"}""")
    fun getMovies(): Call<Response>

    @GET("jsonrpc")
    fun jsonRPC(@Query("request") path: String): Call<Status>

    @GET("""jsonrpc?request={"jsonrpc":"2.0","id":1,"method":"Playlist.Clear","params":{"playlistid":1}}""")
    fun clear(): Call<Status>

}