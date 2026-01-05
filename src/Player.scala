class Player(startX: Int, startY: Int) {

  private var posX: Int = startX
  private var posY: Int = startY
  private var lastMoveTime: Long = 0
  private val moveDelay: Int = 100

  def move(dx: Int, dy: Int, maxX: Int, maxY: Int, grid: WorldGrid): Unit = {

    val currentTime = System.currentTimeMillis()


    if (currentTime - lastMoveTime > moveDelay) {

      val nextX = posX + dx
      val nextY = posY + dy

      if (nextX >= 0 && nextX < maxX && nextY >= 0 && nextY < maxY) {
        if (!grid.collisionSystemPredict((posX, posY), (nextX, nextY))) {
          posX = nextX
          posY = nextY
          lastMoveTime = currentTime
        }
      }
    }
  }


  def forceMove(dx: Int, dy: Int, maxX: Int, maxY: Int): Unit = {
      val nextX = posX + dx
      val nextY = posY + dy

      if (nextX >= 0 && nextX < maxX && nextY >= 0 && nextY < maxY) {
        posX = nextX
        posY = nextY
      }
  }

  def getPos(): (Int, Int) = {
    (posX, posY)
  }
}