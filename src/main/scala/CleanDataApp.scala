import java.io.PrintWriter

import scala.io.Source

/**
  * Created by xiren on 6/15/16.
  */
object CleanDataApp {
  def main(args: Array[String]) {
    val lines = Source.fromFile("./src/main/resources/data/002204.csv").getLines();
    val record = lines.map(_.split(",")).filter(_ (5).toDouble != "0".toDouble).map(r => Array(r(1), r(4), r(5))).toList
    val writer = new PrintWriter("./src/main/resources/data/data_002204.csv");
    var open = "";
    record.map { r =>
      val re = r :+ open
      open = if (r(1).toDouble - r(0).toDouble > 0) "1" else "0"
      re
    }.slice(0, record.size - 1).foreach(line => {
      writer.println(line.mkString(","));
    });
    writer.flush();
    writer.close();
  }
}
