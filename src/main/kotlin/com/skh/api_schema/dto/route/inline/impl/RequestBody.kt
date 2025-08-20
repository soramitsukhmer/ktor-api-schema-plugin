package com.skh.api_schema.dto.route.inline.impl

import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.dto.route.inline.RouteProp

data class RequestBody<T>(override val body: T) : RouteProp<T> {
    override val type: RoutePropEnum
        get() = RoutePropEnum.REQUEST_BODY
}
