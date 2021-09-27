@file:Suppress("unused")

package com.ricky.enavigation

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.ricky.enavigation.api.INavigationInterceptor
import com.ricky.enavigation.bean.NavigationBean
import com.ricky.enavigation.core.NavigationRequest
import com.ricky.enavigation.core.RealENavigation
import com.ricky.enavigation.error.NavigationException

/**
 *
 * @author RickyHal
 * @date 2021/9/9
 */
object ENavigation {
    private val realENavigation: RealENavigation by lazy { RealENavigation() }

    fun init(application: Application, vararg moduleName: String) {
        if (moduleName.isEmpty()) {
            realENavigation.init(application)
        } else {
            realENavigation.init(application, *moduleName)
        }
    }

    fun with(): NavigationRequest {
        return realENavigation.with()
    }

    fun with(fragment: Fragment): NavigationRequest {
        return realENavigation.with(fragment.requireActivity())
    }

    fun with(activity: Activity): NavigationRequest {
        return realENavigation.with(activity = activity)
    }

    internal fun getNavigationBean(host: String, path: String): NavigationBean {
        return realENavigation.getNavigationBean(host, path) ?: throw NavigationException.NullTargetException("Couldn't find path: $host/$path")
    }

    internal fun getGlobalInterceptors(): List<INavigationInterceptor> {
        return realENavigation.getGlobalInterceptors()
    }

    internal fun getInterceptorByName(name: String): INavigationInterceptor? {
        return realENavigation.getInterceptorByName(name)
    }
}