package objects

import path.PathTile
import scalafx.scene.paint.Color.Red
import scalafx.scene.shape.Circle

class RedEnemy(id: String, x: Double, y: Double, targetPathTile: Option[PathTile]) extends Enemy(id, x, y, targetPathTile) {

  var health = 100

  val speed = 2

  val enemyRadius = 20

  val shape = new Circle {
    radius = enemyRadius
    centerX = x
    centerY = y
    fill = Red
  }

}


