package game

import objects.{Enemy, GameObject, GreyTower, Tower, VioletTower}
import path.PathTile
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.input.{KeyCode, KeyEvent, MouseButton, MouseEvent}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, BlueViolet, Grey, LightGreen, OrangeRed}
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.Text
import scalafx.scene.{Node, Scene}
import utils.{CollisionEngine, FileManager, Spawner, Ticker}

import scala.collection.mutable.Buffer

// Starting point for our ScalaFX application.
object Game extends JFXApp {

  var playerHealth = 100
  var playerMoney = 500

  var gameLevel = (Buffer[Int](), Buffer[Boolean]())
  var levelNum = 0
  val lastLevel = 4

  var gameObjects = Buffer[GameObject]()

  var gamePaused = false

  var newTower: Option[Tower] = None
  var towerIsOnVacantGrass = false

  var gameMap = ("", Buffer[PathTile]())
  var enemyPath = gameMap._2

  val mainMenu = new MainMenu {
    playButton.onAction = (event) => toGame()
    mapSelect.onAction = (event) => toMapSelect()
  }

  val mapMenu = new MapMenu {
    map1.onAction = (event) => loadMap("src/main/resources/map1.txt")
    map2.onAction = (event) => loadMap("src/main/resources/map2.txt")
    map3.onAction = (event) => loadMap("src/main/resources/map3.txt")
    map4.onAction = (event) => loadMap("src/main/resources/map4.txt")
  }

  def screenWidth = 1366

  def screenHeight = 768

  def maxTowerCoordinateX = screenWidth - Shop.boxWidth - Tower.size

  def maxTowerCoordinateY = screenHeight - Tower.size

  def maxPathCoordinateX = screenWidth - Shop.boxWidth - PathTile.pathSize

  def maxPathCoordinateY = screenHeight - PathTile.pathSize

  def minPathSpawnOrEndCoordinate = 0 - PathTile.pathSize

  def maxPathSpawnOrEndCoordinateX = screenWidth - Shop.boxWidth

  def maxPathSpawnOrEndCoordinateY = screenHeight

  def maxProjectileCoordinateX(radius: Double) = screenWidth - Shop.boxWidth - radius * 2

  def maxProjectileCoordinateY(radius: Double) = screenHeight - radius * 2

  def spawnTile = enemyPath.head

  def loadMap(source: String) = {
    gameMap = FileManager.loadMap(source)
    enemyPath = gameMap._2
    root.content = mainMenu
  }

  def toGame() = {
    if (enemyPath.nonEmpty) ticker.start()
  }

  def toMapSelect(): Unit = {
    root.content = mapMenu
  }

  def reset() = {
    playerHealth = 100
    playerMoney = 500
    gameObjects = Buffer[GameObject]()
    gameLevel = (Buffer[Int](), Buffer[Boolean]())
    levelNum = 0
    gamePaused = false
    newTower = None
    towerIsOnVacantGrass = false
    gameMap = ("", Buffer[PathTile]())
    enemyPath = gameMap._2
    ticker.stop()
    Spawner.resetLevelInfo()
  }

  def update(): Unit = {

    // spawn new enemies
    if (Spawner.levelInProgress) Spawner.update()

    // destroy dead enemies and sold towers
    val objects = gameObjects.toVector
    for (gameObject <- objects) {
      if (gameObject.dead) {
        gameObjects.remove(gameObjects.indexOf(gameObject))
      }
    }

    // update each GameObjects game state
    gameObjects.foreach(_.update())

    // update collision
    CollisionEngine.updateCollision(gameObjects)

    // update image
    root.content = image

  }

  def image = {
    val img = Buffer[Node]()

    // Grass
    img += (new Rectangle {
      height = screenHeight
      width = screenWidth
      x = 0
      y = 0
      fill = LightGreen
    })

    val playerInfo = new Text {
      text = s"Health: ${playerHealth.toString}\nMoney: ${playerMoney.toString}"
      style = "-fx-font: normal bold 10pt sans-serif"
      x = 5
      y = 15
      fill = Black
    }

    val levelInfo = new Text {
      text = s"Level: $levelNum"
      style = "-fx-font: normal bold 10pt sans-serif"
      x = 1210
      y = 15
      fill = Black
    }

    val instructions = new Text {
      text = "Enter = Start level\nRight Click = Sell tower\nEsc = Quit to menu"
      style = "-fx-font: normal bold 10pt sans-serif"
      x = 600
      y = 15
      fill = Black
    }

    val victoryText = new Text {
      text = "You won!"
      style = "-fx-font: normal bold 100pt sans-serif"
      x = 350
      y = 400
      fill = Black
    }

    val lossText = new Text {
      text = "You Lost."
      style = "-fx-font: normal bold 100pt sans-serif"
      x = 350
      y = 400
      fill = Black
    }

    val pressEnterText = new Text {
      text = "Press Enter"
      style = "-fx-font: normal bold 10pt sans-serif"
      x = 620
      y = 460
      fill = Black
    }

    enemyPath.foreach(x => img += x.shape)
    gameObjects.foreach(x => img += x.shape)
    Shop.shapes.foreach(img += _)
    if (newTower.isDefined) {
      val tower = newTower.get
      img += new Circle {
        radius = if (tower.isInstanceOf[GreyTower]) Tower.greyViewRange else Tower.violetViewRange
        centerX = tower.shape.x.value + Tower.size / 2
        centerY = tower.shape.y.value + Tower.size / 2
        fill = Color.rgb(0, 0, 0, 0.4)
      }
      img += tower.shape
    }
    img += playerInfo
    img += levelInfo
    img += instructions
    if (gameIsLost()) img += lossText += pressEnterText else if (gameIsWon()) img += victoryText += pressEnterText

    img
  }

  // Game loop
  val ticker = new Ticker(update)

  // Objects drawn in window are children of this Scene
  var root = new Scene {
    // fill the background with black
    fill = Black

    onKeyPressed = (ke: KeyEvent) => {
      ke.code match {
        case KeyCode.Space => { // pause and continue game
          if (gamePaused) ticker.start() else ticker.stop()
          gamePaused = !gamePaused
        }
        case KeyCode.Escape => {
          reset()
          content = mainMenu
        }
        case KeyCode.Enter => {
          if (levelNum == lastLevel || gameIsLost()) {
            reset()
            content = mainMenu
          } else startNewLevel()
        }
        case _ =>
      }
    }

    onMousePressed = (me: MouseEvent) => {
      me.button match {
        case MouseButton.Primary => {
          if (newTower.isEmpty) {
            newTower = canBuy(me.x, me.y)
          } else {
            if (towerIsOnVacantGrass) {
              val tower = newTower.get
              tower.xPos = tower.shape.x.value
              tower.yPos = tower.shape.y.value
              gameObjects += tower
              tower.isBuild = true
              playerMoney -= (if (tower.isInstanceOf[GreyTower]) Tower.greyPrice else Tower.violetPrice)
              newTower = None
            } else {
              newTower = None
            }
          }
        }
        case MouseButton.Secondary => {
          val tower = mouseOverlapsWithTower(me.x, me.y)
          if (tower.isDefined) {
            if (tower.get.isInstanceOf[GreyTower]) playerMoney += Tower.greyPrice / 2 else playerMoney += Tower.violetPrice / 2
            tower.get.dead = true
          }
        }
        case _ =>
      }
    }

    onMouseMoved = (me: MouseEvent) => {
      if (newTower.isDefined) {
        val box = newTower.get.shape
        box.x = me.x - 0.5 * Tower.size
        box.y = me.y - 0.5 * Tower.size
        towerIsOnVacantGrass = !newTowerOverlaps(box, gameObjects, enemyPath) && newTowerIsOnGrass(box)
        box.fill = if (!towerIsOnVacantGrass) OrangeRed else if (newTower.get.isInstanceOf[GreyTower]) Grey else BlueViolet
      }
    }

    content = mainMenu

  }

  stage = new JFXApp.PrimaryStage {
    title.value = "Epic Tower Defense"
    width = screenWidth
    height = screenHeight
    scene = root
    resizable.value = false
  }

  def gameIsLost() = {
    if (playerHealth <= 0) {
      ticker.stop()
      true
    } else false
  }

  def gameIsWon() = {
    if (levelNum == lastLevel && !Spawner.levelInProgress && !gameObjects.exists(_.isInstanceOf[Enemy])) {
      ticker.stop()
      true
    } else false
  }

  def mouseOverlapsWithTower(x: Double, y: Double): Option[Tower] = {
    for (t <- gameObjects.filter(_.isInstanceOf[Tower]).asInstanceOf[Buffer[Tower]]) {
      val xOverlaps = x >= t.xPos && x <= t.xPos + Tower.size
      val yOverlaps = y >= t.yPos && y <= t.yPos + Tower.size
      if (xOverlaps && yOverlaps) return Some(t)
    }
    None
  }

  def startNewLevel() = {
    if (!Spawner.levelInProgress && !gameObjects.exists(_.isInstanceOf[Enemy])) {
      if (levelNum == 0) {
        gameLevel = FileManager.loadLevel("src/main/resources/level1.txt")
      } else if (levelNum == 1) {
        gameLevel = FileManager.loadLevel("src/main/resources/level2.txt")
      } else if (levelNum == 2) {
        gameLevel = FileManager.loadLevel("src/main/resources/level3.txt")
      } else if (levelNum == 3) {
        gameLevel = FileManager.loadLevel("src/main/resources/level4.txt")
      }
      levelNum += 1
      Spawner.resetLevelInfo()
    }
  }

  def addGameObject(obj: GameObject): Unit = {
    gameObjects += obj
  }

  def newTowerOverlaps(box: Rectangle, objs: Buffer[GameObject], path: Buffer[PathTile]): Boolean = {
    val rectangles = (objs.filter(obj => obj.isInstanceOf[Tower]).map(_.shape) ++ path.map(_.shape)).asInstanceOf[Buffer[Rectangle]]
    for (rect <- rectangles) {
      if (rectanglesOverlap(box, rect)) return true
    }
    false
  }

  def newTowerIsOnGrass(tower: Rectangle) = {
    tower.x() >= 0 && tower.x() <= maxTowerCoordinateX && tower.y() >= 0 && tower.y() <= maxTowerCoordinateY
  }

  def rectanglesOverlap(first: Rectangle, second: Rectangle) = {
    val fx = first.x()
    val sx = second.x()
    val fy = first.y()
    val sy = second.y()
    val xOverlaps = (fx >= sx && fx <= sx + second.width()) || (sx >= fx && sx <= fx + first.width())
    val yOverlaps = (fy >= sy && fy <= sy + second.height()) || (sy >= fy && sy <= fy + first.height())
    xOverlaps && yOverlaps
  }

  def canBuy(x: Double, y: Double): Option[Tower] = {
    val xOverlapsViolet = x >= Shop.violetTower.x() && x <= (Shop.violetTower.x() + Shop.violetTower.width())
    val yOverlapsViolet = y >= Shop.violetTower.y() && y <= (Shop.violetTower.y() + Shop.violetTower.height())
    val xOverlapsGrey = x >= Shop.greyTower.x() && x <= (Shop.greyTower.x() + Shop.greyTower.width())
    val yOverlapsGrey = y >= Shop.greyTower.y() && y <= (Shop.greyTower.y() + Shop.greyTower.height())
    if (xOverlapsViolet && yOverlapsViolet && playerMoney >= Tower.violetPrice) {
      Some(new VioletTower("vt", x - 0.5 * Tower.size, y - 0.5 * Tower.size))
    } else if (xOverlapsGrey && yOverlapsGrey && playerMoney >= Tower.greyPrice) {
      Some(new GreyTower("gt", x - 0.5 * Tower.size, y - 0.5 * Tower.size))
    } else {
      None
    }
  }

}
