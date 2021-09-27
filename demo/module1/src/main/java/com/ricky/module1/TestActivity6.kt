package com.ricky.module1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ricky.base.PathConfig
import com.ricky.enavigation.api.INavigationInterceptor
import com.ricky.enavigation.api.anno.HostAndPathAnno
import com.ricky.enavigation.api.anno.InterceptorAnno

@HostAndPathAnno(
    PathConfig.Module1.Test6.PATH,
    interceptors = [TestInterceptor1::class],
    interceptorNames = [PathConfig.Module1.Test6.INTERCEPTOR1, PathConfig.Module1.Test6.INTERCEPTOR2],
)
class TestActivity6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.ricky.base.R.layout.empty_activity)
    }
}

class TestInterceptor1 : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        Toast.makeText(chain.request.activity, "拦截器1", Toast.LENGTH_SHORT).show()
        chain.proceed(chain.request)
    }
}

@InterceptorAnno(name = PathConfig.Module1.Test6.INTERCEPTOR2)
class TestInterceptor2 : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        Toast.makeText(chain.request.activity, "拦截器2", Toast.LENGTH_SHORT).show()
        chain.proceed(chain.request)
    }
}

@InterceptorAnno(name = PathConfig.Module1.Test6.INTERCEPTOR1)
class TestInterceptor3 : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        Toast.makeText(chain.request.activity, "拦截器3", Toast.LENGTH_SHORT).show()
        chain.proceed(chain.request)
    }
}
