package com.ricky.enavigation.error

/**
 *
 * @author haiyanghou
 * @date 2021/9/24
 */
class NavigateUncaughtExceptionHandler(private val throwError: (e: Exception) -> Unit) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        throwError(Exception(e))
    }
}