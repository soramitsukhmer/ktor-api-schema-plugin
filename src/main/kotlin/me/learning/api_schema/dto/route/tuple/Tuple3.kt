package me.learning.api_schema.dto.route.tuple

data class Tuple3<T1 : Any, T2 : Any, T3 : Any>(
    val t1: T1,
    val t2: T2,
    val t3: T3
)
