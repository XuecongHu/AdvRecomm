package com.advrecomm.wordSeg

/**
 * Created by frank on 15-7-27.
 */
/**
 * 分词器
 */

import com.advrecomm.vo.UserText
import com.hankcs.hanlp.HanLP

import scala.collection.mutable.ArrayBuffer

class Tokenizer {

  /**
   * 去掉文本的所有标点符号、换行符、空格
   * @param text
   * @return
   */
  def delPunctuations( text : String ): String ={
    return text.replaceAll("\\pP|\\pS|\\pZ|\\s|\\n","")
  }


  /**
   * 对文本进行分词
   * @param text
   * @return
   */
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


  /**
   * 对文本进行分词，过滤掉所有其他非名词，只保留分词后的名词
   * @param text
   * @return
   */
  def segTextNouns(text:String) : Array[String]={
    if(text!=null){
      val words = HanLP.segment(delPunctuations(text))

      val nounsArray = ArrayBuffer[String]()
      val wordIterator = words.iterator()
      while(wordIterator.hasNext){
        val term = wordIterator.next()
        if(term.nature.name().equals("n")) //只保留名词
          nounsArray += term.word
      }

      return nounsArray.toArray
    }else{
      return null
    }
  }

  def seg( userText : UserText) : UserText = {
    userText.setWords()
    return userText
  }
}
