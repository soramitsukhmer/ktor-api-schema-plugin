package me.learning.api_schema.config.exception

import io.ktor.server.plugins.statuspages.StatusPagesConfig

class ExceptionConfig internal constructor() {
    var enabled: Boolean = true

    var property = StatusPagesConfig()

    fun config(block: StatusPagesConfig.() -> Unit) {
        property = StatusPagesConfig().apply(block)
    }
}
