package com.ricky.base

import androidx.fragment.app.FragmentActivity
import com.ricky.enavigation.ENavigation

/**
 *
 * @author RickyHal
 * @date 2021/9/17
 */
object NavigateTest {
    /** 跳转至类不可见的Activity [com.ricky.module2.TestActivity3] */
    fun navigateToInVisibleClassPage(activity: FragmentActivity) {
        ENavigation.with(activity).setHostAndPath(PathConfig.Module2.Test3.PATH).navigate()
    }
}