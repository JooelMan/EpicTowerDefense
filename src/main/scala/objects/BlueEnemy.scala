package objects

import path.PathTile
import scalafx.scene.paint.Color.DeepSkyBlue
import scalafx.scene.shape.Circle

class BlueEnemy(id: String, x: Double, y: Double, targetPathTile: Option[PathTile]) extends Enemy(id, x, y, targetPathTile) {

  var health = 200

  val speed = 3

  val enemyRadius = 20

  val shape = new Circle {
    radius = enemyRadius
    centerX = x
    centerY = y
    fill = DeepSkyBlue
  }

}


