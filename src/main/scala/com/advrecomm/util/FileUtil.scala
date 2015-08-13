package com.advrecomm.util

/**
 * Created by frank on 15-7-27.
 */
import java.io.{FileWriter, File, BufferedWriter, IOException}
;
import scala.io.Source

object FileUtil {
  def readFile(path : String) : Array[String] = {
    val source = Source.fromFile(path)
    val lines = source.getLines.toArray
    return lines
  }

  def writeToSameFile(bWriter : BufferedWriter, text : String){
    if(text != null && text.length>0){
      try{
        println("writing to File :"+ text.length)
        bWriter.append(text)
        bWriter.append("-------------------------------------------------------\r\n")
      } catch{
        case ex : IOException => println("write error :" + text)
      }
    }
  }

  def writeToFile(path : String, text : String){
    val file = new File(path)
    val bWriter = new BufferedWriter(new FileWriter(file))
    if(text != null && text.length>0){
      try{
        println("writing to File :"+ text.length)
        bWriter.append(text)
      } catch{
        case ex : IOException => println("write error :" + text)
      }
    }
    bWriter.flush()
    bWriter.close()
  }
}
