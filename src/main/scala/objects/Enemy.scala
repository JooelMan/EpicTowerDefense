package objects

import game.Game
import path.{EndTile, PathTile}
import scalafx.scene.shape.Circle
import utils.Vector2D

abstract class Enemy(id: String, x: Double, y: Double, targetPathTile: Option[PathTile]) extends GameObject(id, x - 20, y - 20) {

  var targetTile = targetPathTile

  var health: Int

  val speed: Double

  val enemyRadius: Double

  val shape: Circle

  def location = Vector2D(shape.centerX(), shape.centerY())

  def update() = {

      if (health <= 0) {
        this.dead = true
        if (this.isInstanceOf[BlueEnemy]) {
          Game.playerMoney += 80
        } else {
          Game.playerMoney += 40
        }
      }
      val distance = (this.location - targetTile.get.location).length
      if (distance <= speed) {
        if (this.targetTile.get.isInstanceOf[EndTile]) {
          this.dead = true
          if (this.isInstanceOf[BlueEnemy]) {
            Game.playerHealth -= 8
          } else {
            Game.playerHealth -= 4
          }
        } else {
          this.targetTile = this.targetTile.get.nextPathTile
        }
      }
      if (!dead) {
        val loc = this.location
        val s = scala.math.min(speed, (loc - targetTile.get.location).length)
        shape.centerX = loc.x + { if (targetTile.get.centerX > loc.x) s else if (targetTile.get.centerX < loc.x) -s else 0 }
        shape.centerY = loc.y + { if (targetTile.get.centerY > shape.centerY.value) s else if (targetTile.get.centerY < shape.centerY.value) -s else 0 }
      }

  }

}

object Enemy {
  val types = Vector(1, 2)

  def apply(enemyType: Int, x: Double, y: Double, targetPathTile: Option[PathTile]): Enemy = {
    if (enemyType == 1) {
      new RedEnemy(enemyType.toString, x, y, targetPathTile)
    } else {
      new BlueEnemy(enemyType.toString, x, y, targetPathTile)
    }
  }
}
