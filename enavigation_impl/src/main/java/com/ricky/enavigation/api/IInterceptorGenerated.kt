package com.ricky.enavigation.api

import java.util.*

/**
 *
 * @author haiyanghou
 * @date 2021/9/16
 */
interface IInterceptorGenerated {
    val host: String
    fun getGlobalInterceptors(): List<INavigationInterceptor>
    fun getInterceptors(): HashMap<String, INavigationInterceptor>
    fun getInterceptorByName(name: String): INavigationInterceptor?
}