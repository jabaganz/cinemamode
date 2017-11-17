package kino.malve.kodiAPI.pojos.Notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Notification(
        @SerializedName("jsonrpc")
        @Expose
        var jsonrpc: String = "2.0",

        @SerializedName("method")
        @Expose
        var method: String = "Playlist.Add"
)
