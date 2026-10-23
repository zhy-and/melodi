package com.extraordinary.weaves.tendency.notification.localpush

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Selects the global frequency cap using the device's local calendar day. */
object GlobalPushQuota {
    enum class Period {
        FIRST_DAY,
        DAILY,
    }

    data class Selection(
        val period: Period,
        val max: Int,
    )

    /** Legacy configs without first_max retain daily_max as their installation-day cap. */
    fun select(
        firstMax: Int?,
        dailyMax: Int,
        installedAtMs: Long,
        nowMs: Long,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): Selection {
        val localDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { this.timeZone = timeZone }
        return if (localDate.format(Date(installedAtMs)) == localDate.format(Date(nowMs.coerceAtLeast(installedAtMs)))) {
            Selection(Period.FIRST_DAY, firstMax ?: dailyMax)
        } else {
            Selection(Period.DAILY, dailyMax)
        }
    }
}
