package com.ricky.enavigation.core

import androidx.fragment.app.FragmentActivity
import com.ricky.enavigation.api.INavigationInterceptor
import com.ricky.enavigation.error.NavigationException

/**
 *
 * @author RickyHal
 * @date 2021/9/17
 */
class NavigateInterceptor : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        val request = chain.request
        when (val act = request.activity) {
            is FragmentActivity -> {
                val fragmentManager = act.supportFragmentManager
                var fragment = fragmentManager.findFragmentByTag(DelegateFragment.TAG)
                val delegateFragment: DelegateFragment = if (fragment != null) {
                    fragment as DelegateFragment
                } else {
                    fragment = DelegateFragment()
                    fragmentManager.beginTransaction().add(fragment, DelegateFragment.TAG).commitNowAllowingStateLoss()
                    fragment
                }
                delegateFragment.doNavigate(act, request)
            }
            else -> request.throwError(NavigationException.InvalidActivityException("Current activity is not FragmentActivity!"))
        }
    }
}