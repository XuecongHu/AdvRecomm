package com.advrecomm

import com.advrecomm.extractor.{MyExtractor, BPExtractor, CxExtractor}
import com.advrecomm.wordSeg.Tokenizer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructType, StructField}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.{SaveMode, Row, SQLContext}

/**
 * Created by frank on 15-7-27.
 */
object RunDemo {

  def testLocal(): Unit ={
    val userLogFilesPath = "/home/frank/spark/user-log-files/test.txt"
    val userLogReader = new UserLogReader()
    val logDatas = userLogReader.readFromLocal(userLogFilesPath)

  }

  def testSpark(): Unit ={
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val userLogFilesPath = "/home/frank/spark/user-log-files/test.txt"
    val userLogReader = new UserLogReader()
    val logDatas = userLogReader.readFromLocal(userLogFilesPath,sc)

    val userTexts = logDatas.map(BPExtractor.extract)
    val tokenizer = new Tokenizer()
    val userWords = userTexts.map(tokenizer.seg)
    userWords.collect().foreach(println)
  }

  def loadLog(): Unit ={
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val loadLog = new LogOp(sc)
    loadLog.themesCompletes()
  }


  def main(args : Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val kc = new KeywordsCompute(sc)
    val userId = "635530495307500000a51"
    kc.compute(userId)

  }
}
