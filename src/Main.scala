import hevs.graphics.FunGraphics

object Main extends App {

  val window = new FunGraphics(
    GameConfig.WINDOW_WIDTH,
    GameConfig.WINDOW_HEIGHT,
    "Crossy Road Scala"
  )
  window.setKeyManager(Inputs)

  val game = new Game(window)
  game.init()

  while (game.isGameRunning) {
    game.update()
    game.render()
    window.syncGameLogic(GameConfig.TARGET_FPS)
  }

  System.exit(0)
}