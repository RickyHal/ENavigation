package com.ricky.enavigation.core

import com.ricky.enavigation.api.INavigationInterceptor
import com.ricky.enavigation.support.Utils

/**
 *
 * @author RickyHal
 * @date 2021/9/17
 */
class InterceptorChain(
    private val interceptors: List<INavigationInterceptor>,
    private val index: Int,
    override val request: NavigationRequest
) : INavigationInterceptor.Chain {
    private fun isEnd() = index >= interceptors.size
    override fun proceed(request: NavigationRequest) {
        Utils.runOnUiThread {
            if (isEnd()) return@runOnUiThread
            interceptors[index].intercept(InterceptorChain(interceptors, index + 1, request))
        }
    }
}