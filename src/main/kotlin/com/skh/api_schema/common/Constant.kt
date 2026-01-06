package com.skh.api_schema.common

object Constant {
    const val SECURITY_BEARER_SCHEMA_NAME = "bearerAuth"

    /** KH */
    const val DEFAULT_PHONE_REGION = "KH"

    /** yyyy-MM-dd HH:mm:ss */
    const val ISO_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    /** MM/dd/yyyy HH:mm:ss */
    const val DATETIME_FORMAT = "MM/dd/yyyy HH:mm:ss"
    /** MM/dd/yyyy */
    const val DATE_FORMAT = "MM/dd/yyyy"
    /** HH:mm */
    const val TIME_FORMAT = "HH:mm:ss"

    const val DEFAULT_MAX_FILE_SIZE_100MB: Long = 100

    // Complete pattern: "field", "field.nested", "field.nested.deep", "field,asc" or "field,desc"
    val SORTABLE_PATTERN = Regex("^([a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)*)(?:\\s*,\\s*(asc|desc|ASC|DESC))?$")

    val SORT_DIRECTIONS = mapOf("asc" to true, "desc" to false)
}
