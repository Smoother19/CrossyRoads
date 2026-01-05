import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap

import java.awt.Color
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

  val imgGrass   = new GraphicsBitmap("/grass.jpg")
  val imgRoad    = new GraphicsBitmap("/road.png")
  val imgWater   = new GraphicsBitmap("/water.jpg")
  val imgCar     = new GraphicsBitmap("/car.png")
  val imgLog     = new GraphicsBitmap("/log.jpg")
  val imgTree    = new GraphicsBitmap("/grass.jpg")
  val imgChicken = new GraphicsBitmap("/imageChicken.png")

  def display(): Unit = {
    for (y <- 0 until rows) {
      for (x <- 0 until columns) {
        val cellType = grid.getCell(x, y)
        val posX = x * cellSize
        val posY = y * cellSize

        val bgBitmap = cellType match {
          case States.WATER | States.LOG => imgWater
          case States.ROAD | States.CAR  => imgRoad
          case _                         => imgGrass
        }

        window.drawPicture(posX, posY, bgBitmap)

        cellType match {
          case States.CAR  => window.drawPicture(posX, posY, imgCar)
          case States.LOG  => window.drawPicture(posX, posY, imgLog)
          case States.TREE => window.drawPicture(posX, posY, imgTree)
          case _ => ()
        }
      }
    }

    val (px, py) = player.getPos()
    window.drawPicture(px * cellSize, py * cellSize, imgChicken)
  }

  def resetGame(): Unit = {
    grid.fillEmptyGrid(grid.grid)
    grid.generateObstacles(grid.grid)
    player.resetPos()
  }

  // Main
  var time = 0

  val maxTime = 30
  var scrollTimer = 0
  var carTimer = 0
  var logTimer = 0
  var isDead = false

  while (true) {
    val startTime = System.currentTimeMillis()
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
        carTimer = 0
      }

      logTimer += 1
      if (logTimer >= 20) {
        // 2. Mouvement Passif (Suivre la bûche)

        // On vérifie SI le joueur est sur une bûche AVANT de bouger les bûches
        val (px, py) = player.getPos()
        val isOnLog = grid.getCell(px, py) == States.LOG

        // On déplace les bûches
        grid.moveLogs()

        // Si le joueur était sur une bûche, on le déplace dans la même direction
        if (isOnLog) {
           // CORRECTION : On utilise la direction stockée dans la grille pour cette ligne spécifique
           val direction = grid.rowDirections(py)
           player.forceMove(direction, 0, columns, rows)
        }

        logTimer = 0
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
    val endTime = System.currentTimeMillis()

    Thread.sleep(maxTime - (endTime - startTime)) // 30ms = ~33 FPS
  }
}