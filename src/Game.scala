import hevs.graphics.FunGraphics

import java.awt.Color
import javax.swing.JOptionPane

class Game(window: FunGraphics) {

  private val grid = new WorldGrid(GameConfig.ROWS, GameConfig.COLUMNS)
  private val player = new Player(GameConfig.COLUMNS / 2, GameConfig.ROWS - 2)
  private val musicManager = new MusicManager()
  private val menu = new Menu(window)

  private var score: Int = 0
  private var isDead: Boolean = false
  private var isRunning: Boolean = true

  private var xd = 0
  private var yd = 0

  private var scrollTimer: Int = 0
  private var carTimer: Int = 0
  private var logTimer: Int = 0

  private val imgGrass = GameConfig.IMG_GRASS
  private val imgRoad = GameConfig.IMG_ROAD
  private val imgWater = GameConfig.IMG_WATER
  private val imgCar = GameConfig.IMG_CAR
  private val imgLog = GameConfig.IMG_LOG
  private val imgChicken = GameConfig.IMG_CHICKEN

  def init(): Unit = {
    grid.fillEmptyGrid()
    grid.generateObstacles()
    menu.menuManager()
    musicManager.playSound(GameConfig.MUSIC_BG)
  }

  def update(): Unit = {
    if (isDead) {
      handleGameOver()
      return
    }
    handleInputs()
    updateTimers()
  }

  private def handleInputs(): Unit = {
    val inputs = Inputs.getInputs()

    if (inputs.up) {
      yd = -1
    }
    if (inputs.down) {
      yd = 1
    }
    if (inputs.left) {
      xd = -1
    }
    if (inputs.right) {
      xd = 1
    }
    var nextPosX = player.getPos()._1 + xd
    var nextPosY = player.getPos()._2 + yd

    if (!(nextPosX < 0 || nextPosX >= GameConfig.ROWS || nextPosY < 0 || nextPosY >= GameConfig.ROWS)){
      player.move(xd, yd, GameConfig.COLUMNS, GameConfig.ROWS)
      if (yd == -1) score += 1
      if (yd == 1) score -= 1
    }
    checkCollisions()
    xd = 0
    yd = 0
  }

  private def updateTimers(): Unit = {

    scrollTimer += 1
    if (scrollTimer >= GameConfig.SCROLL_INTERVAL) {
      grid.scrollDown()
      player.forceMove(0, 1, GameConfig.COLUMNS, GameConfig.ROWS)
      scrollTimer = 0
    }

    carTimer += 1
    if (carTimer >= GameConfig.CAR_MOVE_INTERVAL) {
      grid.moveCars()
      carTimer = 0
    }

    logTimer += 1
    if (logTimer >= GameConfig.LOG_MOVE_INTERVAL) {
      handleLogMovement()
      logTimer = 0
    }
  }

  private def handleLogMovement(): Unit = {
    val (px, py) = player.getPos()
    val isOnLog = grid.getCell(px, py) == States.LOG

    grid.moveLogs()

    if (isOnLog) {
      val direction = grid.rowDirections(py)
      player.forceMove(direction, 0, GameConfig.COLUMNS, GameConfig.ROWS)
    }
  }

  private def checkCollisions(): Unit = {
    if (grid.collisionSystemReact(player)) {
      isDead = true
      musicManager.stopMusic()
      musicManager.playSound(GameConfig.MUSIC_LOSE)
    }
  }

  private def handleGameOver(): Unit = {

    val message = s"Votre score : $score\nVoulez-vous rejouer ?"
    val result = JOptionPane.showConfirmDialog(
      window.mainFrame,
      message,
      "Game Over!",
      JOptionPane.YES_NO_OPTION
    )

    if (result == JOptionPane.YES_OPTION) {
      resetGame()
    } else {
      musicManager.stopMusic()
      isRunning = false
    }
  }

  private def resetGame(): Unit = {
    grid.fillEmptyGrid()
    grid.generateObstacles()
    player.resetPos()
    score = 0
    isDead = false
    scrollTimer = 0
    carTimer = 0
    logTimer = 0
    musicManager.playSound(GameConfig.MUSIC_BG)
  }

  def render(): Unit = {

    for (y <- 0 until GameConfig.ROWS) {
      for (x <- 0 until GameConfig.COLUMNS) {
        val cellType = grid.getCell(x, y)
        val posX = (x * GameConfig.CELL_SIZE) + GameConfig.HALF_CELL
        val posY = (y * GameConfig.CELL_SIZE) + GameConfig.HALF_CELL

        val bgBitmap = cellType match {
          case States.WATER => imgWater
          case States.ROAD | States.CAR => imgRoad
          case _ => imgGrass
        }
        window.drawPicture(posX, posY, bgBitmap)

        cellType match {
          case States.CAR => window.drawPicture(posX, posY, imgCar)
          case States.LOG => window.drawPicture(posX, posY, imgLog)
          case _          =>
        }
      }
    }

    val (px, py) = player.getPos()
    val playerX = (px * GameConfig.CELL_SIZE) + GameConfig.HALF_CELL
    val playerY = (py * GameConfig.CELL_SIZE) + GameConfig.HALF_CELL
    window.drawPicture(playerX, playerY, imgChicken)

    window.setColor(Color.WHITE)
    window.drawString(10, 20, s"Score: $score")
  }

  def isGameRunning: Boolean = isRunning
}