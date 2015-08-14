package com.advrecomm

/**
 * Created by frank on 15-8-9.
 */


import _root_.java.sql.{SQLException, DriverManager, Connection, ResultSet}

import com.advrecomm.util.{DBConn, FileUtil}
import com.advrecomm.vo.User
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.HashSet
import scala.collection.mutable.ArrayBuffer

object DBRun {
  val user="root"
  val password = "34092402"
  val host="localhost"
  val database="ad_recom"
  val conn_str = "jdbc:mysql://"+host +":3306/"+database+"?user="+user+"&password=" + password +
    "&useEncode=true&characterEncoding=gb2312"

  def writeToFile(path : String,array : Array[String]): Unit ={
    val text = new StringBuilder()
    for(i <- 0 until array.length)
      text.append(array(i)).append("\r\n")
    FileUtil.writeToFile(path,text.toString())
  }

  def extractTheme(longTheme : String) : String = {
    longTheme.replaceAll("\\s+","")
    if(longTheme.contains("-")){
      val index = longTheme.lastIndexOf("-")
      if(index != -1 && index < longTheme.length()){
        return longTheme.substring(index+1,longTheme.length()).trim()
      }
      return longTheme
    }else
      return longTheme
  }

  def delDupl(arr : Array[String]): Array[String] ={
    val themeSet = new HashSet[String]()
    for(i <- 0 until arr.length){
      val tmpStr = arr(i)
      if(tmpStr!=null){
        tmpStr.replaceAll("\\s+","")
        if(tmpStr.contains("-")){
          val index = tmpStr.lastIndexOf("-")
          if(index != -1 && index < tmpStr.length()){
            themeSet.add(tmpStr.substring(index+1,tmpStr.length()).trim())
          }
        }else
          themeSet.add(tmpStr)
      }
    }
    return themeSet.toArray
  }

  def logToCount(): Unit ={
    Class.forName("com.mysql.jdbc.Driver").newInstance()
    val conn = DriverManager.getConnection(conn_str)
    try {
      val statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE)

      //构造一个HashMap存储<theme_name,theme_id>方便根据theme_name找theme_id，而不用连接数据库
      val rs1 = statement.executeQuery("select * from themes")
      val tMap = new HashMap[String,Long]()
      while(rs1.next()){
        tMap.put(rs1.getString("name"),rs1.getLong("id"))
      }

      val rs2 = statement.executeQuery("select * from user_log")
      while(rs2.next()){
        val theme = extractTheme(rs2.getString("theme"))
        if(theme!=null&&theme.length>0){
          val themeId = tMap.get(theme).get
          val userId = rs2.getString("userId")
          var userName = rs2.getString("userName")
          if(userName == null && userName.length==0)
            userName = ""

          val prep1 = conn.prepareStatement("select * from user_theme where theme_id=? and user_id=?")
          prep1.setLong(1,themeId)
          prep1.setString(2,userId)
          val rs3 = prep1.executeQuery()

          if(rs3.next()){//判断表中是否已经有了该theme_id和user_id的记录，如果有则更新计数，无则插入新记录
            val count = rs3.getLong("count")+1
            val prep = conn.prepareStatement("update user_theme set count = ? , user_name = ? where theme_id=? and user_id=?")
            prep.setLong(1,count)
            prep.setString(2,userName)
            prep.setLong(3,themeId)
            prep.setString(4,userId)
            prep.executeUpdate()

            println(rs2.getInt("id")+" update "+themeId+" "+userId+" "+userName+" "+count)
          }else{
            val prep = conn.prepareStatement("insert into user_theme(user_id,user_name,theme_id,count) "+
              "values(?,?,?,?)")
            prep.setString(1,userId)
            prep.setString(2,userName)
            prep.setLong(3,themeId)
            prep.setLong(4,1)
            prep.executeUpdate()

            println(rs2.getInt("id")+" insert "+themeId+" "+userId+" "+userName+" "+1)
          }
        }
      }

    }
    catch {
      case ex1 : NullPointerException => ex1.printStackTrace()
      case ex2 : SQLException => ex2.printStackTrace()
    }
    finally {
      conn.close()
    }
  }


  def main(args:Array[String]): Unit ={
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local[10]")
    val sc = new SparkContext(conf)
    logToCount()
  }
}
