package me.learning.api_schema.extension

import java.util.regex.Pattern

fun String.isEmail() : Boolean {
    val emailRegex =
        Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$")
    return emailRegex.matcher(this).matches()
}

fun String.isPhone() : Boolean {
    val faxRegex = Pattern.compile("855[\\d]{8,9}")
    return faxRegex.matcher(this).matches()
}

fun String.isUsername(): Boolean {
    val regex = Pattern.compile("^(?! )[a-z_0-9]{1,32}")
    return regex.matcher(this).matches()
}

fun String.isContainSpace(): Boolean {
    val string = Pattern.compile(".[\\S]*")
    return string.matcher(this).matches()
}

fun String.isFax() : Boolean {
    val faxRegex = Pattern.compile("855[\\d]{8,9}")
    return faxRegex.matcher(this).matches()
}


fun String.cleanRoute() = when (endsWith("/")) {
    true -> substringBeforeLast("/")
    false -> this
}

fun String.mergeRoute(baseRoute: String, defaultPath: String): String {
    val path = cleanRoute().takeIf { it.trim().isNotEmpty() } ?: defaultPath
    val base = baseRoute.cleanRoute()

    return when (path.startsWith("/")) {
        true -> path
        false -> "/$path"
    }.let(base::plus)
}

