package example

import java.io.{BufferedReader, InputStreamReader}
import java.net.URL
import java.time.{LocalDate, ZoneId}
import java.util.Date

import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable

/**
  * Created by kwang3 on 2016/6/22.
  */
object XueqiuConnector {

  val STOCK_URL: String = "http://xueqiu.com/stock/forchartk/stocklist.json?symbol=%s&period=1day&type=normal&begin=%S&end=%s"

  val LOGIN_URL: String = "http://xueqiu.com/"

  val START_TIME = Date.from(LocalDate.of(1990, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant).getTime

  case class Data(stock: Stock, success: Boolean, chartlist: List[Char])

  case class Stock(symbol: String)

  case class Char(volume: Double, open: Double, high: Double, close: Double, low: Double, chg: Double, percent: Double,
                  turnrate: Double, ma5: Double, ma10: Double, ma20: Double, ma30: Double, dif: Double, dea: Double,
                  macd: Double, time: Date)

  def send(symbol: String): Any = {
    val reqUrl = new URL(STOCK_URL.format(symbol, START_TIME, System.currentTimeMillis()))
    val conn = reqUrl.openConnection()
    conn.setRequestProperty("Cookie", getCookie())
    val br = new BufferedReader(new InputStreamReader(conn.getInputStream))
    val json = JsonMethods.parse(br.readLine()) \\ "chartlist"
    for( c <- json.children) {
      val volume = JsonMethods.compact(JsonMethods.render(c \ "volume"))
    }
    Nil
  }


  private def getCookie(): String = {
    val loginUrl = new URL(LOGIN_URL)
    val conn = loginUrl.openConnection()
    conn.connect()
    val map = mutable.HashMap.empty[String, String]
    var i = 1
    var headName = conn.getHeaderFieldKey(i)
    while (headName != null) {
      if ("Set-Cookie" == headName) {
        val cookie = conn.getHeaderField(i).split(";")(0)
        val pairParts = cookie.split("=", 2)
        map += (pairParts(0) -> pairParts(1))
      }
      i += 1
      headName = conn.getHeaderFieldKey(i)
    }
    map.map(r => r._1 + "=" + r._2).mkString("; ")
  }

  def main(args: Array[String]) {
    println(send("SZ002204"))
  }

}
