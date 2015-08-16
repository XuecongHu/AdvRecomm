package com.advrecomm.extractor

import com.advrecomm.java.TextExtract
import com.advrecomm.vo.UserLog
import com.advrecomm.vo.UserText

/**
 * Created by frank on 15-7-27.
 */

object CxExtractor extends GeneralExtractor{
  override def extract(userLog: UserLog): UserText = {
    val textExtract = new TextExtract()
    return extractFromWebPage(userLog,textExtract.parse)
  }

  override def htmlParser(url:String): String ={
    val textExtract = new TextExtract()
    return htmlParserFunc(url, textExtract.parse)
  }
}
