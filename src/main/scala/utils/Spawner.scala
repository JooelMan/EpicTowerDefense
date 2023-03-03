package utils

import game.Game
import objects.Enemy

object Spawner {

  var enemies = Game.gameLevel._1
  var hasToWait = Game.gameLevel._2

  var i = 0
  var j = 0
  var spawnTimer = 0

  def update() {
    if (spawnTimer == 15) {
      if (i < hasToWait.length && j < enemies.length && !hasToWait(i)) {
        val st = Game.spawnTile
        Game.addGameObject(Enemy(enemies(j), st.location.x, st.location.y, st.nextPathTile))
        j += 1
      }
      spawnTimer = 0
      i += 1
    }
    spawnTimer += 1
  }

  def levelInProgress = enemies.length > j

  def resetLevelInfo() = {
    i = 0
    j = 0
    spawnTimer = 0
    enemies = Game.gameLevel._1
    hasToWait = Game.gameLevel._2
  }

}
