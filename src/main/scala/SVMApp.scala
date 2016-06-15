import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.configuration.Algo
import org.apache.spark.mllib.tree.impurity.Gini
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by xiren on 6/9/16.
  */
object SVMApp {

  def main(args: Array[String]) {
    val sc = new SparkContext("local[2]", "SVMApp", new SparkConf());
    val data = sc.textFile("./src/main/resources/data/data_002204_step8.csv")
      .map(_.split(","))
      .map { r =>
        val label = r(3).toInt
        val features = Array(r(0).toDouble, r(1).toDouble, r(2).toDouble)
        LabeledPoint(label, Vectors.dense(features))
      }
    //    val lrModel = LogisticRegressionWithSGD.train(data, 10)
    //    val lrTotalCorrect = data.map { point => if (lrModel.predict(point.features) == point.label) 1 else 0 }.sum()

    val scaler = new StandardScaler(withMean = true, withStd = true).fit(data.map(lp => lp.features))
    val scaledData = data.map(lp => LabeledPoint(lp.label, scaler.transform(lp.features)))

    //    val lrModelScaler = LogisticRegressionWithSGD.train(scaledData, 10)
    //    val lrScalerTotalCorrect = scaledData.map{ point => if (lrModelScaler.predict(point.features) == point.label) 1 else 0}.sum()
    //    println( "Logistic Regression correct rating, befor:" + lrTotalCorrect / data.count() +", after:"+ lrScalerTotalCorrect/scaledData.count())

    val dtModel = DecisionTree.train(scaledData, Algo.Classification, Gini, 20);
    val dtTotalCorrect = scaledData.map { point =>
      val score = dtModel.predict(point.features)
      val predicted = if (score > 0.5) 1 else 0
      if (predicted == point.label) 1 else 0
    }.sum()

    println("correct rating:" + dtTotalCorrect / scaledData.count() + ", predicted:" + dtModel.predict(Vectors.dense(Array(8.05,8.02,11066100))))
  }

}
