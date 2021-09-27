package com.ricky.base

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.ricky.enavigation.ENavigation
import com.ricky.enavigation.api.INavigationInterceptor
import com.ricky.enavigation.api.anno.GlobalInterceptorAnno

/**
 *
 * @author RickyHal
 * @date 2021/9/9
 */
class BaseApplication : Application() {
    companion object {
        lateinit var application: BaseApplication
    }

    var isEnableGlobalInterceptor = false
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        ENavigation.init(this, "base", "app", "module1", "module2")
    }
}

@GlobalInterceptorAnno(priority = 2)
class MyGlobalInterceptor1 : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        if (BaseApplication.application.isEnableGlobalInterceptor) {
            Toast.makeText(chain.request.activity, "全局拦截器1", Toast.LENGTH_SHORT).show()
        }
        chain.proceed(chain.request)
    }
}

@GlobalInterceptorAnno
class MyGlobalInterceptor2 : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        if (BaseApplication.application.isEnableGlobalInterceptor) {
            Toast.makeText(chain.request.activity, "全局拦截器2", Toast.LENGTH_SHORT).show()
        }
        chain.proceed(chain.request)
    }
}