package utils

import game.Game
import objects.Enemy
import path.{EndTile, PathTile, SpawnTile}

import java.io.{BufferedReader, FileNotFoundException, FileReader, IOException}
import scala.collection.mutable.Buffer

object FileManager {

  def loadLevel(sourceFile: String) = {
    try {

      val fileReader = new FileReader(sourceFile)
      val lineReader = new BufferedReader(fileReader)

      try {
        var enemies = Buffer[Int]()
        var hasToWait = Buffer[Boolean]() // false -> spawn new enemy, true -> wait
        var first = true
        var oneLine: String = null

        while ( {
          oneLine = lineReader.readLine(); oneLine != null
        }) {
          var line = oneLine.trim.toLowerCase()
          if (line != "") {
            if (first) {
              if (line != "level") throw new CorruptedLevelFileException("Unknown file type!")
              first = false
            } else {
              while (line.length >= 2) {
                val block = line.take(2)
                handleBlock(block, enemies, hasToWait)
                line = line.drop(2)
              }
              hasToWait += true += true += true // separate waves with three wait blocks
            }
          }
        }

        // return these
        (enemies, hasToWait)

      } finally {
        fileReader.close()
        lineReader.close()
      }

    } catch {
      case notFound: FileNotFoundException => notFound.printStackTrace(); (Buffer[Int](), Buffer[Boolean]())
      case nfe: NumberFormatException => nfe.printStackTrace(); (Buffer[Int](), Buffer[Boolean]())
      case clfe: CorruptedLevelFileException => clfe.printStackTrace(); (Buffer[Int](), Buffer[Boolean]())
      case e: IOException => e.printStackTrace(); (Buffer[Int](), Buffer[Boolean]())
    }
  }

  def handleBlock(str: String, enemies: Buffer[Int], hasToWait: Buffer[Boolean]) = {

    if (str.trim.length == 2) {

      val typeNum = str(1).asDigit

      str(0) match { // enemy
        case 'e' => {
          if (Enemy.types.contains(typeNum)) { // if does not contain -> skip
            enemies += typeNum
            hasToWait += false
          }
        }
        case 'w' => (1 to typeNum).foreach(hasToWait += true)
        case _ =>
      }

    }

  }

  def loadMap(sourceFile: String): (String, Buffer[PathTile]) = {

    try {

      val fileReader = new FileReader(sourceFile)
      val lineReader = new BufferedReader(fileReader)

      try {
        // Read path coordinates from file
        var path = Buffer[PathTile]()
        var coordinates = Vector[(Double, Double)]()
        var mapNum = ""

        // Read the path spawn, end and corner pieces' coordinates from file
        var oneLine: String = null
        while ( {
          oneLine = lineReader.readLine(); oneLine != null
        }) {
          val line = oneLine.trim
          if (line.contains(",")) {
            val pointCoordinates = line.split(",")
            val x = Integer.parseInt(pointCoordinates(0).trim)
            val y = Integer.parseInt(pointCoordinates(1).trim)
            coordinates = coordinates :+ (x, y)
          } else {
            mapNum = line
          }
        }

        // Check if coordinates are valid
        checkIfCoordinatesAreValid(coordinates)

        // Create path from coordinates
        createPathFromCoordinates(path, coordinates)

        // Create path tiles in the middle of corner path tiles
        path = createMissingPathPieces(path)

        // Assign targetPathTiles for the PathTiles
        assignTargetPathTiles(path)

        // Return map number and path Buffer
        (mapNum, path)

      } finally {
        fileReader.close()
        lineReader.close()
      }

    } catch {
      case notFound: FileNotFoundException => notFound.printStackTrace(); ("", Buffer[PathTile]())
      case nfe: NumberFormatException => nfe.printStackTrace(); ("", Buffer[PathTile]()) // Handle this in some other way
      case cmfe: CorruptedMapFileException => cmfe.printStackTrace(); ("", Buffer[PathTile]())
      case e: IOException => e.printStackTrace(); ("", Buffer[PathTile]())
    }

  }

  def checkIfCoordinatesAreValid(coords: Vector[(Double, Double)]) = {
    if (coords.length < 2) {
      throw new CorruptedMapFileException("Not enough coordinates to create a path!")
    } else {
      for (i <- coords.indices) {
        if (i == coords.indices.last) {
          checkIfSpawnOrEndCoordinatesAreValid(coords(i)._1, coords(i)._2)
        } else {
          if (coords(i)._1 != coords(i + 1)._1 && coords(i)._2 != coords(i + 1)._2) throw new CorruptedMapFileException("Adjacent PathTiles have to have the same X or Y coordinate!")
          if (i == 0) {
            checkIfSpawnOrEndCoordinatesAreValid(coords(i)._1, coords(i)._2)
          } else {
            if (coords(i)._1 < 0 || coords(i)._2 < 0 || coords(i)._1 > Game.maxPathCoordinateX || coords(i)._2 > Game.maxPathCoordinateY) {
              throw new CorruptedMapFileException("path.PathTile's coordinates are not in the accepted range!")
            }
          }
        }
      }
    }
  }

  def checkIfSpawnOrEndCoordinatesAreValid(x: Double, y: Double) = {
    if ((x != Game.minPathSpawnOrEndCoordinate && x != Game.maxPathSpawnOrEndCoordinateX)
      && (y != Game.minPathSpawnOrEndCoordinate && y != Game.maxPathSpawnOrEndCoordinateY)) {
      throw new CorruptedMapFileException("path.SpawnTile or path.EndTile's x or y has to be off the map!")
    }
    if ((x == Game.minPathSpawnOrEndCoordinate || x == Game.maxPathSpawnOrEndCoordinateX) && (y < 0 || y > Game.maxPathCoordinateY))
      throw new CorruptedMapFileException("path.SpawnTile or path.EndTile's both x and y cannot be off the map!")
    if ((y == Game.minPathSpawnOrEndCoordinate || y == Game.maxPathSpawnOrEndCoordinateY) && (x < 0 || x > Game.maxPathCoordinateX))
      throw new CorruptedMapFileException("path.SpawnTile or path.EndTile's both x and y cannot be off the map!")

  }

  def createPathFromCoordinates(path: Buffer[PathTile], coordinates: Vector[(Double, Double)]) = {
    for (i <- coordinates.indices) {
      if (i == 0) {
        path += new SpawnTile(coordinates(i)._1, coordinates(i)._2)
      } else if (i == coordinates.length - 1) {
        path += new EndTile(coordinates(i)._1, coordinates(i)._2)
      } else {
        path += new PathTile(coordinates(i)._1, coordinates(i)._2)
      }
    }
  }

  def createMissingPathPieces(pathCore: Buffer[PathTile]): Buffer[PathTile] = {
    var path = pathCore.clone()
    var j = 1
    for (i <- 0 until (pathCore.length - 1)) {
      val t = pathCore(i) // this path tile
      val n = pathCore(i + 1) // next path tile
      val posX = if (t.xPos > n.xPos) n.xPos else t.xPos
      val posY = if (t.yPos > n.yPos) n.yPos else t.yPos
      val width = if (t.xPos - n.xPos != 0) scala.math.abs(t.xPos - n.xPos) else t.pathWidth
      val height = if (t.yPos - n.yPos != 0) scala.math.abs(t.yPos - n.yPos) else t.pathHeight
      path = (path.take(i + j) :+ new PathTile(posX, posY, width, height)) ++ path.drop(i + j)
      j += 1
    }
    // return new path
    path
  }

  def assignTargetPathTiles(path: Buffer[PathTile]) = {
    for (i <- path.indices) {
      if (i == 0) {
        path(i).nextPathTile = Some(path(i + 1))
      } else if (i < path.length - 1) {
        path(i).nextPathTile = Some(path(i + 1))
      }
    }
  }

}
