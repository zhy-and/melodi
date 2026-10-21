package com.extraordinary.weaves.tendency.runtime

import android.app.job.JobParameters
import android.app.job.JobService

/** Only the ~4 second fallback for a rejected foreground-service start. */
class FgsRecoveryJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        MelodiRuntime.runRecoveryJob(this)
        jobFinished(params, false)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean = true
}
