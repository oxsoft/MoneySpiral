package com.oxsoft.moneyspiral.ai

import com.oxsoft.moneyspiral.core.Action
import com.oxsoft.moneyspiral.state.GameState
import com.oxsoft.moneyspiral.core.Player

class Gambler : ArtificialIntelligence {
    override fun calculate(gameState: GameState, player: Player) = Action.Gamble(null)
}
