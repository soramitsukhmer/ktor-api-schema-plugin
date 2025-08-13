package me.learning.api_schema.dto.request

import me.learning.api_schema.common.DirectionEnum

abstract class PageRequest(
    open val size: Int = 10,
    open val page: Long = 0,
    open val sort: List<Order> = emptyList()
)

data class Order(
    val property: String,
    val direction: DirectionEnum
) {
    init { require(property.isNotBlank()) { "The property must not be blank." } }
}
