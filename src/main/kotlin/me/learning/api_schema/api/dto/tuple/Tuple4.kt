package me.learning.api_schema.api.dto.tuple

data class Tuple4<T1 : Any, T2 : Any, T3 : Any, T4 : Any>(
    val t1: T1,
    val t2: T2,
    val t3: T3,
    val t4: T4
)
