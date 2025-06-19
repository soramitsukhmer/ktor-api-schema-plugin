package me.learning.api_schema.dto.route.inline.impl

import me.learning.api_schema.common.RoutePropEnum
import me.learning.api_schema.dto.route.inline.RouteProp

data class PathVariable<T>(override val body: T) : RouteProp<T> {
    override val type: RoutePropEnum
        get() = RoutePropEnum.PATH_VARIABLE
}
