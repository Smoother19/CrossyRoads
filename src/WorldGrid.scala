import scala.util.Random

class WorldGrid(val length: Int, val width: Int) {


  private val random = new Random()

  private var _grid: Array[Array[Int]] = Array.ofDim(length, width)
  def grid: Array[Array[Int]] = _grid

  private var _rowDirections: Array[Int] = Array.fill(length)(1)
  def rowDirections: Array[Int] = _rowDirections

  def fillEmptyGrid(): Unit = {
    for (row <- 0 until length; col <- 0 until width) {
      _grid(row)(col) = States.GRASS
    }
  }

  def generateObstacles(): Unit = {
    var row = 0

    while (row < length) {
      var terrainType = random.nextInt(4)

      if (row >= length - 3) {
        terrainType = States.GRASS
      } else if (row == length - 4 && (terrainType == States.WATER || terrainType == States.ROAD)) {
        terrainType = States.GRASS
      }

      val height = terrainType match {
        case States.ROAD | States.WATER => 2
        case _ => 1
      }

      val safeType = if (row + height > length) States.GRASS else terrainType
      val safeHeight = if (row + height > length) 1 else height

      var dir = if (random.nextBoolean()) 1 else -1

      for (i <- 0 until safeHeight) {
        if (safeType == States.WATER && i > 0) dir = -dir
        _rowDirections(row + i) = dir

        generateRowContent(row + i, safeType)
      }

      row += safeHeight
    }
  }

  private def generateRowContent(row: Int, terrainType: Int): Unit = {
    var col = 0
    while (col < width) {
      terrainType match {
        case States.ROAD if random.nextInt(100) < GameConfig.CAR_SPAWN_CHANCE =>
          _grid(row)(col) = States.CAR
          col += 1

        case States.WATER if random.nextInt(100) < GameConfig.LOG_SPAWN_CHANCE =>
          _grid(row)(col) = States.LOG

          if (col + 1 < width) {
            _grid(row)(col + 1) = States.LOG
            col += 2
          } else {
            col += 1
          }

        case _ =>
          _grid(row)(col) = terrainType
          col += 1
      }
    }
  }

  def getCell(x: Int, y: Int): Int = {
    if (x >= 0 && x < width && y >= 0 && y < length) {
      _grid(y)(x)
    } else {
      States.GRASS
    }
  }


  def scrollDown(): Unit = {
    for (i <- length - 1 until 0 by -1) {
      _grid(i) = _grid(i - 1)
      _rowDirections(i) = _rowDirections(i - 1)
    }
    generateNewTopRow()
  }

  private def generateNewTopRow(): Unit = {
    val newRow = new Array[Int](width)

    val typeBelow = _grid(1)(0)
    val typeTwoBelow = if (length > 2) _grid(2)(0) else -1

    val terrainType = if (typeBelow == States.ROAD || typeBelow == States.WATER) {
      if (typeBelow != typeTwoBelow) typeBelow else random.nextInt(4)
    } else {
      random.nextInt(4)
    }

    _rowDirections(0) = if (terrainType == States.WATER && typeBelow == States.WATER) {
      -_rowDirections(1)
    } else {
      if (random.nextBoolean()) 1 else -1
    }

    var col = 0
    while (col < width) {
      terrainType match {
        case States.ROAD if random.nextInt(100) < GameConfig.CAR_RESPAWN_CHANCE =>
          newRow(col) = States.CAR
          col += 1

        case States.WATER if random.nextInt(100) < GameConfig.LOG_SPAWN_CHANCE =>
          newRow(col) = States.LOG
          if (col + 1 < width) {
            newRow(col + 1) = States.LOG
            col += 2
          } else {
            col += 1
          }

        case _ =>
          newRow(col) = terrainType
          col += 1
      }
    }

    _grid(0) = newRow
  }

  def moveCars(): Unit = {
    for (y <- 0 until length) {
      for (x <- width - 1 to 0 by -1) {
        if (_grid(y)(x) == States.CAR) {
          if (x + 1 >= width) {
            _grid(y)(x) = States.ROAD
          } else if (_grid(y)(x + 1) != States.CAR) {
            _grid(y)(x + 1) = States.CAR
            _grid(y)(x) = States.ROAD
          }
        }
      }

      if (_grid(y)(0) == States.ROAD && random.nextInt(100) < GameConfig.CAR_RESPAWN_CHANCE) {
        _grid(y)(0) = States.CAR
      }
    }
  }


  def moveLogs(): Unit = {
    for (y <- 0 until length) {
      if (_rowDirections(y) == 1) {
        moveLogsRight(y)
      } else {
        moveLogsLeft(y)
      }
    }
  }

  private def moveLogsRight(y: Int): Unit = {

    for (x <- width - 1 to 0 by -1) {
      if (_grid(y)(x) == States.LOG) {
        if (x + 1 >= width) {
          _grid(y)(x) = States.WATER
        } else if (_grid(y)(x + 1) != States.LOG) {
          _grid(y)(x + 1) = States.LOG
          _grid(y)(x) = States.WATER
        }
      }
    }

    if (_grid(y)(0) == States.WATER) {
      val isHeadAtOne = width > 1 && _grid(y)(1) == States.LOG && (width <= 2 || _grid(y)(2) != States.LOG)
      if (isHeadAtOne) {
        _grid(y)(0) = States.LOG
      } else if ((width <= 1 || _grid(y)(1) != States.LOG) && random.nextInt(100) < GameConfig.LOG_SPAWN_CHANCE) {
        _grid(y)(0) = States.LOG
      }
    }
  }

  private def moveLogsLeft(y: Int): Unit = {

    for (x <- 0 until width) {
      if (_grid(y)(x) == States.LOG) {
        if (x - 1 < 0) {
          _grid(y)(x) = States.WATER
        } else if (_grid(y)(x - 1) != States.LOG) {
          _grid(y)(x - 1) = States.LOG
          _grid(y)(x) = States.WATER
        }
      }
    }

    if (_grid(y)(width - 1) == States.WATER) {
      val isHeadAtEdge = width > 1 && _grid(y)(width - 2) == States.LOG && (width <= 2 || _grid(y)(width - 3) != States.LOG)
      if (isHeadAtEdge) {
        _grid(y)(width - 1) = States.LOG
      } else if ((width <= 1 || _grid(y)(width - 2) != States.LOG) && random.nextInt(100) < GameConfig.LOG_SPAWN_CHANCE) {
        _grid(y)(width - 1) = States.LOG
      }
    }
  }

  def collisionSystemReact(player: Player): Boolean = {
    val (x, y) = player.getPos()
    val currentState = getCell(x, y)
    currentState == States.CAR || currentState == States.WATER
  }
}