package com.ricky.enavigation.support

import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import java.util.*

/**
 *
 * @author RickyHal
 * @date 2021/9/17
 */
internal object Utils {
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    @AnyThread
    fun runOnUiThread(block: () -> Unit) {
        if (isMainThread()) {
            block()
        } else {
            handler.post(block)
        }
    }

    fun isMainThread() = Thread.currentThread() == Looper.getMainLooper().thread

    fun capitalize(str: String): String {
        return str.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}