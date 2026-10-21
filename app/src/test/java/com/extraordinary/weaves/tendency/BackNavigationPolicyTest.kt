package com.extraordinary.weaves.tendency

import org.junit.Assert.assertEquals
import org.junit.Test

class BackNavigationPolicyTest {

    @Test
    fun `routes standard pages to the first exit ad`() {
        assertEquals(BackNavigationPolicy.ExitAction.FIRST_AD, BackNavigationPolicy.exitActionFor("com.example.AppSearch"))
    }

    @Test
    fun `routes detail pages to the second exit ad`() {
        assertEquals(BackNavigationPolicy.ExitAction.SECOND_AD, BackNavigationPolicy.exitActionFor("com.example.AppAlbumDetail"))
    }

    @Test
    fun `does not apply a base exit ad to dedicated back handlers`() {
        assertEquals(BackNavigationPolicy.ExitAction.NONE, BackNavigationPolicy.exitActionFor("com.example.PlayerView"))
    }
}
