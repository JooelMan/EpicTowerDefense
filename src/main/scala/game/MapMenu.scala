package game

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font

class MapMenu extends VBox {

    val vbox = new VBox
    vbox.setMaxSize(1366, 768)

    this.spacing = 10
    this.alignment = Pos.TopCenter

    val header = new Label("Epic Tower Defense")
    header.padding = Insets(50, 25, 10, 25)
    header.font = Font(150)

    val buttonContainer = new VBox
    buttonContainer.padding = Insets(25, 100, 25, 100)
    buttonContainer.spacing = 20
    buttonContainer.alignment = Pos.TopCenter

    val map1 = new Button {
        text = "1"
        maxWidth = 300
        prefHeight = 50
    }

    val map2 = new Button {
        text = "2"
        maxWidth = 300
        prefHeight = 50
    }

    val map3 = new Button {
        text = "3"
        maxWidth = 300
        prefHeight = 50
    }

    val map4 = new Button {
        text = "4"
        maxWidth = 300
        prefHeight = 50
    }

    buttonContainer.children = Array(map1, map2, map3, map4)

    this.children = Array(header, buttonContainer)

}
