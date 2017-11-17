package entities.cinemamode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import entities.cinemamode.playlist.IParam

data class Request(

        @SerializedName("jsonrpc")
        @Expose
        var jsonrpc: String = "2.0",

        @SerializedName("id")
        @Expose
        var id: Int = 1,

        @SerializedName("method")
        @Expose
        var method: String = "Playlist.Add",

        @SerializedName("params")
        @Expose
        var params: IParam
)
