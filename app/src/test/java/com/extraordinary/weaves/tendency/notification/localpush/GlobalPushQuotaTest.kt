package com.extraordinary.weaves.tendency.notification.localpush

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class GlobalPushQuotaTest {
    private val timeZone = TimeZone.getTimeZone("Asia/Shanghai")

    @Test
    fun `uses first max through the installation calendar day`() {
        val quota = GlobalPushQuota.select(
            firstMax = 2,
            dailyMax = 5,
            installedAtMs = timestamp("2026-10-22 23:55:00"),
            nowMs = timestamp("2026-10-22 23:59:59"),
            timeZone = timeZone,
        )

        assertEquals(2, quota.max)
        assertEquals(GlobalPushQuota.Period.FIRST_DAY, quota.period)
    }

    @Test
    fun `uses daily max at midnight after installation`() {
        val quota = GlobalPushQuota.select(
            firstMax = 2,
            dailyMax = 5,
            installedAtMs = timestamp("2026-10-22 23:55:00"),
            nowMs = timestamp("2026-10-23 00:00:00"),
            timeZone = timeZone,
        )

        assertEquals(5, quota.max)
        assertEquals(GlobalPushQuota.Period.DAILY, quota.period)
    }

    @Test
    fun `uses daily max on second calendar day even within first 24 hours`() {
        val quota = GlobalPushQuota.select(
            firstMax = 2,
            dailyMax = 5,
            installedAtMs = timestamp("2026-10-22 09:00:00"),
            nowMs = timestamp("2026-10-23 08:00:00"),
            timeZone = timeZone,
        )

        assertEquals(5, quota.max)
        assertEquals(GlobalPushQuota.Period.DAILY, quota.period)
    }

    @Test
    fun `uses daily max on installation day when legacy config omits first max`() {
        val quota = GlobalPushQuota.select(
            firstMax = null,
            dailyMax = 5,
            installedAtMs = timestamp("2026-10-22 09:00:00"),
            nowMs = timestamp("2026-10-22 10:00:00"),
            timeZone = timeZone,
        )

        assertEquals(5, quota.max)
        assertEquals(GlobalPushQuota.Period.FIRST_DAY, quota.period)
    }

    private fun timestamp(value: String): Long =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply { timeZone = this@GlobalPushQuotaTest.timeZone }
            .parse(value)!!.time
}
