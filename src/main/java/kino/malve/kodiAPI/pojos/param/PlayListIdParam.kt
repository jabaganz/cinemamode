package entities.cinemamode.playlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class PlayListIdParam(

        @SerializedName("playlistid")
        @Expose
        var playlistid: Int


) : IParam
