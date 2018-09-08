package com.oxsoft.moneyspiral.state

import com.oxsoft.moneyspiral.core.Orientation
import com.oxsoft.moneyspiral.core.Point
import com.oxsoft.moneyspiral.core.Side

sealed class ComputerState {
    object Unavailable : ComputerState()
    object Available : ComputerState()
    data class Working(
        val position: Point,
        val orientation: Orientation,
        val side: Side
    ) : ComputerState() {
        fun convert(point: Point) = Point(
            x = orientation.a * point.x + orientation.b * point.y + position.x,
            y = orientation.c * point.x + orientation.d * point.y * side.z + position.y
        )
    }
}
