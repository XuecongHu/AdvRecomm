package com.advrecomm.vo

/**
 * Created by frank on 15-8-12.
 */
case class User(id : Long, userId : String , userName : String){
  def this(userId : String, userName : String){
    this(0 , userId, userName)
  }
}
