package utils

import javafx.animation.AnimationTimer

//This class calls function given as a parameter repeatedly.
class Ticker(function: () => Unit) extends AnimationTimer {

    //Override from animation timer
    override def handle(now: Long): Unit = {function()}

}