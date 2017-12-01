package kino.malve.Api.pojo

interface Element {
      val type: String
      val javaType: String
      val volume: Int
      val lightStatus: Boolean
      val format16To9: Boolean
      var counter: Int
}