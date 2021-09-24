package com.ricky.demo

import android.widget.Toast
import com.ricky.enavigation.api.INavigationInterceptor

/**
 *
 * @author haiyanghou
 * @date 2021/9/24
 */
class DynamicAddedInterceptor : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        val request = chain.request
        Toast.makeText(request.activity, "动态添加了一个拦截器", Toast.LENGTH_SHORT).show()
        chain.proceed(request)
    }
}