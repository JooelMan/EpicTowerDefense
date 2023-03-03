package objects

import game.Game
import scalafx.scene.paint.Color.Yellow
import scalafx.scene.shape.Circle
import utils.Vector2D

class NormalProjectile(x: Double, y: Double, target: Vector2D) extends Projectile(x, y, target) {

  val projectileRadius = 6

  var speed = target - location
  while (speed.length > 20) {
    speed = speed / 1.4
  }

  val shape = new Circle {
    radius = projectileRadius
    centerX = x
    centerY = y
    fill = Yellow
  }

  def update() = {
    if (location.x == target.x && location.y == target.y
          || (location - startLocation).length > Tower.violetShootRange
          || xPos > Game.maxProjectileCoordinateX(projectileRadius) || yPos > Game.maxProjectileCoordinateY(projectileRadius)
          || xPos < 0 || yPos < 0) {
      dead = true
    }
    if (!dead && (xPos != target.x || yPos != target.y)) {
      shape.centerX = shape.centerX() + speed.x
      shape.centerY = shape.centerY() + speed.y
      xPos += speed.x
      yPos += speed.y
    }
  }

  def damage(e: Enemy) = {
    e.health -= 50
    this.dead = true
  }

}

