package example

/**
  * Created by kwang3 on 2016/6/21.
  */
object Cabin {
  def main(args: Array[String]) {
    val symbol = "SZ002204"
    val step = 1

    val ssc = new SingletonSparkContext();
    val trainer = new DecisionTreeTrainer(ssc);
    val wizard = new Wizard(trainer)

    val eidolon = wizard.conjure(symbol, step);

    println(eidolon.toString)
  }
}
