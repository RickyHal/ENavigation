package com.ricky.enavigation.complier.bean

/**
 *
 * @author RickyHal
 * @date 2021/9/14
 */
internal data class NavigationAnnoBean(
    val path: String,
    val target: String,
    val interceptors: List<String> = listOf(),
    val interceptorNames: List<String> = listOf(),
    val desc: String = ""
)

