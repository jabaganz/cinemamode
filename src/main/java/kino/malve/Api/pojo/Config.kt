package kino.malve.Api.pojo

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.*

data class Config(

        @SerializedName("elements")
        @Expose
        val elements: Stack<Element>

)

