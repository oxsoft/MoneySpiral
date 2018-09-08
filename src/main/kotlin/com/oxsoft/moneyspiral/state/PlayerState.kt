package com.oxsoft.moneyspiral.state

import com.oxsoft.moneyspiral.core.Coin
import com.oxsoft.moneyspiral.core.Dice
import com.oxsoft.moneyspiral.core.EnumList
import kotlin.math.abs

data class PlayerState(
    val coins: EnumList<Coin, Int>,
    val money: Int,
    val action: Int
) {
    fun buy(coin: Coin, price: Int, amount: Int): PlayerState {
        if (amount < 0) throw IllegalArgumentException("Amount must be positive number.")
        if (action - amount < 0) throw IllegalStateException("Not enough action.")
        if (price * amount > money) {
            throw IllegalStateException("Not enough money.")
        }
        if (coins[coin] + amount > MAX_COIN) {
            throw IllegalStateException("Too many coin.")
        }
        return copy(
            coins = coins.copy(coin, coins[coin] + amount),
            money = money - price * amount,
            action = action - amount
        )
    }

    fun sell(coin: Coin, price: Int, amount: Int): PlayerState {
        if (amount < 0) throw IllegalArgumentException("Amount must be positive number.")
        if (action - amount < 0) throw IllegalStateException("Not enough action.")
        if (coins[coin] - amount < MIN_COIN) {
            throw IllegalStateException("Too less coin.")
        }
        return copy(
            coins = coins.copy(coin, coins[coin] - amount),
            money = money + price * amount,
            action = action - amount
        )
    }

    fun buyComputers(computers: List<Computer>): PlayerState {
        if (action - computers.size < 0) throw IllegalStateException("Not enough action.")
        val totalPrice = computers.map { it.price }.sum()
        if (money < totalPrice) throw IllegalStateException("Player does not have enough money.")
        return copy(
            money = money - totalPrice,
            action = action - computers.size
        )
    }

    fun setupComputers(count: Int): PlayerState {
        if (count < 0) throw IllegalArgumentException("Count must be positive number.")
        val c = if (count == 0) 1 else count
        val afterAction = action - 1 - (c - 1) / COMPUTER_SETUP_ACTION
        if (afterAction < 0) throw IllegalStateException("Not enough action.")
        return copy(action = afterAction)
    }

    fun gamble(mortgage: Computer?): Pair<PlayerState, Boolean> {
        requireAction()
        val a = Dice.roll()
        val b = Dice.roll()
        val diff = abs(a - b)
        val (afterMoney, mortgageUsed) = if (diff == 0) {
            val m = money - a
            if (m < 0) {
                if (mortgage == null) {
                    0 to false
                } else {
                    m + mortgage.price to true
                }
            } else {
                m to false
            }
        } else {
            money + diff to false
        }
        return copy(
            action = action - 1,
            money = afterMoney
        ) to mortgageUsed
    }

    fun changeNextStartPlayer(): PlayerState {
        requireAction()
        return copy(action = action - 1)
    }

    private fun requireAction() {
        if (action == 0) throw IllegalStateException("No action.")
    }

    companion object {
        const val MAX_COIN = 10
        const val MIN_COIN = -4
        const val COMPUTER_SETUP_ACTION = 2
    }
}
