import hevs.graphics.FunGraphics
import java.awt.Color
import hevs.graphics.utils.GraphicsBitmap
import java.awt.event.{KeyAdapter, KeyEvent}

object Main extends App {

  val columns = 15      // Largeur de la grille (nombre de cases)
  val rows = 20         // Hauteur de la grille
  val cellSize = 50     // Taille d'une case en pixels

  val windowWidth = columns * cellSize
  val windowHeight = rows * cellSize

  val window = new FunGraphics(windowWidth, windowHeight, "Crossy Road Scala")
  window.setKeyManager(Inputs)
  val grid = new WorldGrid(rows, columns)

  val player = new Player(columns / 2, rows - 2)

  grid.fillEmptyGrid(grid.grid)
  grid.generateObstacles(grid.grid)

  def display(): Unit = {

    for (y <- 0 until rows) {
      for (x <- 0 until columns) {


        val cellType = grid.getCell(x, y)


        val color = cellType match {
          case States.GRASS => new Color(34, 139, 34)
          case States.ROAD  => Color.GRAY
          case States.WATER => Color.BLUE
          case States.TREE  => new Color(0, 100, 0)
          case States.CAR   => Color.RED
          case States.LOG   => new Color(150,90,55)
          case _            => Color.WHITE
        }

        window.setColor(color)
        window.drawFillRect(x * cellSize, y * cellSize, cellSize, cellSize)


        window.setColor(Color.BLACK)
        window.drawRect(x * cellSize, y * cellSize, cellSize, cellSize)

        val (px, py) = player.getPos()

        window.setColor(Color.YELLOW)

        window.drawFillRect(px * cellSize + 5, py * cellSize + 5, cellSize - 10, cellSize - 10)
      }
    }
  }

  def resetGame(): Unit = {
    grid.fillEmptyGrid(grid.grid)
    grid.generateObstacles(grid.grid)
    player.resetPos()
  }

  // Main
  var time = 0

  var scrollTimer = 0
  var carTimer = 0
  var isDead = false

  while (true) {

    if (!isDead) {
      val inputs = Inputs.getInputs()
      if (inputs.up)    player.move(0, -1, columns, rows, grid)
      if (inputs.down)  player.move(0, 1,  columns, rows, grid)
      if (inputs.left)  player.move(-1, 0, columns, rows, grid)
      if (inputs.right) player.move(1, 0,  columns, rows, grid)

      scrollTimer += 1
      if (scrollTimer >= 50) {
        grid.scrollDown()
        player.forceMove(0, 1, columns, rows) // Le joueur descend avec le monde
        scrollTimer = 0 // Reset du timer
      }

      carTimer += 1
      if (carTimer >= 10) {
        grid.moveCars()
        grid.moveLogs()
        carTimer = 0
      }


      if (grid.collisionSystemReact(player)) {
        println("Game Over!")
        isDead = true
      }
    } else {

      val inputs = Inputs.getInputs()
      if (inputs.enter) {
        resetGame()
        isDead = false

      }
    }

    display()

    Thread.sleep(30) // 30ms = ~33 FPS
  }
}