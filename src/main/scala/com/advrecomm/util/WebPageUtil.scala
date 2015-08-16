package com.advrecomm.util

import java.net.SocketTimeoutException

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by frank on 15-8-15.
 */
class WebPageUtil {

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
}
