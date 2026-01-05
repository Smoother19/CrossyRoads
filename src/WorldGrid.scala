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

      // Determine the height of the line (ROAD/WATER = 2, Others = 1)
      val height: Int = terrainType match{
        case States.ROAD | States.WATER => 2
        case States.TREE | States.GRASS => 1
      }

      // Check if we have enough place to place this block

      // If we are at the last line and try to add a route (size 2)
      var safeType = terrainType
      if (row + height > length){
        safeType = States.GRASS
      }

      // force the block to have a height of 1
      var safeHeight = height
      if (row + height > length) {
        safeHeight = 1
      }

      // Lines filling
      // ... (ton code précédent avec safeHeight, safeType ...)

      // Remplissage des lignes
      for (i <- 0 until safeHeight) {
        for (col <- 0 until width) {

          // LOGIQUE D'APPARITION DES VOITURES
          // Si le terrain est une ROUTE, on a une chance de mettre une voiture
          if (safeType == States.ROAD && random.nextInt(100) < 15) { // 15% de chance
            gridToFill(row + i)(col) = States.CAR
          } else {
            // Sinon, on met le terrain normal (Herbe, Eau, ou Route vide)
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
      grid(y)(x) // Warning : we first access the line (y) and then the column (x)
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
      // LOGIQUE D'APPARITION (La même qu'en haut)
      if (terrainType == States.ROAD && random.nextInt(100) < 15) {
        newRow(col) = States.CAR
      } else {
        newRow(col) = terrainType
      }
    }

    grid(0) = newRow
  }

  /**
   * Déplace toutes les voitures vers la droite.
   */
  def moveCars(): Unit = {
    val random = new Random()

    // On parcourt chaque ligne
    for (y <- 0 until length) {

      // OPTIMISATION : On ne travaille que si la ligne contient une route ou une voiture
      // (Pour faire simple, on applique ça à toutes les lignes, le 'if' gère le reste)

      // 1. DEPLACEMENT (On boucle À L'ENVERS : de droite à gauche)
      for (x <- width - 1 to 0 by -1) {

        if (grid(y)(x) == States.CAR) {
          // On vérifie si on peut avancer (si la case devant est libre ou si c'est la fin de l'écran)

          // Cas A : La voiture sort de l'écran (à droite)
          if (x + 1 >= width) {
            grid(y)(x) = States.ROAD // Elle disparait (devient une route vide)
          }
          // Cas B : La voiture avance (si la case devant n'est pas déjà occupée par une voiture)
          else if (grid(y)(x + 1) != States.CAR) {
            grid(y)(x + 1) = States.CAR // Elle arrive ici
            grid(y)(x) = States.ROAD    // Elle quitte sa case précédente
          }
        }
      }

      // 2. APPARITION (SPAWN)
      // Si c'est une route et que la première case à gauche (0) est vide...
      if (grid(y)(0) == States.ROAD) {
        // ... on a une petite chance (5%) de faire entrer une nouvelle voiture
        if (random.nextInt(100) < 5) {
          grid(y)(0) = States.CAR
        }
      }
    }
  }
}
