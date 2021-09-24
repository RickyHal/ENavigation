package com.ricky.enavigation.support

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 *
 * @author haiyanghou
 * @date 2021/9/9
 */
class ActivityStackManager : Application.ActivityLifecycleCallbacks {
    private val activityStack: ArrayList<Activity> = arrayListOf()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStack.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityStack.remove(activity)
    }

    fun getCurrentActivity(): Activity {
        if (activityStack.isNotEmpty()) {
            return activityStack.last()
        } else {
            throw IllegalArgumentException("Current activity is null!")
        }
    }
}