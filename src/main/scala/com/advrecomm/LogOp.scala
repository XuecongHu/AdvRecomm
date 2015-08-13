package com.advrecomm

import _root_.java.sql.{ResultSet, DriverManager}

import com.advrecomm.util.{FileUtil, StringUtil, DBConn}
import com.advrecomm.vo.{Themes, User, UserThemes}
import org.apache.spark.sql.{SaveMode, SQLContext}
import org.apache.spark.{SparkContext}

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, HashMap}

/**
 * Created by frank on 15-8-9.
 */
class LogOp(val sc : SparkContext){
  val sqlContext = new SQLContext(sc)

  def loadLog(): Unit = {
    val userLogFilesPath = "/home/frank/spark/user-log-files"
    val userLogReader = new UserLogReader()
    val logDatas = userLogReader.readFromLocalWholeTexts(userLogFilesPath, sc)

    val writer = new AdDBWriter()
    writer.writeToDB(logDatas, sqlContext)
  }

//  def logToUserTheme() : Unit = {
//    val dfReader = sqlContext.read
//    val userLogDF = dfReader.jdbc(DBConn.url,DBConn.userLogTable,DBConn.properties)
//    val themeDF = dfReader.jdbc(DBConn.url,DBConn.themesTable,DBConn.properties)
//    val userThemeDF = dfReader.jdbc(DBConn.url,DBConn.userThemeTable,DBConn.properties)
//
//    userThemeDF.registerTempTable("user_themes")
//    themeDF.registerTempTable("themes")
//
//    val ulIter = userLogDF.rdd.toLocalIterator
//    val stringUtil = new StringUtil()
//    while(ulIter.hasNext){
//      val ulRow = ulIter.next()
//      val theme = stringUtil.extractTheme(ulRow.getAs("theme"))
//      if(theme!=null&&theme.length>0){
//        val tqDF = sqlContext.sql("select id from themes where name = '"+theme+"'")
//        //判断频道主题名在themes表中能否找到对应id
//        if(tqDF.count()>0){
//          val themeId = tqDF.first().getLong(0)
//
//          val userId = ulRow.getString(1)
//          var userName = ulRow.getString(8)
//          if(userName == null && userName.length==0)
//            userName = ""
//
//          //从暂时的user_theme表中检查有无此项
//          val utDF = sqlContext.sql("select * from user_themes where user_id='"+userId+"' and " +
//            "theme_id='"+themeId+"'")
//          //user_theme表中有此项,则更新其访问计数
//          if(utDF.count()>0){
//            val userThemesItem = utDF.first()
//            val count = userThemesItem.getLong(4)+1
//            val userThemes = new UserThemes(userId,userName,themeId,count)
//            val uArr = Array[UserThemes](userThemes)
//            val userThemeEleDF = sqlContext.createDataFrame(sc.parallelize(uArr))
//            userThemeEleDF.write.mode(SaveMode.Append).insertInto("user_themes")
//          }else{//没有此项，则插入新记录
//            val userThemes = new UserThemes(userId,userName,themeId,1)
//            val uArr = Array[UserThemes](userThemes)
//            val userThemeEleDF = sqlContext.createDataFrame(sc.parallelize(uArr))
//            userThemeEleDF.write.mode(SaveMode.Append).insertInto("user_themes")
//          }
//        }
//      }
//    }
//  }

  def buildUsers(): Unit ={
    val dfReader = sqlContext.read
    val userLogDF = dfReader.jdbc(DBConn.url,DBConn.userLogTable,DBConn.properties)

    val idNameMap = new HashMap[String, String]()
    val ulIter = userLogDF.rdd.toLocalIterator
    while(ulIter.hasNext){
      val ulRow = ulIter.next()

      val userId = ulRow.getString(1)
      val userName = ulRow.getString(8)

      //判断map中是否已经有了该id
      if (idNameMap.contains(userId)) {
        val name = idNameMap.get(userId).get //如果有，先取出对应的name
        if (name == null || name.length() == 0) {
          //判断Name是否为空，如果不空说明已经有了对应的Name，不用更新
          if (userName != null && userName.length > 0) //判断表中读取的username是否为空，如果不空则更新
            idNameMap.update(userId, userName)
        }
      } else {
        //如果无，则直接加到Map中
        idNameMap.put(userId, userName)
      }
    }

    val users = new ArrayBuffer[User]()
    val iter = idNameMap.keysIterator
    while (iter.hasNext) {
      val id = iter.next()
      users += new User(id, idNameMap.get(id).get)
    }

    val userRdd = sc.parallelize(users)
    val writer = new AdDBWriter()
    writer.writeUserToDB(userRdd, sqlContext)
  }


  /*
  临时用于读取主题文件完善论坛主题
   */
  def themesCompletes(): Unit ={
    val dfReader = sqlContext.read
    val themeDF = dfReader.jdbc(DBConn.url,DBConn.themesTable,DBConn.properties)

    val tLines = sc.textFile("/home/frank/spark/论坛主题.txt")
    val tlines = tLines.map(line=>line.split("""\:{1}""")).map(line=>line(1)).map(line=>line.split("""\|{1}"""))

    val themes = tlines.collect()

    val set = new mutable.HashSet[String]()
    val tIter = themeDF.rdd.toLocalIterator

    while(tIter.hasNext){
      val tRow = tIter.next()
      set.add(tRow.getString(1)) //把表中已有的主题名称提取出来，做成一个set，方便后面查询
    }

    var themeId = set.size.toLong + 1L
    val themeItems = new ArrayBuffer[Themes]()

    for(i <- 0 until themes.length){
      val t = themes(i)
      for(j <- 0 until t.length){
        if(!set.contains(t(j))){
          themeItems += new Themes(themeId,t(j))
          themeId += 1
        }
      }
    }

    val themesRdd = sc.parallelize(themeItems)
    val writer = new AdDBWriter()
    writer.writeThemesToDB(themesRdd, sqlContext)
  }
}
