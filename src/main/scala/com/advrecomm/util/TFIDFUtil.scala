package com.advrecomm.util

import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.{IDF, HashingTF}
import org.apache.spark.mllib.linalg.{DenseVector, SparseVector}
import org.apache.spark.rdd.RDD

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

/**
 * Created by frank on 15-8-15.
 */
class TFIDFUtil(sc : SparkContext) {

  /**
   *
   * @param docsWords 要进行tf-idf计算的文档集合，每个文档包含了已经分好词的所有词语
   * @return 每个文档中每个词汇对应的tf-idf值
   */
  def compute(docsWords : Array[Array[String]]): Array[HashMap[String,Double]]={
    if(docsWords!=null){
      val docNums = docsWords.length  //文档总数
      val tiArray = new ArrayBuffer[HashMap[String,Double]]()

      for(doc<-docsWords){

        val map = new HashMap[String,Double]() //建立一个<词语，tf-idf值>的map，避免同文档中的词语重复计算
        val wordNum = doc.length //单篇文档中的总词数

        for(word<-doc){  //遍历文档中的所有词
          if(!map.contains(word)){  //没有该词语就进行计算
          val countInSingeDoc = doc.count(_.equals(word)) //该词语在其文档中出现的次数
          val tf = countInSingeDoc*1.0/wordNum  //计算词频

            var countInDocs = 0 //该词语在所有文档中出现的次数
            for(tmpDoc <- docsWords){
              if(tmpDoc.count(_.equals(word))>0)
                countInDocs += 1
            }

            val idf = math.log(docNums*1.0/(countInDocs)) //计算逆文档频率

            val tfIdf = tf*idf
            map.put(word,tfIdf)
          }
        }

        tiArray.append(map)
      }
      return tiArray.toArray
    }else
      return null
  }

}

object Main{
  def main(args: Array[String]): Unit = {
    val a = Array("1","2","2")
    println(a.count(_.equals("3")))
  }
}
