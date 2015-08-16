package com.advrecomm.extractor

import java.net.SocketTimeoutException

import com.advrecomm.util.WebPageUtil
import com.advrecomm.vo.{UserText, UserLog}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
/**
 * Created by frank on 15-7-28.
 */

abstract class GeneralExtractor {


  protected def extractFromWebPage( userLog : UserLog , extractFunc : (String=>String)) : UserText = {
    if(userLog != null) {
      val webPageUtil = new WebPageUtil()
      val webPage = webPageUtil.getWebPage(userLog.toUrl)
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

  protected def htmlParserFunc(url : String, parserFunc:String=>String): String ={
    if(url!=null&&url.length>0){
      val webPageUtil = new WebPageUtil()
      val webPage = webPageUtil.getWebPage(url)
      val html = webPage.outerHtml()

      if(html!=null && html.length>0){
        var text = parserFunc(html)
        if(text==null || text.length==0){
          text = webPage.title()
        }
        return text
      }
    }
    return null
  }

  def extract( userLog : UserLog) : UserText

  def htmlParser( url : String) : String
}
