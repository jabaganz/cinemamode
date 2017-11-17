package kino.malve.kodiAPI.pojos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Result(

        @SerializedName("limits")
        @Expose
        var limits: Limits,

        @SerializedName("movies")
        @Expose
        var movies: List<Movie>
)
