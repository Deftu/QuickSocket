package xyz.deftu.quicksocket.common.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Creates a JSON object that can be modified inside of a Kotlin closure.
 */
fun buildJsonObject(block: JsonObject.() -> Unit): JsonObject =
    JsonObject().apply(block)
/**
 * Validates whether a [String] is valid JSON or not.
 */
fun String.isJson() = try {
    JsonParser.parseString(this)
    true
} catch (e: Exception) {
    false
}