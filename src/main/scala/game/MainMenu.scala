package game

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font

class MainMenu extends VBox {

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

    val playButton = new Button {
        text = "Play Game"
        maxWidth = 300
        prefHeight = 50
        onAction = (event) => println("Start game")
    }

    val mapSelect = new Button {
        text = "Select Map"
        maxWidth = 300
        prefHeight = 50
    }

    buttonContainer.children = Array(playButton, mapSelect)

    this.children = Array(header, buttonContainer)

}
