package com.ricky.enavigation.api.anno

import androidx.annotation.Keep

/**
 *
 * @author RickyHal
 * @date 2021/9/9
 */
@Keep
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class GlobalInterceptorAnno(val priority: Int = 0)
