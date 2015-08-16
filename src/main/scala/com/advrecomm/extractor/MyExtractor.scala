package com.advrecomm.extractor

import com.advrecomm.util.WebPageUtil

/**
 * Created by frank on 15-8-15.
 */
class MyExtractor{

  /**
   * 过滤掉文本里特定的噪音，如论坛中有“幻灯播放”
   * @param text
   * @return
   */
  def wordFilter(text : String): String ={
    return text.replaceAll("幻灯播放","")
  }
  /**
   * 从东方热线论坛中帖子网页中提取帖子内容和评论
   * @param url 帖子网页的url
   */
  def bbsParser(url: String): String = {
    if(url!=null&&url.length>0){
      val webPageUtil = new WebPageUtil()
      val webPage = webPageUtil.getWebPage(url)
      val elements = webPage.getElementsByClass("thread-cont")
      val content = new StringBuilder()
      for(i <- 0 until elements.size()){
        val text = wordFilter(elements.get(i).text())

        if(text.length>0)
          content.append(text)
      }
      return content.toString
    }
    return null
  }

  /**
   * 从网页提取正文内容
   * @param url
   * @return
   */
  def htmlParser(url : String):String={
    if(url!=null&&url.contains("bbs.cnool.net")){  //目前只抓取论坛网页的内容，其他的网页过滤掉
      return bbsParser(url)
    }else
      return null
  }
}

object Main{
  def main(args: Array[String]) {
    val m = new MyExtractor()
    println(m.htmlParser("http://bbs.cnool.net/cthread-105903527.html"))
  }
}
