package com.market.shared

object Utils {

  implicit class StringExt(value: String) {

    def toSnakeCase: String =
      value.map(x => if (!x.isLower) s"_${x.toLower}" else x).mkString.tail
  }

  implicit class ObjectGetName(`object`: Any) {
    def getClearedClassName: String =
      `object`.getClass.getName.split("\\.").last.split("\\$").last
    def toSnakeCaseClassName: String = `object`.getClearedClassName.toSnakeCase
  }
}
