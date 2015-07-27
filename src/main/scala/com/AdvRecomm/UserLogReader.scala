package com.AdvRecomm

import com.AdvRecomm.util.FileUtil
import com.AdvRecomm.vo.UserLog
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
 * Created by frank on 15-7-27.
 */
object Reader{
  /**
   * 清洗日志数据，将原本的格式"user_id|||toUrl|||....|||..."提取出来
   * @param line 一行日志数据
   * @return
   */
  def formatData( line : String) : Array[String]={
    return line.split("""\|{3}""")
  }

  /**
   * 将数组对象转换为UserLog对象
   * @param array 分隔日志数据后的数组（userId,toUrl,...,....)
   * @return
   */
  def transToUserLog(array:Array[String]):UserLog = {
    if(array.length==7){
      return new UserLog(array(0),array(1),array(2),array(3),array(4),array(5),array(6))
    }else
      return null
  }
}

class UserLogReader {
  /**
   * 从本地文件系统提取日志数据
   * @param path 日志数据文件所在的目录
   * @return
   */
  def readFromLocal( path : String ): Array[UserLog] = {
    val logDatas = FileUtil.readFile(path)
    return logDatas.map(Reader.formatData).map(Reader.transToUserLog)
  }

  /**
   * 从本地文件系统提取日志数据
   * @param path 日志数据文件所在的目录
   * @return
   */
  def readFromLocal( path : String , sc : SparkContext): RDD[UserLog] = {
    val logDatas = sc.textFile(path).cache()
    val userLogs = logDatas.map(Reader.formatData).map(Reader.transToUserLog)
    return userLogs
  }
}
