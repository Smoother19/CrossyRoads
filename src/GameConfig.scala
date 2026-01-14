import hevs.graphics.utils.GraphicsBitmap

object GameConfig {
  val COLUMNS: Int = 15
  val ROWS: Int = 20
  val CELL_SIZE: Int = 50
  val HALF_CELL: Int = CELL_SIZE / 2

  val WINDOW_WIDTH = COLUMNS * CELL_SIZE
  val WINDOW_HEIGHT: Int = ROWS * CELL_SIZE

  val TARGET_FPS: Int = 30
  val SCROLL_INTERVAL: Int = 50


  val CAR_SPAWN_CHANCE: Int = 25
  val CAR_RESPAWN_CHANCE: Int = 15
  val CAR_MOVE_INTERVAL: Int = 10

  val LOG_SPAWN_CHANCE: Int = 25
  val LOG_MOVE_INTERVAL: Int = 20

  val IMG_GRASS = new GraphicsBitmap("/res/img/grass.jpg")
  val IMG_ROAD = new GraphicsBitmap("/res/img/road.png")
  val IMG_WATER = new GraphicsBitmap("/res/img/water.jpg")
  val IMG_CAR = new GraphicsBitmap("/res/img/car.png")
  val IMG_LOG = new GraphicsBitmap("/res/img/log.png")
  val IMG_CHICKEN = new GraphicsBitmap("/res/img/imageChicken.png")
  val IMG_MENU = new GraphicsBitmap("/res/img/menu.png")

  val MUSIC_BG = "res/sound/gameSound.wav"
  val MUSIC_LOSE = "res/sound/losingSound.wav"
}
