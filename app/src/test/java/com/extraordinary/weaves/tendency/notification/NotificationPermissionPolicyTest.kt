package com.extraordinary.weaves.tendency.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPermissionPolicyTest {
    @Test fun firstDenialAllowsHomeRetry() {
        val result = NotificationPermissionPolicy.afterResult(false, 0)
        assertEquals(1, result.denials)
        assertEquals(NotificationPermissionPolicy.NextStep.REQUEST, result.nextStep)
        assertTrue(NotificationPermissionPolicy.shouldRequestAtHome(1, false))
    }

    @Test fun secondDenialStopsLaunchRequests() {
        val result = NotificationPermissionPolicy.afterResult(false, 1)
        assertEquals(NotificationPermissionPolicy.NextStep.NONE, result.nextStep)
        assertFalse(NotificationPermissionPolicy.shouldRequestAtLaunch(result.denials))
    }
}
