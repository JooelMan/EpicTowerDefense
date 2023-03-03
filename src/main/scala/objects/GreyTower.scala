package objects

import game.Game
import scalafx.scene.paint.Color.Grey
import scalafx.scene.shape.Rectangle
import utils.Vector2D

import scala.collection.mutable

class GreyTower(id: String, posX: Double, posY: Double) extends Tower(id, posX, posY) {

  var reloadingTime = 0

  val shape = new Rectangle {
    height = Tower.size
    width = Tower.size
    x = xPos
    y = yPos
    fill = Grey
  }

  def update() = {
    if (isBuild) {

      if (reloadingTime == Tower.greyReloadTime) {
        if (enemyOnRange) {
          // shoot
          val pUp = new SmallProjectile(location.x, location.y, location + Vector2D(0, -100))
          val pUpRight = new SmallProjectile(location.x, location.y, location + Vector2D(71, -71))
          val pRight = new SmallProjectile(location.x, location.y, location + Vector2D(100, 0))
          val pDownRight = new SmallProjectile(location.x, location.y, location + Vector2D(71, 71))
          val pDown = new SmallProjectile(location.x, location.y, location + Vector2D(0, 100))
          val pDownLeft = new SmallProjectile(location.x, location.y, location + Vector2D(-71, 71))
          val pLeft = new SmallProjectile(location.x, location.y, location + Vector2D(-100, 0))
          val pUpLeft = new SmallProjectile(location.x, location.y, location + Vector2D(-71, -71))
          Game.gameObjects += pUp += pUpRight += pRight += pDownRight += pDown += pDownLeft += pLeft += pUpLeft
          reloadingTime = 0 // reset reloading time
        }
      } else {
        reloadingTime += 1
      }

    } else {

    }
  }

  def enemyOnRange() = {
    val enemies = Game.gameObjects.filter(_.isInstanceOf[Enemy]).asInstanceOf[mutable.Buffer[Enemy]]
    val enemiesOnRange = enemies.filter(e => (e.location - this.location).length - e.enemyRadius <= Tower.greyViewRange)
    if (enemiesOnRange.nonEmpty) true else false
  }

}


