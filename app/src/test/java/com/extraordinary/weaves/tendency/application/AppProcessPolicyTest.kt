package com.extraordinary.weaves.tendency.application

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppProcessPolicyTest {

    @Test
    fun `runs full initialization in the main process`() {
        assertTrue(
            shouldRunFullAppInitialization(
                processName = "com.extraordinary.weaves.tendency",
                packageName = "com.extraordinary.weaves.tendency",
            ),
        )
    }

    @Test
    fun `skips full initialization in the media process`() {
        assertFalse(
            shouldRunFullAppInitialization(
                processName = "com.extraordinary.weaves.tendency:appmedia",
                packageName = "com.extraordinary.weaves.tendency",
            ),
        )
    }
}
