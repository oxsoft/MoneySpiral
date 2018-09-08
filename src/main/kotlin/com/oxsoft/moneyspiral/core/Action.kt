package com.oxsoft.moneyspiral.core

import com.oxsoft.moneyspiral.state.Computer
import com.oxsoft.moneyspiral.state.ComputerState

sealed class Action {
    data class Buy(val coin: Coin, val amount: Int) : Action()
    data class Sell(val coin: Coin, val amount: Int) : Action()
    data class BuyComputers(val ids: List<Int>) : Action()
    data class SetupComputers(val computerStates: Map<Int, ComputerState.Working>) : Action()
    data class Gamble(val mortgage: Computer?) : Action()
    data class ChangeNextStartPlayer(val player: Player) : Action()
}
