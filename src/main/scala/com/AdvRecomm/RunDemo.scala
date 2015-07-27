package com.AdvRecomm

import org.apache.spark.{SparkContext, SparkConf}
import com.AdvRecomm.vo.UserText
/**
 * Created by frank on 15-7-27.
 */
object RunDemo {

  def testLocal(): Unit ={
    val userLogFilesPath = "/home/frank/spark/user-log-files/test.txt"
    val userLogReader = new UserLogReader()
    val logDatas = userLogReader.readFromLocal(userLogFilesPath)

    val extractor = new WebPageExctrator()
    val userTexts = extractor.extract(logDatas)

    for (i <- 0 until userTexts.length) {
      userTexts(i).setWords()
      println(userTexts(i).words.mkString(","))
    }
  }

  def testSpark(): Unit ={
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val userLogFilesPath = "/home/frank/spark/user-log-files/test.txt"
    val userLogReader = new UserLogReader()
    val logDatas = userLogReader.readFromLocal(userLogFilesPath,sc)

    val extractor = new WebPageExctrator()
    val userTexts = extractor.extract(logDatas)

    userTexts.map(Tokenizer.seg).foreach(println)
  }

  def main(args : Array[String]): Unit ={
    testSpark()
  }

}
