package kino.malve.kodiAPI.pojos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Status(

        @SerializedName("id")
        @Expose
        var id: String,

        @SerializedName("jsonrpc")
        @Expose
        var jsonrpc: String,

        @SerializedName("result")
        @Expose
        var result: String

)