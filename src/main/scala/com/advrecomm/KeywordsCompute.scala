package com.advrecomm

import com.advrecomm.extractor.MyExtractor
import com.advrecomm.util.{WebPageUtil, TFIDFUtil, JdbcConnect, DBConn}
import com.advrecomm.wordSeg.Tokenizer
import com.hankcs.hanlp.HanLP
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

/**
 * Created by frank on 15-8-15.
 */
class KeywordsCompute(sc: SparkContext) {

  /**
   * 计算该用户浏览过所有网页的所有关键词
   * @param userId
   */
  def compute(userId: String): Unit = {
    val ulo = new UserLogOp(sc)
    val urls = ulo.findUrls(userId) //查找该用户访问过的所有网页url
    if(urls!=null&&urls.length>0){
      val extractor = new MyExtractor()
      val pages = urls.map(extractor.htmlParser(_)) //从网页url提取正文内容
//       .filter(_!=null) //过滤掉空的数据

      //对正文内容进行分词，只保留名词
      val tokenizer = new Tokenizer()
      val nounWords = pages.map(tokenizer.segTextNouns(_))


      val tfidfUtil = new TFIDFUtil(sc)
      val tfidfs = tfidfUtil.compute(nounWords)

      val k = 4
      for ( i <- 0 until tfidfs.length){
        val tfidf = tfidfs(i)
        if(tfidf!=null){
          val seq = tfidf.toSeq.sortBy(-_._2)
          var knum = k
          if(seq.length<k)
            knum = seq.length
          for(j <- 0 until knum){
            val keyword = seq(j)._1
            print(keyword+" "+seq(j)._2+" ")
          }
        }
        val w = new WebPageUtil()
        println(" url: "+urls(i) + "title: " + w.getWebPage(urls(i)).title())

      }
//      val ti = tfidfs.map(_.toSeq.sortBy(-_._2))


//      for (t<-tfidfs){
//        if(t.length>0){
//          var knum = k
//          if(t.length<k)
//            knum = t.length
//          for(i <- 0 until knum){
//            val keyword = t(i)._1
//            print(keyword+" ")
//          }
//          println()
//        }
//      }


    }else{
      println("该用户没有访问网页记录")
    }
  }
}
