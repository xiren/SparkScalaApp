import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by xiren on 6/5/16.
  */
object ScalaApp {

  def main(args: Array[String]) {
    val sc = new SparkContext("local[2]", "Spark Scala App", new SparkConf());
    val data = List(Array(1, 2, 5.1), Array(1, 3, 4.1), Array(2, 3, 4.1));
    val rdd = sc.parallelize(data);
    val ratings = rdd.map(r => new Rating(r(0).toInt, r(1).toInt, r(2).toFloat))
    val model = ALS.train(ratings, 50, 10, 0.01);
    println(model.predict(2, 2))
    println(model.recommendProducts(2, 1).mkString("\n"))
  }
}
