package com.advrecomm.extractor

import java.net.SocketTimeoutException

import com.advrecomm.vo.{UserText, UserLog}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
/**
 * Created by frank on 15-7-28.
 */

abstract class GeneralExtractor {
  def getWebPage(url : String) :Document={
    if(url != null && url.startsWith("http")){
      try{
        println("start to  get HTML from : "+url)
        val doc = Jsoup.connect(url).timeout(10*1000) //超时
          .get() //连接
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

  protected def extractFromWebPage( userLog : UserLog , extractFunc : (String=>String)) : UserText = {
    if(userLog != null) {
      val webPage = getWebPage(userLog.toUrl)
      val html = webPage.outerHtml()

      if (html != null && html.length > 0) {
        println("extract text from :  " + userLog.toUrl)

        var text = extractFunc(html)
        if (text == null || text.length == 0) {
          text = webPage.title()
        }

        return new UserText(userLog.userId, text, userLog.time)
      }
    }
    return null
  }

  def extract( userLog : UserLog) : UserText
}
