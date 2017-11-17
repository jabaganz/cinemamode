package kino.malve.Api.pojo

abstract class Element {
    abstract val name: String
    abstract val volume: Int
    abstract val lightStatus: Boolean
    abstract val format16To9: Boolean
    abstract var counter: Int
}