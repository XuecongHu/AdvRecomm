package com.advrecomm

import _root_.java.util.Properties

import com.advrecomm.vo.UserLog
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, SaveMode}

/**
 * Created by frank on 15-8-8.
 */
class UserLogWriter {

  def writeToDB(logDatas : RDD[UserLog] , sqlContext : SQLContext): Boolean ={
    import sqlContext.implicits._
    val df = sqlContext.createDataFrame(logDatas)

    val properties = new Properties()
    properties.setProperty("user","root")
    properties.setProperty("password","34092402")
    properties.setProperty("useEncode","true")
    properties.setProperty("characterEncoding","gb2312")
    try{
      df.write.mode(SaveMode.Append).jdbc("jdbc:mysql://localhost:3306/ad_recom",
        "user_log",properties)
    }catch{
      case ex:MySQLIntegrityConstraintViolationException=>{
        println("数据库主键重复")
        return false
      }
    }

    return true
  }

}
