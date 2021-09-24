package com.ricky.enavigation.bean

import com.ricky.enavigation.api.INavigationInterceptor
import kotlin.reflect.KClass

/**
 *
 * @author haiyanghou
 * @date 2021/9/17
 */
data class NavigationBean(
    val path: String,
    val target: KClass<*>,
    val stringInterceptors: List<String> = listOf(),
    val classInterceptors: List<INavigationInterceptor> = listOf(),
    val desc: String = ""
)