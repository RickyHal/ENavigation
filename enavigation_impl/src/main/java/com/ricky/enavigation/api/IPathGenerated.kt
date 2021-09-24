package com.ricky.enavigation.api

import com.ricky.enavigation.bean.NavigationBean
import java.util.*

/**
 *
 * @author haiyanghou
 * @date 2021/9/16
 */
interface IPathGenerated {
    val host: String
    fun getPathMap(): HashMap<String, NavigationBean>
}