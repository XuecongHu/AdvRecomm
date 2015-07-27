package com.AdvRecomm

/**
 * Created by frank on 15-7-27.
 */

import _root_.java.net.SocketTimeoutException

import com.AdvRecomm.java.TextExtract
import com.AdvRecomm.vo.{UserLog, UserText}
import org.apache.spark.rdd.RDD
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object Extractor{

  def getWebPage(url : String) :Document={
    if(url != null && url.startsWith("http")){
      try{
        println("start to  get HTML from : "+url)
        val doc = Jsoup.connect(url).timeout(10*1000) //设置连接超时时间
          .get() //获取该url的Docment对象
        return doc
      } catch{
        case ex : SocketTimeoutException =>{
          println("get url timeout: "+url)
        }
      }
    }else
      println("url format error: "+url)
    return null
  }

  def extractFromWebPage( userLog : UserLog ) : UserText = {
    if(userLog != null) {
      val webPage = getWebPage(userLog.toUrl)
      val html = webPage.outerHtml()

      if (html != null && html.length > 0) {
        println("extract text from :  " + userLog.toUrl)

        val textExtract = new TextExtract()
        var text = textExtract.parse(html)
        println(text)

        if (text == null || text.length == 0) {
          text = webPage.title()
        }

        return new UserText(userLog.id, text, userLog.time)
      }
    }
    return null
  }
}

class WebPageExctrator {
  def extract( userLogs : Array[UserLog]): Array[UserText] ={
    return userLogs.map(Extractor.extractFromWebPage)
  }

  def extract( userLogs : RDD[UserLog]): RDD[UserText] ={
    return userLogs.map(Extractor.extractFromWebPage)
  }
}
