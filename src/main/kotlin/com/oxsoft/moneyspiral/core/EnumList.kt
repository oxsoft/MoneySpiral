package com.oxsoft.moneyspiral.core

class EnumList<K : Enum<K>, V>(private val values: List<V>) : List<V> by values {
    operator fun get(key: K) = values[key.ordinal]
    fun copy(key: K, value: V) = EnumList<K, V>(values.mapIndexed { index, v ->
        if (index == key.ordinal) {
            value
        } else {
            v
        }
    })

    override fun toString() = values.toString()

    inline fun <reified K : Enum<K>, reified T> mapWithKey(block: (K, V) -> T) = withIndex().map { (index, v) ->
        block(enumValues<K>()[index], v)
    }
}

inline fun <reified K : Enum<K>, V> enumListOf(block: (K) -> V) = EnumList<K, V>(enumValues<K>().map(block))
