package objects

import scalafx.scene.shape.Shape
import utils.Vector2D

abstract class GameObject(val id: String, var xPos: Double, var yPos: Double) {

  // xPos and yPos are object's left corner coordinates

  val shape: Shape

  var dead = false

  def location: Vector2D // object's center coordinates

  def update(): Unit

}
