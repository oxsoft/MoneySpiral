package com.oxsoft.moneyspiral.core

enum class Player {
    RED, YELLOW, GREEN, BLUE;

    val next get() = Player.values()[(ordinal + 1) % Player.values().size]
}
