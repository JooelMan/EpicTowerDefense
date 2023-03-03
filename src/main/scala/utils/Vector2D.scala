package utils

case class Vector2D(val x: Double, val y: Double) {

  def length = Math.sqrt(x * x + y * y)

  def +(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)

  def -(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)

  def *(d: Double): Vector2D = Vector2D(x * d, y * d)

  def /(d: Double): Vector2D = this * (1 / d)

}
