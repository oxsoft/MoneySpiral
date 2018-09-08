package com.oxsoft.moneyspiral.core

enum class Orientation(
    val a: Int, val b: Int,
    val c: Int, val d: Int
) {
    R0(
        1, 0,
        0, 1
    ),
    R90(
        0, -1,
        1, 0
    ),
    R180(
        -1, 0,
        0, -1
    ),
    R270(
        0, 1,
        -1, 0
    )
}
