package kino.malve.kodiAPI.pojos.param

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import entities.cinemamode.playlist.IParam
import kino.malve.kodiAPI.pojos.item.IItem

data class PlayListParam(

        @SerializedName("playlistid")
        @Expose
        var playlistid: Int = 1,

        @SerializedName("item")
        @Expose
        var item: IItem

) : IParam
