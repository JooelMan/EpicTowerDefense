package game

import objects.Tower
import scalafx.scene.Node
import scalafx.scene.paint.Color.{Black, BlueViolet, Grey, SaddleBrown}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

object Shop {

  def boxWidth = 100

  def shapes = Vector[Node](box, violetTower, greyTower, violetPriceText, greyPriceText)

  val box = new Rectangle {
    height = Game.screenHeight
    width = boxWidth
    x = Game.screenWidth - boxWidth
    y = 0
    fill = SaddleBrown
  }

  val violetTower = new Rectangle {
    height = Tower.size
    width = Tower.size
    x = box.x.value + (boxWidth - Tower.size) / 2
    y = 25
    fill = BlueViolet
  }

  val greyTower = new Rectangle {
    height = Tower.size
    width = Tower.size
    x = box.x.value + (boxWidth - Tower.size) / 2
    y = violetTower.y() + Tower.size + 25
    fill = Grey
  }

  val violetPriceText = new Text {
    text = Tower.violetPrice.toString + " $"
    style = "-fx-font: normal bold 10pt sans-serif"
    x = violetTower.x()
    y = violetTower.y() + Tower.size + 5
    fill = Black
  }

  val greyPriceText = new Text {
    text = Tower.greyPrice.toString + " $"
    style = "-fx-font: normal bold 10pt sans-serif"
    x = greyTower.x()
    y = greyTower.y() + Tower.size + 5
    fill = Black
  }


}
