package com.advrecomm

import com.advrecomm.util.FileUtil
import com.advrecomm.vo.UserLog
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

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

  def lineData( line : String) : Array[String] = {
    return line.split("""\r\n""")
  }

  /**
   * 将数组对象转换为UserLog对象
   * @param array 分隔日志数据后的数组（userId,toUrl,...,....)
   * @return
   */
  def transToUserLog(array:Array[String]):UserLog = {
    if(array.length==7){
      return new UserLog(array(0),array(1),array(2),array(3),array(4),array(5),array(6),"")
    }else if(array.length==8){
      if(array(7)==null||array(7).equals("")){
        return new UserLog(array(0),array(1),array(2),array(3),array(4),array(5),array(6),"")
      }else
        return new UserLog(array(0),array(1),array(2),array(3),array(4),array(5),array(6),array(7))
    }
      return null
  }



}

class UserLogReader {
  /**
   * 从本地文件系统提取日志数据
   * @param path 日志数据文件所在的路径
   * @return
   */
  def readFromLocal( path : String ): Array[UserLog] = {
    val logDatas = FileUtil.readFile(path)
    return logDatas.map(Reader.formatData).map(Reader.transToUserLog).filter(_!=null)
  }


  /**
   * 从本地文件系统的整个目录提取日志数据
   * @param path 日志数据目录路径
   * @return
   */
  def readFromLocalWholeTexts(path : String , sc : SparkContext) : RDD[UserLog] = {
    val files= sc.wholeTextFiles(path)
    val contents = files.map(f=>f._2)
    val fd =  contents.map(Reader.lineData).collect()
    val datas = new ArrayBuffer[String]()
    val iter = fd.iterator
    while(iter.hasNext){
      val dd = iter.next()
      for(i <- 0 until dd.length){
        if(dd(i)!=null && dd(i).length>0 && !dd(i).equals(""))
          datas.append(dd(i))
      }
    }
    val rdatas = sc.parallelize(datas)

    return rdatas.map(Reader.formatData).map(Reader.transToUserLog).filter(_!=null)
  }

  /**
   * 从本地文件系统提取日志数据
   * @param path 日志数据文件的路径
   * @return
   */
  def readFromLocal( path : String , sc : SparkContext): RDD[UserLog] = {
    val logDatas = sc.textFile(path)
    val userLogs = logDatas.map(Reader.formatData).map(Reader.transToUserLog).filter(_!=null)
    return userLogs
  }
}
