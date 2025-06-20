package me.learning.api_schema.dto.route.inline

import me.learning.api_schema.common.RoutePropEnum

interface RouteProp<T> {
    val type: RoutePropEnum
    val body: T
}
