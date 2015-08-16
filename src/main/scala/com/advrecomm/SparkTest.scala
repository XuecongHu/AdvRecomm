package com.advrecomm

/**
 * Created by frank on 15-7-27.
 */

import _root_.java.util.TreeMap

import com.advrecomm.extractor.{MyExtractor, CxExtractor, BPExtractor}
import com.advrecomm.wordSeg.Tokenizer
import com.hankcs.hanlp.HanLP
import com.qcloud.Module.{Wenzhi}
import com.qcloud.QcloudApiModuleCenter
import com.qcloud.Utilities.Json.JSONObject
import org.ansj.splitWord.analysis.{NlpAnalysis, ToAnalysis}
import org.apache.spark.mllib.feature.{IDF, HashingTF}
import org.apache.spark.mllib.linalg.{DenseVector, SparseVector}
import org.apache.spark.{SparkContext, SparkConf}
import org.apdplat.word.WordSegmenter

import scala.collection.mutable.ArrayBuffer

object SparkTest {
  def tfidfTest(docWords : Array[Array[String]], sc :SparkContext) {

//    val document = sc.textFile("/home/frank/spark/text-files/ti-test/").map(_.split(",").toSeq)
    val document = sc.parallelize(docWords.map(_.toSeq))

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
      val na = arrays.sortWith(_>_)
      tfidfArrays.append(na)
    }

    tfidfArrays.foreach(print)
//    val docs = document.collect()
//
//    for(i <- 0 until tfidfArrays.length){
//      val tfidInADoc = tfidfArrays(i)
//      var max = 0.0
//      var maxIndex = -1
//      for(j <- 0 until tfidInADoc.length){
//        if(tfidInADoc(j)>max){
//          max = tfidInADoc(j)
//          maxIndex = j
//        }
//      }
//
//      if(maxIndex != -1){
//        val keyWord = docs(i)(maxIndex)
//        println(maxIndex+"  "+keyWord+" "+max)
//      }
//    }
  }

  def main(args: Array[String]) {

//    val content =
//      """
//        每天上午8时，浙江杭州江干区解放东路58号J座杭州图书馆新馆楼下就站了不少人。晨练的市民发现，总有些人扛着脏兮兮的大麻袋，或拎一兜饮料瓶，衣衫褴褛。副馆长梁亮对这一幕再熟悉不过：一些流浪、拾荒者正等着图书馆开门，“在旧馆就常看到很多流浪、拾荒者背着铺盖，把捡来的垃圾放在阅览室门口，再进去看书。”其实杭图不仅允许流浪、拾荒者入馆，也允许他们携行李入内。而将杂物放于门外，则是自发行为。来源：法制晚报
//        免责声明:本网转载内容均注明出处，转载是出于传递更多信息之目的，并不意味着赞同其观点或证实其内容的真实性。本网所刊载的作品，其版权归属原作者或所属媒体所有，请原作者与我们联系稿酬事宜。
//       """
//    val keywordList = HanLP.extractKeyword(content, 5);
//    val sentenceList = HanLP.extractPhrase(content,5)
//    val summary = HanLP.extractSummary(content, 2)
//
//    println(keywordList)
//    println(sentenceList)
//    println(summary)

//    val config = new TreeMap[String, Object]()
//
//    config.put("SecretId", "AKIDWS6a8YDseSyzfzRI2yZRciPLgHPAuhRZ")
//    config.put("SecretKey", "CX8NgNYlznxMhC1VKCtjutzkwt19YLjO")
//    config.put("RequestMethod", "GET")
//    config.put("DefaultRegion", "gz")
//    val module = new QcloudApiModuleCenter(new Wenzhi(), config)
//
//    val paramsCG = new TreeMap[String, Object]()
//    try{
//
//      paramsCG.put("url", "http://bbs.cnool.net/cthread-104993790.html")
//      val result = module.call("ContentGrab", paramsCG)
//      val json_result = new JSONObject(result)
//      val code = json_result.getInt("code")
//
//      if(code==0){
//        val title = json_result.getString("title")
//        val content = json_result.getString("content")
//
//        val paramsTK = new TreeMap[String, Object]()
//        paramsTK.put("title", title)
//        paramsTK.put("content", content)
//
//        val tkJsonResult = new JSONObject(module.call("TextKeywords", paramsTK))
//        println(tkJsonResult.toString())
//        val tkCode = tkJsonResult.getInt("code")
//        if(tkCode==0){
//          val keywordsArray = tkJsonResult.getJSONArray("keywords")
//          if(keywordsArray!=null&&keywordsArray.length()>0){
//            for(i <- 0 until keywordsArray.length())
//              println(keywordsArray.getJSONObject(i).getString("keyword"))
//          }
//        }
//
//
//      }else{
//        println("调用错误")
//      }
//
//    }
//    catch{
//      case ex : Exception =>println(ex.getMessage())
//    }
      val e = new MyExtractor()
      val page = e.htmlParser("http://bbs.cnool.net/cthread-105613278.html")
      val t = new Tokenizer()
      val c = t.delPunctuations(page)
      println(c)
  }
}

