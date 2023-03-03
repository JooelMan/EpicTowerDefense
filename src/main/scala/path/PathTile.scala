package path

import scalafx.scene.paint.Color.SandyBrown
import scalafx.scene.shape.Rectangle
import utils.Vector2D

class PathTile(val xPos: Double, val yPos: Double, val pathWidth: Double = PathTile.pathSize, val pathHeight: Double = PathTile.pathSize) {

  val centerX = xPos + pathWidth / 2
  val centerY = yPos + pathHeight / 2
  var nextPathTile: Option[PathTile] = None

  val shape = new Rectangle {
    width = pathWidth
    height = pathHeight
    x = xPos
    y = yPos
    fill = SandyBrown
  }

  def location = Vector2D(centerX, centerY)

}

object PathTile {
  val pathSize = 48
}