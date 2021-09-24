package com.ricky.enavigation.api.anno

import androidx.annotation.Keep

/**
 *
 * @author haiyanghou
 * @date 2021/9/9
 */
@Keep
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class InterceptorAnno(val name: String)
