package com.AdvRecomm.vo

/**
 * Created by frank on 15-7-27.
 */
import com.AdvRecomm.Tokenizer

class UserText ( val id : String, val text : String, val time : String,
                 var words : Array[String]){

  def this ( id : String, text : String, time : String) = {
    this(id,text,time,null)
  }

  def setWords(): Boolean ={
    if( this.text != null){
      this.words = Tokenizer.segText(this.text)
      return true
    }else
      return false
  }

  override def toString()= {
     words.mkString(",")
//    this.text
  }
}
