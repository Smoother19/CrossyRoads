import scala.util.Random

class WorldGrid(length: Int, width: Int) {

  var grid: Array[Array[Int]] = Array.ofDim(length, width)

  // 1 = Vers la Droite, -1 = Vers la Gauche
  var rowDirections: Array[Int] = Array.fill(length)(1)

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

      val height: Int = terrainType match {
        case States.ROAD | States.WATER => 2
        case States.TREE | States.GRASS => 1
      }

      var safeType = terrainType
      if (row + height > length) safeType = States.GRASS

      var safeHeight = if (row + height > length) 1 else height

      // On définit une direction aléatoire pour ce bloc
      var dir = if (random.nextBoolean()) 1 else -1

      for (i <- 0 until safeHeight) {

        // ASTUCE : Si on a un bloc d'eau de 2 lignes, on inverse la direction de la 2ème ligne
        // pour créer un effet de slalom (Gauche / Droite)
        if (safeType == States.WATER && i > 0) {
          dir = -dir
        }
        rowDirections(row + i) = dir

        // Utilisation de while pour gérer les paires de bûches
        var col = 0
        while (col < width) {
          if (safeType == States.ROAD && random.nextInt(100) < 25) {
            gridToFill(row + i)(col) = States.CAR
            col += 1
          }
          else if (safeType == States.WATER && random.nextInt(100) < 25) {
            // On pose une bûche
            gridToFill(row + i)(col) = States.LOG
            // On essaie de poser la suite immédiatement (taille 2)
            if (col + 1 < width) {
              gridToFill(row + i)(col + 1) = States.LOG
              col += 2
            } else {
              col += 1
            }
          } else {
            gridToFill(row + i)(col) = safeType
            col += 1
          }
        }
      }
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
    // 1. On décale la grille ET les directions
    for (i <- length - 1 until 0 by -1) {
      grid(i) = grid(i - 1)
      rowDirections(i) = rowDirections(i - 1) // La direction suit la ligne
    }

    val newRow = new Array[Int](width)
    val random = new Random()
    var terrainType = States.GRASS

    val typeBelow = grid(1)(0)
    val typeTwoBelow = if (length > 2) grid(2)(0) else -1

    if (typeBelow == States.ROAD || typeBelow == States.WATER) {
      if (typeBelow != typeTwoBelow) terrainType = typeBelow
      else terrainType = random.nextInt(4)
    } else {
      terrainType = random.nextInt(4)
    }

    // 2. On définit la direction de la nouvelle ligne (index 0)
    // Si la nouvelle ligne est de l'eau et celle du dessous aussi, on inverse le sens !
    if (terrainType == States.WATER && typeBelow == States.WATER) {
      rowDirections(0) = -rowDirections(1)
    } else {
      rowDirections(0) = if (random.nextBoolean()) 1 else -1
    }

    // 3. Remplissage avec gestion des bûches doubles
    var col = 0
    while (col < width) {
      if (terrainType == States.ROAD && random.nextInt(100) < 15) {
        newRow(col) = States.CAR
        col += 1
      }
      else if (terrainType == States.WATER && random.nextInt(100) < 25) {
        newRow(col) = States.LOG
        if (col + 1 < width) {
          newRow(col + 1) = States.LOG
          col += 2
        } else {
          col += 1
        }
      }
      else {
        newRow(col) = terrainType
        col += 1
      }
    }
    grid(0) = newRow
  }

  def moveCars(): Unit = {
    val random = new Random()
    for (y <- 0 until length) {
      for (x <- width - 1 to 0 by -1) {
        if (grid(y)(x) == States.CAR) {
          if (x + 1 >= width) grid(y)(x) = States.ROAD
          else if (grid(y)(x + 1) != States.CAR) {
            grid(y)(x + 1) = States.CAR
            grid(y)(x) = States.ROAD
          }
        }
      }
      if (grid(y)(0) == States.ROAD) {
        if (random.nextInt(100) < 15) grid(y)(0) = States.CAR
      }
    }
  }

  def moveLogs(): Unit = {
    val random = new Random()

    for (y <- 0 until length) {

      // ON UTILISE LA DIRECTION STOCKÉE (et non y % 2)
      if (rowDirections(y) == 1) {
        // --- Vers la DROITE ---
        for (x <- width - 1 to 0 by -1) {
          if (grid(y)(x) == States.LOG) {
            if (x + 1 >= width) grid(y)(x) = States.WATER
            else if (grid(y)(x + 1) != States.LOG) {
              grid(y)(x + 1) = States.LOG
              grid(y)(x) = States.WATER
            }
          }
        }
        // Apparition Droite (force taille 2)
        if (grid(y)(0) == States.WATER) {
          val isHeadAtOne = width > 1 && grid(y)(1) == States.LOG && (width <= 2 || grid(y)(2) != States.LOG)
          if (isHeadAtOne) grid(y)(0) = States.LOG
          else if ((width <= 1 || grid(y)(1) != States.LOG) && random.nextInt(100) < 25) grid(y)(0) = States.LOG
        }

      } else {
        // --- Vers la GAUCHE ---
        for (x <- 0 until width) {
          if (grid(y)(x) == States.LOG) {
            if (x - 1 < 0) grid(y)(x) = States.WATER
            else if (grid(y)(x - 1) != States.LOG) {
              grid(y)(x - 1) = States.LOG
              grid(y)(x) = States.WATER
            }
          }
        }
        // Apparition Gauche (force taille 2)
        if (grid(y)(width - 1) == States.WATER) {
          val isHeadAtEdge = width > 1 && grid(y)(width - 2) == States.LOG && (width <= 2 || grid(y)(width - 3) != States.LOG)
          if (isHeadAtEdge) grid(y)(width - 1) = States.LOG
          else if ((width <= 1 || grid(y)(width - 2) != States.LOG) && random.nextInt(100) < 25) grid(y)(width - 1) = States.LOG
        }
      }
    }
  }

  def collisionSystemPredict(nextPos: (Int, Int)): Boolean = {
    val nextPoseState = getCell(nextPos._1, nextPos._2)
    nextPoseState == States.CAR
  }

  def collisionSystemReact(player: Player): Boolean = {
    val (x, y) = player.getPos()
    val currentState = getCell(x, y)
    currentState == States.WATER || currentState == States.CAR
  }
}