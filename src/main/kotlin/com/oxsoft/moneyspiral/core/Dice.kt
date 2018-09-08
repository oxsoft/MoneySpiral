package com.oxsoft.moneyspiral.core

object Dice {
    fun roll(): Int = (Math.random() * 6).toInt() + 1
}
