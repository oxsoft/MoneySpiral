package com.oxsoft.moneyspiral.state

data class CoinState(
    val price: Int,
    val demand: Int,
    val supply: Int
) {
    fun buy(amount: Int) = copy(
        price = price + if (demand + amount >= DEMAND_THRESHOLD) 1 else 0,
        demand = (demand + amount) % DEMAND_THRESHOLD
    )

    fun sell(amount: Int) = copy(
        price = price - if (supply + amount >= SUPPLY_THRESHOLD) 1 else 0,
        supply = (supply + amount) % SUPPLY_THRESHOLD
    )

    companion object {
        const val DEMAND_THRESHOLD = 2
        const val SUPPLY_THRESHOLD = 2
    }
}
