package com.advrecomm.extractor

/**
 * Created by frank on 15-7-28.
 */
/**
 * 使用BoilerPipe技术对正文进行提取
 */

import com.advrecomm.vo.{UserText, UserLog}
import de.l3s.boilerpipe.extractors._

object BPExtractor extends GeneralExtractor{

  override def extract(userLog: UserLog): UserText = {
    return extractFromWebPage(userLog, ArticleExtractor.INSTANCE.getText)
  }

  override def htmlParser(url:String): String ={
    return htmlParserFunc(url, ArticleExtractor.INSTANCE.getText)
  }
}
