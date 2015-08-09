package com.advrecomm.vo

/**
 * Created by frank on 15-7-27.
 */

case class UserLog(id : Int,userId:String, toUrl:String, ip:String,
                   fromUrl:String, theme:String, browser:String,
                   time:String , userName : String){
  def this(userId:String, toUrl:String,ip:String,fromUrl:String, theme:String,
           browser:String,time:String , userName : String){
    this(0,userId,toUrl,ip,fromUrl,theme,browser,time,userName)
  }
}

