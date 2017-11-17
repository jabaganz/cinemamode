package kino.malve.kodiAPI.pojos.param

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import entities.cinemamode.playlist.IParam

data class VolumeParam(

        @SerializedName("volume")
        @Expose
        var volume: Int

) : IParam