package com.skh.api_schema.dto.route.inline

import com.skh.api_schema.common.RoutePropEnum

interface RouteProp<T> {
    val type: RoutePropEnum
    val body: T
}
