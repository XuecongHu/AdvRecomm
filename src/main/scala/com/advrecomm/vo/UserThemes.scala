package com.advrecomm.vo

/**
 * Created by frank on 15-8-10.
 */
case class UserThemes (id:Long,userId:String, userName:String,themeId:Long,count:Long){
  def this(userId:String, userName:String,themeId:Long,count:Long){
    this(0,userId,userName,themeId,count)
  }
}
