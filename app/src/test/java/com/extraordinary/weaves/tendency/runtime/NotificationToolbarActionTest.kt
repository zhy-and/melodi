package com.extraordinary.weaves.tendency.runtime

import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationToolbarActionTest {

    @Test
    fun `maps notification shortcuts to the redesigned actions`() {
        assertEquals(
            NotificationToolbarAction.PLAY_PAUSE,
            NotificationToolbarAction.fromWireValue("play_pause"),
        )
        assertEquals(
            NotificationToolbarAction.LIBRARY,
            NotificationToolbarAction.fromWireValue("library"),
        )
        assertEquals(
            NotificationToolbarAction.SCAN,
            NotificationToolbarAction.fromWireValue("scan"),
        )
    }

    @Test
    fun `falls back to home for an unknown notification action`() {
        assertEquals(
            NotificationToolbarAction.HOME,
            NotificationToolbarAction.fromWireValue("minty_favorites"),
        )
    }
}
