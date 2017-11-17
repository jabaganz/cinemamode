package kino.malve.kodiAPI.pojos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Limits(

        @SerializedName("end")
        @Expose
        var end: Int,

        @SerializedName("start")
        @Expose
        var start: Int,

        @SerializedName("total")
        @Expose
        var total: Int

)
