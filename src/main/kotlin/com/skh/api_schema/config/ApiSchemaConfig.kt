package com.skh.api_schema.config

import com.skh.api_schema.config.exception.ExceptionConfig

class ApiSchemaConfig internal constructor() {
    var handler = ExceptionConfig()

    fun handler(block: ExceptionConfig.() -> Unit) {
        handler = ExceptionConfig().apply(block)
    }
}
