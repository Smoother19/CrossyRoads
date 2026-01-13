import hevs.graphics.FunGraphics

import java.awt.{Color, Font}
import javax.swing.SwingConstants

class Menu(window: FunGraphics) {
  private var selectedOption = 0
  private val options = List("Play","Exit")
  private val buttonWidth = 450
  private val buttonHeight = 100
  private val spacing = 50
  private var gameStarted = false

  def menuManager(): Unit = {
    while (!gameStarted) {
      var inputs = Inputs.getInputs()
      if (inputs.up) selectPrevious()
      if (inputs.down) selectNext()
      if (inputs.enter) {
        getSelectedOption match {
          case "Play" => gameStarted = true
          case "Exit" => System.exit(0)
        }
      }
      display()
    }
  }

  def display(): Unit = {
    // Fond
    window.drawPicture(GameConfig.WINDOW_WIDTH/2, GameConfig.WINDOW_HEIGHT, GameConfig.IMG_MENU)
    window.setColor(Color.BLACK)
    window.drawRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT)

    // Titre
    window.setColor(Color.WHITE)
    val titleText: String = "Crossy Road"
    window.drawString((GameConfig.WINDOW_WIDTH) / 2, 80, titleText, null, Font.PLAIN, 100, Color.BLACK, SwingConstants.CENTER, SwingConstants.CENTER)

    // Boutons
    options.zipWithIndex.foreach { case (option, index) =>
      val buttonX = GameConfig.WINDOW_WIDTH / 4
      val buttonY = 200 + index * (buttonHeight + spacing)

      val fontColor = if (index == selectedOption) Color.BLACK else Color.LIGHT_GRAY

      val textX = buttonX + (buttonWidth / 2) - (option.length * 10)
      val textY = buttonY + (buttonHeight / 2) + 10
      window.drawString(textX, textY, option, null, Font.PLAIN, 40, fontColor, SwingConstants.CENTER, SwingConstants.CENTER)
    }
  }

  def getSelectedOption: String = options(selectedOption)

  def selectNext(): Unit = {
    selectedOption = (selectedOption + 1) % options.length
    println("l")
  }

  def selectPrevious(): Unit = {
    selectedOption = (selectedOption - 1 + options.length) % options.length
    println("l")
  }
}
