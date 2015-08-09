package com.advrecomm

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext}

/**
 * Created by frank on 15-8-9.
 */
class LoadLog {

  def loadLog(sc : SparkContext): Unit = {
    val sqlContext = new SQLContext(sc)
    val userLogFilesPath = "/home/frank/spark/user-log-files"
    val userLogReader = new UserLogReader()
    val logDatas = userLogReader.readFromLocalWholeTexts(userLogFilesPath, sc)

    val writer = new UserLogWriter()
    writer.writeToDB(logDatas, sqlContext)
  }
}
