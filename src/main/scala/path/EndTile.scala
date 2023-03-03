package path

class EndTile(x: Double, y: Double) extends PathTile(x, y) {

    nextPathTile = Some(this)

}
