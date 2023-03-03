package objects

import scalafx.scene.shape.Rectangle
import utils.Vector2D

abstract class Tower(id: String, posX: Double, posY: Double) extends GameObject(id, posX, posY) {

  var isBuild = false

  val shape: Rectangle

  def location = Vector2D(xPos + Tower.size / 2, yPos + Tower.size / 2)

  def update(): Unit

}

object Tower {
  val size = 44
  val violetPrice = 150
  val violetViewRange = 300
  val violetShootRange = 330
  val violetReloadTime = 70
  val greyPrice = 100
  val greyViewRange = 100
  val greyShootRange = 110
  val greyReloadTime = 50
}
