package com.advrecomm.util

import java.util.Properties

/**
 * Created by frank on 15-8-10.
 */
object DBConn {
  val properties = new Properties()
  properties.setProperty("user","root")
  properties.setProperty("password","34092402")
  properties.setProperty("useEncode","true")
  properties.setProperty("characterEncoding","gb2312")

  val url = "jdbc:mysql://localhost:3306/ad_recom"

  val goodsClassTable = "goods_class"
  val themesTable = "themes"
  val themesGoodsClassTable = "themes_goodsClass"
  val userLogTable = "user_log"
  val userThemeTable = "user_theme"
  val userTable = "user"
}
