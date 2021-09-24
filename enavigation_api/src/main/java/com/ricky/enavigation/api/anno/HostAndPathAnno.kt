package com.ricky.enavigation.api.anno

import androidx.annotation.Keep
import kotlin.reflect.KClass

/**
 *
 * @author haiyanghou
 * @date 2021/9/9
 */
@Keep
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class HostAndPathAnno(
    val hostAndPath: String,
    val interceptors: Array<KClass<*>> = [],
    val interceptorNames: Array<String> = [],
    val desc: String = ""
)
