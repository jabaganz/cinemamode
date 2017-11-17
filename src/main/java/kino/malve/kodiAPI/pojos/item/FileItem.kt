package kino.malve.kodiAPI.pojos.item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FileItem(

        @SerializedName("file")
        @Expose
        var file: String

) : IItem
