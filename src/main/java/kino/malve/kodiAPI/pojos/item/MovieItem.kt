package kino.malve.kodiAPI.pojos.item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieItem(

        @SerializedName("movieid")
        @Expose
        var id: Int

) : IItem
