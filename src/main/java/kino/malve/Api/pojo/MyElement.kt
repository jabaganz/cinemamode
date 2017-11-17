package kino.malve.Api.pojo

data class MyElement(
        override val name: String,
        override val volume: Int,
        override val lightStatus: Boolean,
        override val format16To9: Boolean,
        override var counter: Int,
        val itemsPath: String
) : Element()
