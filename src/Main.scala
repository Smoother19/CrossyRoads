import hevs.graphics.FunGraphics
import java.awt.Color
import java.awt.event.{KeyAdapter, KeyEvent}

object Main extends App {
  // --- CONFIGURATION ---
  val columns = 15      // Largeur de la grille (nombre de cases)
  val rows = 20         // Hauteur de la grille
  val cellSize = 30     // Taille d'une case en pixels

  // Calcul de la taille de la fenêtre en fonction de la grille
  val windowWidth = columns * cellSize
  val windowHeight = rows * cellSize

  // --- INITIALISATION ---
  val window = new FunGraphics(windowWidth, windowHeight, "Crossy Road Scala")
  window.setKeyManager(Inputs)
  val grid = new WorldGrid(rows, columns)

  // Création du joueur : on le met au milieu (columns / 2) et en bas (rows - 2)
  val player = new Player(columns / 2, rows - 2)

  // On remplit la grille initiale et on génère les premiers obstacles
  grid.fillEmptyGrid(grid.grid)
  grid.generateObstacles(grid.grid)

  // --- METHODE D'AFFICHAGE (DISPLAY) ---
  def display(): Unit = {
    // On efface tout (pour éviter les traces fantômes)
    window.clear(Color.BLACK)

    // On parcourt toute la grille
    for (y <- 0 until rows) {
      for (x <- 0 until columns) {

        // 1. Récupérer le type de terrain
        val cellType = grid.getCell(x, y)

        // 2. Choisir la couleur
        val color = cellType match {
          case States.GRASS => new Color(34, 139, 34)
          case States.ROAD  => Color.GRAY
          case States.WATER => Color.BLUE
          case States.TREE  => new Color(0, 100, 0)
          case States.CAR   => Color.RED
          case _            => Color.WHITE
        }

        // 3. Dessiner le carré
        // Attention aux coordonnées graphiques : x * taille, y * taille
        window.setColor(color)
        window.drawFillRect(x * cellSize, y * cellSize, cellSize, cellSize)

        // Petit contour noir pour bien voir la grille (optionnel)
        //window.setColor(Color.BLACK)
        //window.drawRect(x * cellSize, y * cellSize, cellSize, cellSize)
        // 1. On récupère sa position
        val (px, py) = player.getPos()

        // 2. On choisit une couleur qui ressort bien (ex: Jaune ou Rouge)
        window.setColor(Color.YELLOW)

        // 3. On le dessine (un peu plus petit que la case pour faire joli)
        // On ajoute un petit décalage (+5) pour le centrer dans la case
        window.drawFillRect(px * cellSize + 5, py * cellSize + 5, cellSize - 10, cellSize - 10)
      }
    }
  }

  // --- BOUCLE DE JEU (GAME LOOP) ---
  // C'est ce qui fait tourner le jeu en permanence
  var time = 0

  var scrollTimer = 0
  var carTimer = 0

  while (true) {
    // --- 1. GESTION DES INPUTS (Très réactif) ---
    // On vérifie les touches à chaque tour (toutes les 30ms)
    // Grâce au cooldown dans Player, ça ne sera pas trop rapide
    val inputs = Inputs.getInputs()
    if (inputs.up)    player.move(0, -1, columns, rows)
    if (inputs.down)  player.move(0, 1,  columns, rows)
    if (inputs.left)  player.move(-1, 0, columns, rows)
    if (inputs.right) player.move(1, 0,  columns, rows)

    // --- 2. LOGIQUE DU JEU (Ralentie par des timers) ---

    // SCROLL : On veut qu'il descende environ toutes les 1.5 secondes
    // 1500ms / 30ms par tour = 50 tours
    scrollTimer += 1
    if (scrollTimer >= 50) {
      grid.scrollDown()
      player.move(0, 1, columns, rows) // Le joueur descend avec le monde
      scrollTimer = 0 // Reset du timer
    }

    // VOITURES : On veut qu'elles bougent assez vite, disons toutes les 300ms
    // 300ms / 30ms = 10 tours
    carTimer += 1
    if (carTimer >= 10) {
      grid.moveCars()
      carTimer = 0
    }

    // --- 3. AFFICHAGE (Rapide) ---
    display() // On redessine 33 fois par seconde, c'est plus fluide pour l'oeil

    // --- 4. TEMPORISATION (Courte) ---
    Thread.sleep(30) // 30ms = ~33 FPS (Images par seconde)
  }
}