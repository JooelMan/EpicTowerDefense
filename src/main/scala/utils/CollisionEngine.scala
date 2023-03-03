package utils

import objects.{Enemy, GameObject, Projectile}

import scala.collection.mutable.Buffer

object CollisionEngine {

  def updateCollision(objs: Buffer[GameObject]) = {
    val enemies = objs.filter(_.isInstanceOf[Enemy])
    val projectiles = objs.filter(_.isInstanceOf[Projectile])
    for {
      p <- projectiles.asInstanceOf[Buffer[Projectile]]
      e <- enemies.asInstanceOf[Buffer[Enemy]]
    } {
      var distance = (p.location - e.location).length
      if (distance < p.projectileRadius + e.enemyRadius) {
        p.damage(e)
      }
    }
  }

}
