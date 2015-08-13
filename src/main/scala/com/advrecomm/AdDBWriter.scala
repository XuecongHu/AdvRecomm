package com.advrecomm

import com.advrecomm.util.DBConn
import com.advrecomm.vo.{Themes, User, UserLog}
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, SaveMode}

/**
 * Created by frank on 15-8-8.
 */
class AdDBWriter {

  def writeToDB(logDatas : RDD[UserLog] , sqlContext : SQLContext): Boolean ={
    import sqlContext.implicits._
    val df = sqlContext.createDataFrame(logDatas)

    try{
      df.write.mode(SaveMode.Append).jdbc(DBConn.url,DBConn.userLogTable,DBConn.properties)
    }catch{
      case ex:MySQLIntegrityConstraintViolationException=>{
        println("数据库主键重复")
        return false
      }
    }

    return true
  }

  def writeUserToDB(datas : RDD[User] , sqlContext : SQLContext): Boolean ={
    val df = sqlContext.createDataFrame(datas)

    try{
      df.write.mode(SaveMode.Append).jdbc(DBConn.url,DBConn.userTable,DBConn.properties)
    }catch{
      case ex:MySQLIntegrityConstraintViolationException=>{
        println("数据库主键重复")
        return false
      }
    }

    return true
  }

  def writeThemesToDB(datas : RDD[Themes] , sqlContext : SQLContext): Boolean ={
    val df = sqlContext.createDataFrame(datas)

    try{
      df.write.mode(SaveMode.Append).jdbc(DBConn.url,DBConn.themesTable,DBConn.properties)
    }catch{
      case ex:MySQLIntegrityConstraintViolationException=>{
        println("数据库主键重复")
        return false
      }
    }

    return true
  }

}
