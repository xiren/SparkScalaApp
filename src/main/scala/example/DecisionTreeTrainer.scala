package example

import javax.inject.{Inject, Singleton}

import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.configuration.Algo
import org.apache.spark.mllib.tree.impurity.Gini
import org.apache.spark.rdd.RDD


/**
  * Created by kwang3 on 2016/6/21.
  */

trait Trainer {
  def classifierTrain(trainingData: List[Array[String]], destinationData: Array[Double]): ClassifierPrediction

  def trainRegression(trainingData: List[Array[String]], destinationData: Array[Double]): RegressionPrediction
}

@Singleton
class DecisionTreeTrainer @Inject()(ssc: SingletonSparkContext) extends Trainer {
  val sc = ssc.getSparkContext

  override def classifierTrain(trainingData: List[Array[String]], destinationData: Array[Double]): ClassifierPrediction = {
    val data = toRDD(trainingData)
    val scaledData = scaledFeature(data)

    val dtModel = DecisionTree.train(scaledData, Algo.Classification, Gini, 20)
    val dtTotalCorrect = scaledData.map { point =>
      val score = dtModel.predict(point.features)
      val predicted = if (score > 0.5) 1 else 0
      if (predicted == point.label) 1 else 0
    }.sum()
    new ClassifierPrediction(dtTotalCorrect / scaledData.count(), dtModel.predict(Vectors.dense(destinationData)))
  }

  override def trainRegression(trainingData: List[Array[String]], destinationData: Array[Double]): RegressionPrediction = {
    val data = toRDD(trainingData)
    val scaledData = scaledFeature(data)
    val dtModel = DecisionTree.trainRegressor(scaledData, Map[Int, Int](), "variance", 20, 32);
    val predictionRating = scaledData.map(r => (r.label, dtModel.predict(r.features)))
    new RegressionPrediction(math.sqrt(predictionRating.map(r => (r._1 - r._2) * (r._1 - r._2)).reduce(_ + _) / scaledData.count()), dtModel.predict(Vectors.dense(destinationData)))
  }

  private def scaledFeature(data: RDD[LabeledPoint]): RDD[LabeledPoint] = {
    val scaler = new StandardScaler(withMean = true, withStd = true).fit(data.map(labeledPoint => labeledPoint.features))
    data.map(lp => LabeledPoint(lp.label, scaler.transform(lp.features)))
  }

  private def toRDD(list: List[Array[String]]): RDD[LabeledPoint] = {
    sc.parallelize(list).map { r =>
      val label = r(0).toString.toDouble
      val feature = Array(r(1).toString.toDouble, r(2).toString.toDouble, r(3).toString.toDouble, r(4).toString.toDouble, r(5).toString.toDouble)
      LabeledPoint(label, Vectors.dense(feature))
    }
  }
}

class ClassifierPrediction(a: Double, r: Double) {
  val accuracy: Double = a
  val result = r
}

class RegressionPrediction(a: Double, r: Double) {
  val rmse = a
  val result = r
}

