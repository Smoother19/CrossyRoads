import java.awt.event.{KeyAdapter, KeyEvent}

case class InputState(up: Boolean, down: Boolean, left: Boolean, right: Boolean)

object Inputs extends KeyAdapter {

  var isUpPressed = false
  var isDownPressed = false
  var isLeftPressed = false
  var isRightPressed = false

  override def keyPressed(e: KeyEvent): Unit = {
    val key = e.getKeyCode
    // Quand on appuie, on mémorise l'intention
    key match{
      case KeyEvent.VK_W => isUpPressed = true
      case KeyEvent.VK_S => isDownPressed = true
      case KeyEvent.VK_A => isLeftPressed = true
      case KeyEvent.VK_D => isRightPressed = true
    }

    //if (key == KeyEvent.VK_W) isUpPressed = true
    //else if (key == KeyEvent.VK_S) isDownPressed = true
    //else if (key == KeyEvent.VK_A) isLeftPressed = true
    //else if (key == KeyEvent.VK_D) isRightPressed = true
  }

  // ON VIDE CETTE METHODE
  // On ne veut pas que relâcher la touche annule l'ordre avant que le jeu l'ait vu
  override def keyReleased(e: KeyEvent): Unit = {
    // Ne rien faire ici
  }

  def getInputs(): InputState = {
    // 1. On crée l'état actuel pour l'envoyer au jeu
    val state = InputState(isUpPressed, isDownPressed, isLeftPressed, isRightPressed)

    // 2. IMPORTANT : On "consomme" l'input.
    // On remet tout à zéro APRÈS avoir envoyé l'info au Main.
    // Comme ça, un appui = un mouvement, et on est sûr que le Main l'a vu.
    isUpPressed = false
    isDownPressed = false
    isLeftPressed = false
    isRightPressed = false

    return state
  }
}