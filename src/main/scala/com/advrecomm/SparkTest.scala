package com.advrecomm

/**
 * Created by frank on 15-7-27.
 */

import com.advrecomm.util.FileUtil
import org.jsoup.Jsoup

/** Computes an approximation to pi */
object SparkTest {
  def main(args: Array[String]) {
    val themes = FileUtil.readFile("/home/frank/spark/主题列表.txt")
    themes.foreach(println)
  }
}
