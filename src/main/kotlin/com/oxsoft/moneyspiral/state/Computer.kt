package com.oxsoft.moneyspiral.state

import com.oxsoft.moneyspiral.core.Player
import com.oxsoft.moneyspiral.core.Point

sealed class Computer {
    abstract val id: Int
    abstract val owner: Player?
    abstract val computerState: ComputerState
    abstract val price: Int
    protected abstract val points: List<Point>

    abstract fun changeOwner(owner: Player?): Computer
    abstract fun changeComputerState(computerState: ComputerState): Computer
    fun points() = (computerState as? ComputerState.Working)?.let { working ->
        points.map { working.convert(it) }
    } ?: emptyList()

    data class Small(
        override val id: Int,
        override val owner: Player? = null,
        override val computerState: ComputerState = ComputerState.Unavailable
    ) : Computer() {
        override val price = 8
        override val points = listOf(
            Point(0, 0),
            Point(1, 0)
        )

        override fun changeOwner(owner: Player?) = copy(owner = owner)
        override fun changeComputerState(computerState: ComputerState) = copy(computerState = computerState)
    }

    sealed class Medium : Computer() {
        override val price = 12

        data class Straight(
            override val id: Int,
            override val owner: Player? = null,
            override val computerState: ComputerState = ComputerState.Unavailable
        ) : Medium() {
            override val points = listOf(
                Point(0, 0),
                Point(1, 0),
                Point(2, 0)
            )

            override fun changeOwner(owner: Player?) = copy(owner = owner)
            override fun changeComputerState(computerState: ComputerState) = copy(computerState = computerState)
        }

        data class Bended(
            override val id: Int,
            override val owner: Player? = null,
            override val computerState: ComputerState = ComputerState.Unavailable
        ) : Medium() {
            override val points = listOf(
                Point(0, 0),
                Point(1, 0),
                Point(0, 1)
            )

            override fun changeOwner(owner: Player?) = copy(owner = owner)
            override fun changeComputerState(computerState: ComputerState) = copy(computerState = computerState)
        }
    }

    sealed class Large : Computer() {
        override val price = 16

        data class Straight(
            override val id: Int,
            override val owner: Player? = null,
            override val computerState: ComputerState = ComputerState.Unavailable
        ) : Large() {
            override val points = listOf(
                Point(0, 0),
                Point(1, 0),
                Point(2, 0),
                Point(3, 0)
            )

            override fun changeOwner(owner: Player?) = copy(owner = owner)
            override fun changeComputerState(computerState: ComputerState) = copy(computerState = computerState)
        }

        data class Bended(
            override val id: Int,
            override val owner: Player? = null,
            override val computerState: ComputerState = ComputerState.Unavailable
        ) : Large() {
            override val points = listOf(
                Point(0, 0),
                Point(1, 0),
                Point(2, 0),
                Point(0, 1)
            )

            override fun changeOwner(owner: Player?) = copy(owner = owner)
            override fun changeComputerState(computerState: ComputerState) = copy(computerState = computerState)
        }
    }
}
