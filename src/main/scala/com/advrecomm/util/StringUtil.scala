package com.advrecomm.util

/**
 * Created by frank on 15-8-10.
 */
class StringUtil {
  def extractTheme(longTheme : String) : String = {
    longTheme.replaceAll("\\s+","")
    if(longTheme.contains("-")){
      val index = longTheme.lastIndexOf("-")
      if(index != -1 && index < longTheme.length()){
        return longTheme.substring(index+1,longTheme.length()).trim()
      }
      return longTheme
    }else
      return longTheme
  }
}
