package com.AdvRecomm

/**
 * Created by frank on 15-7-27.
 */
/**
 * 分词器
 */

import com.AdvRecomm.vo.UserText
import com.hankcs.hanlp.HanLP

import scala.collection.mutable.ArrayBuffer

object Tokenizer {

  private def delPunctuations( text : String ): String ={
    return text.replaceAll("\\pP|\\pS|\\pZ|\\s|\\n","")
  }

  def segText(text: String): Array[String] = {
    val words = HanLP.segment(delPunctuations(text))

    val wordsArray = ArrayBuffer[String]()
    val wordIterator = words.iterator()
    while(wordIterator.hasNext){
      val term = wordIterator.next()
      wordsArray += term.word
    }

    return wordsArray.toArray
  }

  def seg( userText : UserText) : UserText = {
    userText.setWords()
    return userText
  }

}
