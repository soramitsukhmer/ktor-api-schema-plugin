package com.skh.api_schema.extension

import io.ktor.http.ContentType
import com.skh.api_schema.common.RoutePropEnum
import com.skh.api_schema.common.Helper.extractAllPathParameters
import java.io.File
import java.util.regex.Pattern
import kotlin.reflect.KClass

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


fun String.getApiSchemaBuilderProp(
    pairs: List<Pair<KClass<*>, RoutePropEnum>> = emptyList()
): Pair<Map<String, KClass<*>>, KClass<*>?> {
    val allPathVar = extractAllPathParameters(this)
    val variable = pairs.filter { it.second == RoutePropEnum.PATH_VARIABLE }
        .mapIndexed { index, pair -> (allPathVar.getOrNull(index) ?: "") to pair.first }
        .toMap()
    val requestBody = pairs.find { it.second == RoutePropEnum.REQUEST_BODY }?.first
    return Pair(variable, requestBody)
}

fun String.cleanRoutePath() = when (endsWith("/")) {
    true -> substringBeforeLast("/")
    false -> this
}

fun File.determineContentType() : String {
    val extension = this.extension.lowercase()
    val detectedType = when (extension) {
        "jpg", "jpeg" -> ContentType.Image.JPEG
        "png" -> ContentType.Image.PNG
        "gif" -> ContentType.Image.GIF
        "pdf" -> ContentType.Application.Pdf
        "json" -> ContentType.Application.Json
        "xml" -> ContentType.Application.Xml
        "txt" -> ContentType.Text.Plain
        "csv" -> ContentType.Text.CSV
        "zip" -> ContentType.Application.Zip
        else -> ContentType.Application.OctetStream
    }
    return detectedType.toString()
}
