package com.advrecomm.vo

/**
 * Created by frank on 15-8-13.
 */
case class SimilarUser (id:Long, userSysId1:Long, userSysId2:Long, similarity:Double){
  def this(userSysId1:Long, userSysId2:Long, similarity:Double){
    this(0, userSysId1, userSysId2, similarity)
  }
}
