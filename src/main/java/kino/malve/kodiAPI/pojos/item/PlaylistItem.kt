package kino.malve.kodiAPI.pojos.item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by baganz on 02.11.17.
 */
data class PlaylistItem(

        @SerializedName("playlistid")
        @Expose
        var id: Int

) : IItem