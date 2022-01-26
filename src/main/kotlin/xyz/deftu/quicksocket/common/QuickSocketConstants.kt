package xyz.deftu.quicksocket.common

import com.google.gson.GsonBuilder

object QuickSocketConstants {
    val GSON = GsonBuilder()
        .setPrettyPrinting()
        .setLenient()
        .create()
}