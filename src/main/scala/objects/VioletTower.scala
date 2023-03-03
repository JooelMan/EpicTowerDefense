package objects

import game.Game
import scalafx.scene.paint.Color.BlueViolet
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.Buffer

class VioletTower(id: String, posX: Double, posY: Double) extends Tower(id, posX, posY) {

  val shape = new Rectangle {
    height = Tower.size
    width = Tower.size
    x = xPos
    y = yPos
    fill = BlueViolet
  }

  var reloadingTime = 0
  def update() = {
    if (isBuild) {

      if (reloadingTime == Tower.violetReloadTime) {
        val target = targetEnemy()
        if (target.isDefined) {
          // shoot
          val p = new NormalProjectile(location.x, location.y, target.get.location)
          Game.gameObjects += p
          reloadingTime = 0 // reset reloading time
        }
      } else {
        reloadingTime += 1
      }

    } else {

    }
  }

  def targetEnemy() = {
    val enemies = Game.gameObjects.filter(_.isInstanceOf[Enemy]).asInstanceOf[Buffer[Enemy]]
    val enemiesOnRange = enemies.filter(e => (e.location - this.location).length - e.enemyRadius <= Tower.violetViewRange)
    enemiesOnRange.headOption
  }

}


