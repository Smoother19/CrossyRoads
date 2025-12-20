import hevs.graphics.FunGraphics

object Main extends App {

  val window = new FunGraphics(500, 500, "CrossyRoads project Scala")

  window.setKeyManager(Inputs)

  gameLoop()

  def gameLoop(): Unit = {
    while (true) {
      val inputs = Inputs.getInputs()

    }
  }

  def display(): Unit = ???

  def checkCollision(pos: Array[Int], nextPos: Array[Int]): Boolean = ???
}