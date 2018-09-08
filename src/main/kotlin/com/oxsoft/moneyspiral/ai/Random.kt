package com.oxsoft.moneyspiral.ai

import com.oxsoft.moneyspiral.core.*
import com.oxsoft.moneyspiral.state.ComputerState
import com.oxsoft.moneyspiral.state.GameState

class Random : ArtificialIntelligence {
    override fun calculate(gameState: GameState, player: Player): Action {
        while (true) {
            val action = when (r(6)) {
                0 -> Action.Buy(r(), 1)
                1 -> Action.Sell(r(), 1)
                2 -> Action.BuyComputers(listOfNotNull(
                    gameState.computers.filter {
                        it.computerState !is ComputerState.Unavailable && it.owner == null
                    }.map {
                        it.id
                    }.firstOrNull()
                ))
                3 -> Action.SetupComputers(
                    gameState.computers.filter {
                        (it.computerState is ComputerState.Available && it.owner == player)
                            || it.computerState is ComputerState.Working
                    }.take(2).associate {
                        it.id to ComputerState.Working(
                            Point(r(4), r(6)),
                            r(),
                            r()
                        )
                    }
                )
                4 -> Action.Gamble(null)
                else -> Action.ChangeNextStartPlayer(r())
            }
            try {
                gameState.execute(action)
                return action
            } catch (e: Exception) {
                continue
            }
        }
    }

    private fun r(i: Int) = (Math.random() * i).toInt()
    private inline fun <reified E : Enum<E>> r(): E {
        val v = enumValues<E>()
        return v[r(v.size)]
    }
}
