package com.advrecomm.vo

/**
 * Created by frank on 15-8-12.
 */
case class Themes (id : Long, name : String){
  def this(name : String){
    this(0, name)
  }
}
