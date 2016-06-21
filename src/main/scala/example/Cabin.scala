package example

/**
  * Created by kwang3 on 2016/6/21.
  */
object Cabin {
  def main(args: Array[String]) {
    val exchange = "ss"
    val symbol = "002204"
    val step = 10

    val ssc = new SingletonSparkContext();
    val trainer = new DecisionTreeTrainer(ssc);
    val wizard = new Wizard(trainer)

    val eidolon = wizard.conjure(symbol, exchange, step);

    println(eidolon.toString)
  }
}
