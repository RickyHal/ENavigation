package com.ricky.enavigation.core

import android.app.Activity
import android.app.Application
import com.ricky.enavigation.api.IInterceptorGenerated
import com.ricky.enavigation.api.INavigationInterceptor
import com.ricky.enavigation.api.IPathGenerated
import com.ricky.enavigation.bean.NavigationBean
import com.ricky.enavigation.support.ASMUtil
import com.ricky.enavigation.support.ActivityStackManager
import com.ricky.enavigation.support.Utils

/**
 *
 * @author haiyanghou
 * @date 2021/9/23
 */
class RealENavigation {
    private val packageName = "com.ricky.enavigation"
    private lateinit var activityStackManager: ActivityStackManager
    private val interceptorModules = HashMap<String, IInterceptorGenerated>()
    private val pathModules = HashMap<String, IPathGenerated>()

    /**
     * Manual register
     */
    fun init(application: Application, vararg moduleName: String) {
        application.registerActivityLifecycleCallbacks(ActivityStackManager().also { activityStackManager = it })
        moduleName.map { Utils.capitalize(it) }.forEach {
            initModule(it)
        }
    }

    /**
     * Auto register
     */
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(ActivityStackManager().also { activityStackManager = it })
        val moduleNameList = ASMUtil.getModuleNameList()
        if (moduleNameList.isEmpty()) {
            throw IllegalStateException("Couldn't find any module, Please apply the plugin in build.gradle.")
        }
        moduleNameList.forEach {
            val pathClass = ASMUtil.getPathClassByHost(it)
            if (pathClass != null) {
                pathModules[it] = pathClass
            }
            val interceptorClass = ASMUtil.getInterceptorClassByHost(it)
            if (interceptorClass != null) {
                interceptorModules[it] = interceptorClass
            }
        }
    }

    fun with(): NavigationRequest {
        if (::activityStackManager.isInitialized) {
            return with(activityStackManager.getCurrentActivity())
        } else {
            throw IllegalStateException("You must execute ENavigation.init(application) before execute this method!")
        }
    }

    fun with(activity: Activity): NavigationRequest {
        return NavigationRequest(activity = activity)
    }

    private fun initModule(moduleName: String) {
        initPath(moduleName)
        initInterceptor(moduleName)
    }

    private fun initPath(moduleName: String) {
        try {
            val capModuleName = Utils.capitalize(moduleName)
            val className = "$packageName.impl.path.${capModuleName}PathGenerated"
            val cls = Class.forName(className)
            val pathHolder = cls.newInstance() as IPathGenerated
            if (!pathHolder.host.endsWith(moduleName, true)) {
                throw IllegalStateException("Module $moduleName doesn't exists, please check your config.")
            }
            pathModules[moduleName] = pathHolder
        } catch (e: ClassNotFoundException) {
        }
    }

    private fun initInterceptor(moduleName: String) {
        try {
            val capModuleName = Utils.capitalize(moduleName)
            val className = "$packageName.impl.interceptor.${capModuleName}InterceptorGenerated"
            val cls = Class.forName(className)
            val interceptorHolder = cls.newInstance() as IInterceptorGenerated
            if (!interceptorHolder.host.endsWith(moduleName, true)) {
                throw IllegalStateException("Module $moduleName doesn't exists, please check your config.")
            }
            interceptorModules[moduleName] = interceptorHolder
        } catch (e: ClassNotFoundException) {
            // 没有使用过注解的话会存在找不到类的情况
        }
    }

    internal fun getGlobalInterceptors(): List<INavigationInterceptor> {
        val list = mutableListOf<INavigationInterceptor>()
        interceptorModules.values.forEach {
            val interceptors = it.getGlobalInterceptors()
            list.addAll(interceptors)
        }
        return list
    }

    internal fun getNavigationBean(host: String, path: String): NavigationBean? {
        return pathModules.getOrDefault(host, null)?.getPathMap()?.getOrDefault("$host/$path", null)
    }

    internal fun getInterceptorByName(name: String): INavigationInterceptor? {
        interceptorModules.values.forEach {
            val interceptor = it.getInterceptorByName(name)
            if (interceptor != null) return interceptor
        }
        return null
    }
}