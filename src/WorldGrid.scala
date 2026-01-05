import scala.util.Random

class WorldGrid(length: Int, width: Int) {

  var grid: Array[Array[Int]] = Array.ofDim(length, width)

  def fillEmptyGrid(emptyGrid: Array[Array[Int]]): Array[Array[Int]] = {
    for (row <- 0 until length) {
      for (col <- 0 until width) {
        emptyGrid(row)(col) = States.GRASS
      }
    }
    emptyGrid
  }

  def generateObstacles(gridToFill: Array[Array[Int]]): Array[Array[Int]] = {
    val random = new Random()
    var row = 0

    while (row < length) {

      val terrainType = random.nextInt(4)


      val height: Int = terrainType match{
        case States.ROAD | States.WATER => 2
        case States.TREE | States.GRASS => 1
      }

      var safeType = terrainType
      if (row + height > length){
        safeType = States.GRASS
      }

      var safeHeight = height
      if (row + height > length) {
        safeHeight = 1
      }

      for (i <- 0 until safeHeight) {
        for (col <- 0 until width) {


          if (safeType == States.ROAD && random.nextInt(100) < 15) {
            gridToFill(row + i)(col) = States.CAR
          } else {

            gridToFill(row + i)(col) = safeType
          }

        }
      }
      row += safeHeight
      row += safeHeight
    }
    gridToFill
  }

  def getCell(x: Int, y: Int): Int = {
    if (x >= 0 && x < width && y >= 0 && y < length) {
      grid(y)(x)
    } else {
      States.GRASS
    }
  }

  def scrollDown(): Unit = {
    for (i <- length - 1 until 0 by -1) {
      grid(i) = grid(i - 1)
    }

    val newRow = new Array[Int](width)
    val random = new Random()
    var terrainType = States.GRASS

    val typeBelow = grid(1)(0)

    val typeTwoBelow = if (length > 2) grid(2)(0) else -1

    if (typeBelow == States.ROAD || typeBelow == States.WATER) {

      if (typeBelow != typeTwoBelow) {

        terrainType = typeBelow
      } else {

        terrainType = random.nextInt(4)
      }
    }
    else {
      terrainType = random.nextInt(4)
    }


    for (col <- 0 until width) {
      if (terrainType == States.ROAD && random.nextInt(100) < 15) {
        newRow(col) = States.CAR
      } else {
        newRow(col) = terrainType
      }
    }

    grid(0) = newRow
  }

  // Déplacement des voitures vers la droite
  def moveCars(): Unit = {
    val random = new Random()

    for (y <- 0 until length) {

      for (x <- width - 1 to 0 by -1) {

        if (grid(y)(x) == States.CAR) {

          if (x + 1 >= width) {
            grid(y)(x) = States.ROAD
          }

          else if (grid(y)(x + 1) != States.CAR) {
            grid(y)(x + 1) = States.CAR
            grid(y)(x) = States.ROAD
          }
        }
      }

      if (grid(y)(0) == States.ROAD) {
        if (random.nextInt(100) < 5) {
          grid(y)(0) = States.CAR
        }
      }
    }
  }


  def collisionSystemPredict(playerPos: (Int, Int), nextPos: (Int, Int)): Boolean = {
    val nextPoseState = getCell(nextPos._1, nextPos._2)
    nextPoseState == States.CAR
  }


  def collisionSystemReact(player: Player): Boolean = {
    val (x, y) = player.getPos()
    val currentState = getCell(x, y)
    currentState == States.WATER || currentState == States.CAR
  }
}
