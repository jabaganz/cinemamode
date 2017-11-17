package entities.cinemamode.playlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kino.malve.kodiAPI.pojos.item.IItem

data class ItemParam(

        @SerializedName("item")
        @Expose
        var item: IItem

) : IParam
