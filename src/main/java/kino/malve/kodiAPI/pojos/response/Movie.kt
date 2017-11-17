package kino.malve.kodiAPI.pojos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Movie(

        @SerializedName("label")
        @Expose
        var label: String,

        @SerializedName("movieid")
        @Expose
        var movieid: Int

)
