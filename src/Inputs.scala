import java.awt.event.{KeyAdapter, KeyEvent}


case class InputState(up: Boolean, down: Boolean, left: Boolean, right: Boolean)

object Inputs extends KeyAdapter {

  var isUpPressed = false
  var isDownPressed = false
  var isLeftPressed = false
  var isRightPressed = false

  override def keyPressed(e: KeyEvent): Unit = {
    val key = e.getKeyCode

    if (key == KeyEvent.VK_W) {
      isUpPressed = true
    }
    else if (key == KeyEvent.VK_S) {
      isDownPressed = true
    }
    else if (key == KeyEvent.VK_A) {
      isLeftPressed = true
    }
    else if (key == KeyEvent.VK_D) {
      isRightPressed = true
    }
  }

  override def keyReleased(e: KeyEvent): Unit = {
    val key = e.getKeyCode

    // On remet la variable à false
    if (key == KeyEvent.VK_W) {
      isUpPressed = false
    }
    else if (key == KeyEvent.VK_S) {
      isDownPressed = false
    }
    else if (key == KeyEvent.VK_A) {
      isLeftPressed = false
    }
    else if (key == KeyEvent.VK_D) {
      isRightPressed = false
    }
  }

  def getInputs(): InputState = {
    return InputState(isUpPressed, isDownPressed, isLeftPressed, isRightPressed)
  }
}