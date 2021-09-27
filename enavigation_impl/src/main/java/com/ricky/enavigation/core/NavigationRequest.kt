@file:Suppress("unused")

package com.ricky.enavigation.core

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import androidx.annotation.AnimRes
import androidx.fragment.app.FragmentActivity
import com.ricky.enavigation.ENavigation
import com.ricky.enavigation.api.INavigationInterceptor
import com.ricky.enavigation.bean.NavigationBean
import com.ricky.enavigation.error.NavigateUncaughtExceptionHandler
import com.ricky.enavigation.error.NavigationException
import com.ricky.enavigation.impl.R
import com.ricky.enavigation.support.Utils
import java.io.Serializable
import kotlin.random.Random


/**
 *
 * @author RickyHal
 * @date 2021/9/9
 */

class NavigationRequest(val activity: Activity? = null) : Serializable {
    companion object {
        val RANDOM_REQUEST_CODE: Int get() = Random.nextInt(0, 65536)
    }

    private var host: String? = null
    private var path: String? = null
    private var scheme: String? = null

    private val interceptors: MutableList<INavigationInterceptor> = mutableListOf()

    private var isNavigating = false

    @AnimRes
    internal var animationIn: Int = -1
        private set

    @AnimRes
    internal var animationOut: Int = -1
        private set

    val intent: Intent = Intent()
    private var navigationBean: NavigationBean? = null

    private var exceptionCallback: ((Exception) -> Unit)? = null
    internal var beforeAction: (FragmentActivity) -> Unit = {}
        private set
    internal var afterAction: (FragmentActivity) -> Unit = {}
        private set
    internal var onResult: ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)? = null
        private set
    internal var requestCode: Int = RANDOM_REQUEST_CODE
        private set

    fun setHostAndPath(hostAndPath: String) = apply {
        hostAndPath.split("/").forEachIndexed { index, item ->
            when (index) {
                0 -> host = Utils.capitalize(item)
                1 -> path = item
            }
        }
    }

    fun setScheme(scheme: String) = apply {
        this.scheme = scheme
    }

    fun onError(callback: (Exception) -> Unit) = apply {
        this.exceptionCallback = callback
    }

    fun setInterceptors(vararg interceptor: INavigationInterceptor) = apply {
        this.interceptors.clear()
        this.interceptors.addAll(interceptor)
    }

    fun beforeAction(action: (FragmentActivity) -> Unit) = apply {
        this.beforeAction = action
    }

    fun afterAction(action: (FragmentActivity) -> Unit) = apply {
        this.afterAction = action
    }

    fun onResult(block: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit) = apply {
        this.onResult = block
    }

    fun requestCode(code: Int) = apply {
        this.requestCode = code
    }

    fun putInt(key: String, value: Int) = apply {
        intent.putExtra(key, value)
    }

    fun putByte(key: String, value: Byte) = apply {
        intent.putExtra(key, value)
    }

    fun putChar(key: String, value: Char) = apply {
        intent.putExtra(key, value)
    }

    fun putLong(key: String, value: Long) = apply {
        intent.putExtra(key, value)
    }

    fun putFloat(key: String, value: Float) = apply {
        intent.putExtra(key, value)
    }

    fun putShort(key: String, value: Short) = apply {
        intent.putExtra(key, value)
    }

    fun putDouble(key: String, value: Double) = apply {
        intent.putExtra(key, value)
    }

    fun putBoolean(key: String, value: Boolean) = apply {
        intent.putExtra(key, value)
    }

    fun putBundle(key: String, value: Bundle) = apply {
        intent.putExtra(key, value)
    }

    fun putString(key: String, value: String?) = apply {
        intent.putExtra(key, value)
    }

    fun putIntArray(key: String, value: IntArray?) = apply {
        intent.putExtra(key, value)
    }

    fun putByteArray(key: String, value: ByteArray?) = apply {
        intent.putExtra(key, value)
    }

    fun putCharArray(key: String, value: CharArray?) = apply {
        intent.putExtra(key, value)
    }

    fun putLongArray(key: String, value: LongArray?) = apply {
        intent.putExtra(key, value)
    }

    fun putFloatArray(key: String, value: FloatArray?) = apply {
        intent.putExtra(key, value)
    }

    fun putParcelable(key: String, value: Parcelable?) = apply {
        intent.putExtra(key, value)
    }

    fun putShortArray(key: String, value: ShortArray?) = apply {
        intent.putExtra(key, value)
    }

    fun putDoubleArray(key: String, value: DoubleArray?) = apply {
        intent.putExtra(key, value)
    }

    fun putBooleanArray(key: String, value: BooleanArray?) = apply {
        intent.putExtra(key, value)
    }

    fun putCharSequenceArray(key: String, value: CharSequence?) = apply {
        intent.putExtra(key, value)
    }

    fun putSerializable(key: String, value: Serializable?) = apply {
        intent.putExtra(key, value)
    }

    fun combineIntent(intent: Intent) = apply {
        this.intent.putExtras(intent)
    }

    fun combineBundle(bundle: Bundle) = apply {
        intent.putExtras(bundle)
    }

    fun setAction(action: String) = apply {
        intent.action = action
    }

    fun setFlags(flags: Int) = apply {
        intent.flags = flags
    }

    fun setCategories(vararg category: String) = apply {
        category.forEach {
            intent.addCategory(it)
        }
    }

    fun setType(type: String?) = apply {
        intent.type = type
    }

    fun setDataAndType(uri: Uri?, type: String?) = apply {
        intent.setDataAndType(uri, type)
    }

    fun setData(uri: Uri?) = apply {
        intent.data = uri
    }

    /** Animation */
    fun animateIn(@AnimRes anim: Int) = apply {
        this.animationIn = anim
    }

    fun animateOut(@AnimRes anim: Int) = apply {
        this.animationOut = anim
    }

    fun fade() = apply {
        this.animationIn = android.R.anim.fade_in
        this.animationOut = android.R.anim.fade_out
    }

    fun right() = apply {
        this.animationIn = R.anim.slide_in_right
        this.animationOut = android.R.anim.fade_out
    }

    fun top() = apply {
        this.animationIn = R.anim.slide_in_top
        this.animationOut = android.R.anim.fade_out
    }

    fun bottom() = apply {
        this.animationIn = R.anim.slide_in_bottom
        this.animationOut = android.R.anim.fade_out
    }

    fun left() = apply {
        this.animationIn = R.anim.slide_in_left
        this.animationOut = android.R.anim.fade_out
    }

    fun expand() = apply {
        this.animationIn = R.anim.expand_in_center
        this.animationOut = android.R.anim.fade_out
    }

    fun fadeIn() = apply {
        this.animationIn = android.R.anim.fade_in
    }

    fun fadeOut() = apply {
        this.animationOut = android.R.anim.fade_out
    }

    fun leftIn() = apply {
        this.animationIn = R.anim.slide_in_left
    }

    fun leftOut() = apply {
        this.animationOut = R.anim.slide_out_left
    }

    fun rightIn() = apply {
        this.animationIn = R.anim.slide_in_right
    }

    fun rightOut() = apply {
        this.animationOut = R.anim.slide_out_right
    }

    fun bottomIn() = apply {
        this.animationIn = R.anim.slide_in_bottom
    }

    fun bottomOut() = apply {
        this.animationOut = R.anim.slide_out_bottom
    }

    fun topIn() = apply {
        this.animationIn = R.anim.slide_in_top
    }

    fun topOut() = apply {
        this.animationOut = R.anim.slide_out_top
    }

    fun expandTopLeftIn() = apply {
        this.animationIn = R.anim.expand_in_top_left
    }

    fun shrinkTopLeftOut() = apply {
        this.animationOut = R.anim.shrink_out_top_left
    }

    fun expandTopCenterIn() = apply {
        this.animationIn = R.anim.expand_in_top_center
    }

    fun shrinkTopCenterOut() = apply {
        this.animationOut = R.anim.shrink_out_top_center
    }

    fun expandTopRightIn() = apply {
        this.animationIn = R.anim.expand_in_top_right
    }

    fun shrinkTopRightOut() = apply {
        this.animationOut = R.anim.shrink_out_top_right
    }

    fun expandCenterLeftIn() = apply {
        this.animationIn = R.anim.expand_in_center_left
    }

    fun shrinkCenterLeftOut() = apply {
        this.animationOut = R.anim.shrink_out_center_left
    }

    fun expandCenterIn() = apply {
        this.animationIn = R.anim.expand_in_center
    }

    fun shrinkCenterOut() = apply {
        this.animationOut = R.anim.shrink_out_center
    }

    fun expandCenterRightIn() = apply {
        this.animationIn = R.anim.expand_in_center_right
    }

    fun shrinkCenterRightOut() = apply {
        this.animationOut = R.anim.shrink_out_center_right
    }

    fun expandBottomLeftIn() = apply {
        this.animationIn = R.anim.expand_in_bottom_left
    }

    fun shrinkBottomLeftOut() = apply {
        this.animationOut = R.anim.shrink_out_bottom_left
    }

    fun expandBottomCenterIn() = apply {
        this.animationIn = R.anim.expand_in_bottom_center
    }

    fun shrinkBottomCenterOut() = apply {
        this.animationOut = R.anim.shrink_out_bottom_center
    }

    fun expandBottomRightIn() = apply {
        this.animationIn = R.anim.expand_in_bottom_right
    }

    fun shrinkBottomRightOut() = apply {
        this.animationOut = R.anim.shrink_out_bottom_right
    }

    internal fun throwError(exception: Exception) {
        exceptionCallback?.invoke(exception)
    }

    fun navigate() {
        try {
            val act = activity ?: throw NavigationException.NullActivityException("Current activity is null!")
            if (act.isFinishing || act.isDestroyed) {
                throw NavigationException.ActivityDetachedException("Current activity is destroyed!")
            }
            if (isNavigating) return
            isNavigating = true
            doPrepare(act)
            doIntercept()
            isNavigating = false
        } catch (e: Exception) {
            throwError(e)
        }
    }

    private fun doPrepare(activity: Activity) {
        if (scheme.isNullOrEmpty()) {
            if (intent.action.isNullOrEmpty()) {
                prepareForActivity(activity)
            }
            // 隐式跳转不需要准备
        } else {
            prepareForScheme()
        }
    }

    private fun prepareForActivity(activity: Activity) {
        val realHost = host
        val realPath = path
        if (realHost.isNullOrEmpty()) {
            throw NavigationException.NullTargetException("invalid host: $realHost")
        }
        if (realPath.isNullOrEmpty()) {
            throw NavigationException.NullTargetException("invalid path: $realHost")
        }
        navigationBean = ENavigation.getNavigationBean(realHost, realPath).also {
            intent.component = ComponentName(activity, it.target.java)
        }
    }

    private fun prepareForScheme() {
        intent.action = Intent.ACTION_VIEW
        if (scheme.isNullOrEmpty()) {
            throw  NavigationException.NullTargetException("Target scheme is empty: $scheme")
        } else {
            intent.data = Uri.parse(scheme)
        }
    }

    private fun doIntercept() {
        // 因为拦截器是在ui线程执行，这里使用UncaughtExceptionHandler捕获异常
        Looper.getMainLooper().thread.uncaughtExceptionHandler = NavigateUncaughtExceptionHandler(::throwError)
        val bean = navigationBean
        val interceptors = mutableListOf<INavigationInterceptor>()
        // 全局拦截器
        interceptors.addAll(ENavigation.getGlobalInterceptors())
        // 本次跳转手动添加的拦截器
        interceptors.addAll(this.interceptors)
        if (bean != null) {
            // 注解添加的拦截器，此时NavigationBean一定不能为空
            interceptors.addAll(bean.classInterceptors)
            interceptors.addAll(bean.stringInterceptors.mapNotNull { ENavigation.getInterceptorByName(it) })
        }
        // 最后一个拦截器，执行跳转
        interceptors.add(NavigateInterceptor())
        val chain = InterceptorChain(interceptors, 0, this)
        chain.proceed(this)
    }
}