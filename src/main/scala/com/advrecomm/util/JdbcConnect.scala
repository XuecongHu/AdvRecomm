package com.advrecomm.util

import java.sql.{Connection, SQLException, DriverManager, ResultSet}

/**
 * Created by frank on 15-8-15.
 */
class JdbcConnect {

  def createConn(): Connection ={
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance()
      return DriverManager.getConnection(DBConn.jdbcConnUrl)
    }catch {
      case ex : InstantiationException => println(ex.getMessage())
      case ex : IllegalAccessException => println(ex.getMessage())
    }
    return null
  }

  def query(sql: String): ResultSet = {
    val conn = createConn()
    if(conn!=null){
      try {
        val statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
        val resultSet = statement.executeQuery(sql)
        return resultSet
      } catch {
        case ex1: SQLException => println(ex1.getMessage())
      }
    }
    return null
  }
}
