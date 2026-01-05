import java.awt.event.{KeyAdapter, KeyEvent}

case class InputState(up: Boolean, down: Boolean, left: Boolean, right: Boolean, enter: Boolean)

object Inputs extends KeyAdapter {

  var isUpPressed = false
  var isDownPressed = false
  var isLeftPressed = false
  var isRightPressed = false
  var isEnterPressed = false

  override def keyPressed(e: KeyEvent): Unit = {
    val key = e.getKeyCode

    key match{
      case KeyEvent.VK_W => isUpPressed = true
      case KeyEvent.VK_S => isDownPressed = true
      case KeyEvent.VK_A => isLeftPressed = true
      case KeyEvent.VK_D => isRightPressed = true
      case KeyEvent.VK_ENTER => isEnterPressed = true
    }

  }


  override def keyReleased(e: KeyEvent): Unit = {
    super.keyReleased(e)
  }

  def getInputs(): InputState = {

    val state = InputState(isUpPressed, isDownPressed, isLeftPressed, isRightPressed,isEnterPressed)

    isUpPressed = false
    isDownPressed = false
    isLeftPressed = false
    isRightPressed = false
    isEnterPressed = false

    return state
  }
}