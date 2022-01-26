package xyz.deftu.quicksocket.common.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser

fun buildJsonObject(block: JsonObject.() -> Unit): JsonObject =
    JsonObject().apply(block)
fun String.isJson(): Boolean {
    return try {
        JsonParser.parseString(this)
        true
    } catch(e: Exception) {
        false
    }
}