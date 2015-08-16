package com.advrecomm

import com.advrecomm.util.{DBConn, JdbcConnect}
import org.apache.spark.SparkContext

import scala.collection.mutable.ArrayBuffer

/**
 * Created by frank on 15-8-15.
 */
class UserLogOp(sc: SparkContext) {

  /**
   * 提取出该用户浏览过的所有网页url
   * @param userId
   * @return url列表
   */
  def findUrls(userId: String): Array[String] = {
    val jdbc = new JdbcConnect()
    val resultSet = jdbc.query("select * from " + DBConn.userLogTable + " where userId ='" + userId + "'")

    if (resultSet != null) {
      if (resultSet.first) {   //把cursor移到第一行,如果结果集非空，则为true
        val urls = new ArrayBuffer[String]()

        val url = resultSet.getString("toUrl")  //把当前第一行的结果添加进去
        urls.append(url)

        while (resultSet.next) {
          val url = resultSet.getString("toUrl")
          urls.append(url)
        }

        return urls.toArray
      } else {
        println("没有找到该用户('" + userId + "')对应的浏览url")
      }
    } else {
      println("与数据库连接发生问题")
    }
    return null
  }


}
