package objects

import scalafx.scene.shape.Circle
import utils.Vector2D

abstract class Projectile(x: Double, y: Double, target: Vector2D) extends GameObject("", x, y) {

  val projectileRadius: Double

  val shape: Circle

  var speed: Vector2D

  val startLocation = Vector2D(x, y)

  def location = Vector2D(xPos, yPos)

  def update(): Unit

  def damage(e: Enemy): Unit

}