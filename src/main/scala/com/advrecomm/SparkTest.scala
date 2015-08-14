package com.advrecomm

/**
 * Created by frank on 15-7-27.
 */

import org.apache.spark.mllib.feature.{IDF, HashingTF}
import org.apache.spark.mllib.linalg.{DenseVector, SparseVector}
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.ArrayBuffer

object SparkTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("mlib-df-idf test").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val document = sc.textFile("/home/frank/spark/text-files/ti-test/").map(_.split(",").toSeq)

    val hashingTF = new HashingTF()
    val tf = hashingTF.transform(document)

    tf.cache()
    val idf = new IDF().fit(tf)
    val tfidf = idf.transform(tf)

    val tfidfArrays = new ArrayBuffer[ArrayBuffer[Double]]()
    val iter = tfidf.toLocalIterator
    while(iter.hasNext){
      val arrays = new ArrayBuffer[Double]()
      val v = iter.next()
      v match{
        case sv : SparseVector =>
          val N = sv.indices.size
          var k = 0
          while(k<N){
            arrays.append(sv.values(k))
            k += 1
          }
        case dv : DenseVector =>
          val N = dv.size
          var k = 0
          while(k<N){
            print(dv.values(k)+" ")
            k += 1
          }
      }
      tfidfArrays.append(arrays)
    }

    val docs = document.collect()

    for(i <- 0 until tfidfArrays.length){
      val tfidInADoc = tfidfArrays(i)
      var max = 0.0
      var maxIndex = -1
      for(j <- 0 until tfidInADoc.length){
        if(tfidInADoc(j)>max){
          max = tfidInADoc(j)
          maxIndex = j
        }
      }

      if(maxIndex != -1){
        val keyWord = docs(i)(maxIndex)
        println(maxIndex+"  "+keyWord+" "+max)
      }
    }
  }
}
