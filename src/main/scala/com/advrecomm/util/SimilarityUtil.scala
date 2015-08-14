package com.advrecomm.util

/**
 * Created by frank on 15-8-13.
 */

import breeze.linalg._

object SimilarityUtil {
  def cosSim(vec1:DenseVector[Long], vec2:DenseVector[Long]): Double ={
    return (vec1 dot vec2)/(norm(vec1)*norm(vec2))
  }

  def main(args: Array[String]) {
    val map = Map((1,2))
    val a = map.get(2)
    if(a==None)
      println("a")
  }
}
