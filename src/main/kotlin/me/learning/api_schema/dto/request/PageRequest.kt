package me.learning.api_schema.dto.request

import jakarta.validation.constraints.NotBlank
import me.learning.api_schema.common.DirectionEnum

abstract class PageRequest(
    open val size: Int = 10,
    open val page: Long = 0,
    open val sort: List<Order> = emptyList()
)

data class Order(
    @field:NotBlank(message = "The property field must not be blank") val property: String,
    val direction: DirectionEnum
)
