package com.extraordinary.weaves.tendency.runtime

enum class NotificationToolbarAction(val wireValue: String) {
    PLAY_PAUSE("play_pause"),
    LIBRARY("library"),
    SCAN("scan"),
    HOME("home");

    companion object {
        fun fromWireValue(value: String?): NotificationToolbarAction =
            entries.firstOrNull { it.wireValue == value } ?: HOME
    }
}
