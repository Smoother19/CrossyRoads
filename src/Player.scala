class Player(startX: Int, startY: Int) {

  private var posX: Int = startX
  private var posY: Int = startY
  private var lastMoveTime: Long = 0
  private val moveDelay: Int = 150 // Le joueur peut bouger tous les 150ms (ajuste selon tes gouts)

  def move(dx: Int, dy: Int, maxX: Int, maxY: Int): Unit = {
    // On regarde l'heure actuelle
    val currentTime = System.currentTimeMillis()

    // Si assez de temps s'est écoulé depuis le dernier mouvement
    if (currentTime - lastMoveTime > moveDelay) {

      val nextX = posX + dx
      val nextY = posY + dy

      if (nextX >= 0 && nextX < maxX && nextY >= 0 && nextY < maxY) {
        posX = nextX
        posY = nextY
        lastMoveTime = currentTime // On note l'heure du mouvement
      }
    }
  }

  def getPos(): (Int, Int) = {
    (posX, posY)
  }
}