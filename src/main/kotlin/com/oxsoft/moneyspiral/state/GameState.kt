package com.oxsoft.moneyspiral.state

import com.oxsoft.moneyspiral.core.*

data class GameState(
    val turn: Player?,
    val round: Int,
    val players: EnumList<Player, PlayerState>,
    val computers: List<Computer>,
    val coins: EnumList<Coin, CoinState>,
    val nextStartPlayer: Player
) {
    fun execute(action: Action): GameState = when (action) {
        is Action.Buy -> buy(action.coin, action.amount)
        is Action.Sell -> sell(action.coin, action.amount)
        is Action.BuyComputers -> buyComputers(action.ids)
        is Action.SetupComputers -> setupComputers(action.computerStates)
        is Action.Gamble -> gamble(action.mortgage)
        is Action.ChangeNextStartPlayer -> changeNextStartPlayer(action.player)
    }


    fun buy(coin: Coin, amount: Int): GameState {
        turn ?: throw IllegalStateException("Round is end.")
        val coinStare = coins[coin]
        val afterPlayerState = players[turn].buy(coin, coinStare.price, amount)
        val afterCoinState = coinStare.buy(amount)
        return copy(
            turn = calcNextPlayer(),
            players = players.copy(turn, afterPlayerState),
            coins = coins.copy(coin, afterCoinState)
        )
    }

    fun sell(coin: Coin, amount: Int): GameState {
        turn ?: throw IllegalStateException("Round is end.")
        val coinStare = coins[coin]
        val afterPlayerState = players[turn].sell(coin, coinStare.price, amount)
        val afterCoinState = coinStare.sell(amount)
        return copy(
            turn = calcNextPlayer(),
            players = players.copy(turn, afterPlayerState),
            coins = coins.copy(coin, afterCoinState)
        )
    }

    fun buyComputers(ids: List<Int>): GameState {
        turn ?: throw IllegalStateException("Round is end.")
        val targetComputers = ids.map { id ->
            computers.find { it.id == id } ?: throw IllegalArgumentException("Computer is not found.")
        }
        if (targetComputers.any { it.owner != null }) throw IllegalArgumentException("Computer is owned.")
        if (targetComputers.any { it.computerState == ComputerState.Unavailable }) throw IllegalArgumentException("Computer is unavailable.")
        val afterPlayerState = players[turn].buyComputers(targetComputers)
        val afterComputers = computers.map { computer ->
            targetComputers.find { it.id == computer.id }?.changeOwner(turn) ?: computer
        }
        return copy(
            turn = calcNextPlayer(),
            players = players.copy(turn, afterPlayerState),
            computers = afterComputers
        )
    }

    fun setupComputers(computerStates: Map<Int, ComputerState.Working>): GameState {
        turn ?: throw IllegalStateException("Round is end.")
        val afterPlayerState = players[turn].setupComputers(computerStates.size)
        val afterComputers = computers.map { computer ->
            computerStates[computer.id]?.let {
                if (computer.computerState == ComputerState.Unavailable) throw IllegalArgumentException("Computer is unavailable.")
                computer.changeComputerState(it)
            } ?: computer
        }
        val outOfBounds = afterComputers.any { computer ->
            computer.points().any { (x, y) ->
                x < 0 || x > 4 || y < 0 || y > 6
            }
        }
        if (outOfBounds) throw IllegalArgumentException("Computer position is out of bounds.")
        val points = afterComputers.map { it.points() }.flatten()
        if (points.size != points.distinct().size) throw IllegalArgumentException("Duplicate computer position.")
        return copy(
            turn = calcNextPlayer(),
            players = players.copy(turn, afterPlayerState),
            computers = afterComputers
        )
    }

    fun gamble(mortgage: Computer?): GameState {
        turn ?: throw IllegalStateException("Round is end.")
        val playerState = players[turn]
        if (mortgage == null && playerState.money < 6 && computers.any { it.owner == turn }) {
            throw IllegalArgumentException("Player need mortgage.")
        }
        if (mortgage != null && mortgage.owner != turn) {
            throw IllegalArgumentException("Mortgage is not player's.")
        }
        val (afterPlayerState, mortgageUsed) = playerState.gamble(mortgage)
        val afterComputers = computers.map {
            if (it.id == mortgage?.id && mortgageUsed) {
                it.changeOwner(null)
            } else {
                it
            }
        }
        return copy(
            turn = calcNextPlayer(),
            players = players.copy(turn, afterPlayerState),
            computers = afterComputers
        )
    }

    fun changeNextStartPlayer(player: Player): GameState {
        turn ?: throw IllegalStateException("Round is end.")
        val afterPlayerState = players[turn].changeNextStartPlayer()
        return copy(
            turn = calcNextPlayer(),
            players = players.copy(turn, afterPlayerState),
            nextStartPlayer = player
        )
    }

    fun goToNextRound(): GameState {
        if (players.any { it.action > 0 }) {
            throw IllegalStateException("Some players have action.")
        }
        val dices = enumListOf<Coin, Int> { Dice.roll() }
        val afterPlayers = enumListOf<Player, PlayerState> { player ->
            val afterCoins = enumListOf<Coin, Int> { coin ->
                computers.filter {
                    it.owner == player
                }.map {
                    it.points()
                }.flatten().filter { (x, _) ->
                    x >= coin.ordinal * 2 && x <= coin.ordinal * 2 + 1
                }.filter { (_, y) ->
                    y + 1 == dices[coin]
                }.count() + players[player].coins[coin]
            }
            players[player].copy(
                coins = afterCoins,
                action = INITIAL_ACTION
            )
        }
        val afterCoins = enumListOf<Coin, CoinState> {
            val price = coins[it].price
            val afterPrice = price + when (price) {
                in 0..5 -> listOf(-2, -1, 1, 2, 3, 4)
                in 6..7 -> listOf(-6, -1, 1, 2, 3, 4)
                in 8..9 -> listOf(-6, -6, 1, 2, 3, 4)
                else -> listOf(-6, -6, -6, 2, 3, 4)
            }[dices[it] - 1]
            coins[it].copy(price = afterPrice, demand = 0, supply = 0)
        }
        // TODO: when coin's price is zero or less
        return copy(
            turn = nextStartPlayer,
            round = round + 1,
            players = afterPlayers,
            coins = afterCoins,
            nextStartPlayer = nextStartPlayer.next
        )
    }

    private fun calcNextPlayer(): Player? {
        if (turn == null) return null
        val players = players.copy(turn, players[turn].copy(action = players[turn].action - 1))
        val length = Player.values().size
        Player.values().indices.map {
            it + turn.ordinal + 1
        }.map {
            (it + length) % length
        }.map {
            Player.values()[it]
        }.forEach {
            if (players[it].action > 0) return it
        }
        return null
    }

    companion object {
        const val TOTAL_ROUND = 7
        private const val INITIAL_ACTION = 2

        fun init() = GameState(
            turn = Player.values()[0],
            round = 1,
            players = enumListOf { player ->
                PlayerState(
                    coins = enumListOf { 0 },
                    money = 10 + player.ordinal * 2,
                    action = INITIAL_ACTION
                )
            },
            computers = listOf(
                Computer.Small(id = 0, computerState = ComputerState.Available),
                Computer.Small(id = 1, computerState = ComputerState.Available),
                Computer.Small(2),
                Computer.Small(3),
                Computer.Small(4),
                Computer.Medium.Straight(5),
                Computer.Medium.Bended(6),
                Computer.Large.Straight(7),
                Computer.Large.Bended(8)
            ),
            coins = enumListOf { CoinState(Dice.roll(), 0, 0) },
            nextStartPlayer = Player.values()[1]
        )
    }
}
