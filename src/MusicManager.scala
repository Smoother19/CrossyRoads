import javax.sound.sampled.{AudioSystem, Clip, DataLine, FloatControl}

class MusicManager() {

  var clip: Clip = _

  def playSound(filePath: String): Unit = {
    stopMusic()
    try {
      val url = getClass.getResource(filePath)
      if (url == null) {
        println("Fichier son introuvable !")
        return
      }

      val audioInputStream = AudioSystem.getAudioInputStream(url)
      clip = AudioSystem.getLine(new DataLine.Info(classOf[Clip], audioInputStream.getFormat)).asInstanceOf[Clip]

      clip.open(audioInputStream)
      clip.loop(Clip.LOOP_CONTINUOUSLY)

      val volume = clip.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]
      volume.setValue(0.0f)

      clip.start()

    } catch {
      case e: Exception =>
        println(s"Erreur lors de la lecture du son : ${e.getMessage}")
    }
  }

  def stopMusic(): Unit = {
    if (clip != null && clip.isRunning) {
      clip.stop()
      clip.close()
    }
  }

}
