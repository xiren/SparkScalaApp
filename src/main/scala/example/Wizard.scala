package example

import javax.inject.Inject

import scala.collection.mutable.ListBuffer

/**
  * Created by kwang3 on 2016/6/21.
  */
class Wizard @Inject()(trainer: DecisionTreeTrainer) {

  def conjure(code: String, market: String, step: Int): Eidolon = {
    val records = YahooConnector.send(code, market).reverse
    val last = records.last
    val lastDate = last(0)
    val classifierPrediction = classifier(records, step)
    val regressionPrediction = regression(records, step)
    new Eidolon(lastDate, regressionPrediction.result, regressionPrediction.rmse, classifierPrediction.accuracy, classifierPrediction.result)
  }

  private def classifier(records: List[Array[String]], step: Int): ClassifierPrediction = {
    val last = records.last
    var index = 0
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      val todayClose = r(4)
      if ((step + index) < records.size) {
        val targetClose = records(step + index)(4)
        var swing = 0
        if ((targetClose.toDouble - todayClose.toDouble) > 0) {
          swing = 1
        } else {
          swing = 0
        }
        trainingData += Array(swing.toString, r(1), r(2), r(3), r(4), r(5))
      }
      index += 1
    }

    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(3).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    trainer.classifierTrain(trainingData.toList, destinationData)
  }

  private def regression(records: List[Array[String]], step: Int): RegressionPrediction = {
    val last = records.last
    var index = 0
    val trainingData: ListBuffer[Array[String]] = new ListBuffer()
    for (r <- records) {
      if ((step + index) < records.size) {
        val targetClose = records(step + index)(4)
        trainingData += Array(targetClose, r(1), r(2), r(3), r(4), r(5))
      }
      index += 1
    }
    val destinationData = Array(last(1).toString.toDouble, last(2).toString.toDouble, last(3).toString.toDouble, last(4).toString.toDouble, last(5).toString.toDouble)
    trainer.trainRegression(trainingData.toList, destinationData)
  }
}

class Eidolon(d: String, v: Double, r: Double, a: Double, u: Any) {
  val lastDate = d
  val value = v
  val rmse = r
  val accuracy = a
  val up = u

  override def toString: String = s"lastDate:" + lastDate + ", value:" + value + ", RMSE:" + rmse + ", accuracy:" + accuracy + ", up:" + up
}


