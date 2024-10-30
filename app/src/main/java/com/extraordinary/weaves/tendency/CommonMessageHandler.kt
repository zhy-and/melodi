package com.extraordinary.weaves.tendency

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference


class CommonMessageHandler<T>(cls : T) : Handler(Looper.getMainLooper()) {
    private var ref: WeakReference<T>? = null

    init {
        ref = WeakReference(cls)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        ref?.get()?.apply {
            val activity = this as? baseActivity ?: return
            if (activity.isFinishing || activity.isDestroyed) return
            activity.receiverMsgCallBack(msg)
        }
    }
}
