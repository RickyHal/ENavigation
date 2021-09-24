package com.ricky.enavigation.api

import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import com.ricky.enavigation.core.NavigationRequest

/**
 *
 * @author haiyanghou
 * @date 2021/9/9
 */
interface INavigationInterceptor {
    @UiThread
    @Throws(Exception::class)
    fun intercept(chain: Chain)
    interface Chain {
        val request: NavigationRequest

        @AnyThread
        @Throws(Exception::class)
        fun proceed(request: NavigationRequest)
    }
}