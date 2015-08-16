package com.advrecomm.util

import java.util.Properties

/**
 * Created by frank on 15-8-10.
 */
object DBConn {
  val properties = new Properties()

  val user = "root"
  val password = "34092402"
  val useEncode = "true"
  val charset = "gb2312"

  properties.setProperty("user",user)
  properties.setProperty("password",password)
  properties.setProperty("useEncode",useEncode)
  properties.setProperty("characterEncoding",charset)

  val url = "jdbc:mysql://localhost:3306/ad_recom"

  val jdbcConnUrl = url+"?user="+user+"&password="+password+"&useEncode="+
    useEncode+"&characterEncoding="+charset

  val goodsClassTable = "goods_class"
  val themesTable = "themes"
  val themesGoodsClassTable = "themes_goodsClass"
  val userLogTable = "user_log"
  val userThemeTable = "user_theme"
  val userTable = "user"
  val simiUsersTable = "similar_users"
}
