package com.oxsoft.moneyspiral

import com.oxsoft.moneyspiral.ai.ArtificialIntelligence
import com.oxsoft.moneyspiral.ai.Gambler
import com.oxsoft.moneyspiral.ai.Random
import com.oxsoft.moneyspiral.core.Coin
import com.oxsoft.moneyspiral.state.GameState
import com.oxsoft.moneyspiral.core.Player
import com.oxsoft.moneyspiral.core.enumListOf

object Main {
    @JvmStatic
    fun main(vararg args: String) {
        var gameState = GameState.init()
        val ais = enumListOf<Player, ArtificialIntelligence> {
            if (it.ordinal == 0) {
                Random()
            } else {
                Gambler()
            }
        }
        while (true) {
            println(gameState)
            if (gameState.round > GameState.TOTAL_ROUND) {
                break
            }
            gameState = gameState.turn?.let {
                val action = ais[it].calculate(gameState, it)
                println("$it\t$action")
                gameState.execute(action)
            } ?: run {
                println()
                gameState.goToNextRound()
            }
        }
        // TODO: calc results
        gameState.players.withIndex().map { (index, state) ->
            Player.values()[index] to state.money + state.coins.mapWithKey<Coin, Int> { coin, amount ->
                gameState.coins[coin].price * amount
            }.sum()
        }.sortedByDescending { (_, money) -> money }.forEach { (player, money) ->
            println("$player\t$money")
        }
    }
}
