class Player(startX: Int, startY: Int) {

  private var posX: Int = startX
  private var posY: Int = startY

  def move(dx: Int, dy: Int, maxX: Int, maxY: Int): Boolean = {
    val nextX = posX + dx
    val nextY = posY + dy

    posX = nextX
    posY = nextY
    true
  }

  def x: Int = posX

  def y: Int = posY

  def forceMove(dx: Int, dy: Int, maxX: Int, maxY: Int): Boolean = {
    val nextX = posX + dx
    val nextY = posY + dy

    if (nextX >= 0 && nextX < maxX && nextY >= 0 && nextY < maxY) {
      posX = nextX
      posY = nextY
      true
    } else {
      false
    }
  }

  def resetPos(): Unit = {
    posX = startX
    posY = startY
  }

  def getPos(): (Int, Int) = {
    (posX, posY)
  }
}

