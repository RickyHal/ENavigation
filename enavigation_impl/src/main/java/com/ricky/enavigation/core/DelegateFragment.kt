package com.ricky.enavigation.core

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ricky.enavigation.error.NavigationException
import com.ricky.enavigation.impl.R

/**
 *
 * @author RickyHal
 * @date 2021/9/9
 */
class DelegateFragment : Fragment() {
    companion object {
        const val TAG = "delegate_fragment"
    }

    private var throwErrorBlock: ((NavigationException) -> Unit)? = null
    private var onResult: ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)? = null
    private var currentRequestCode: Int = -1

    fun doNavigate(activity: FragmentActivity, request: NavigationRequest) {
        onResult = request.onResult
        currentRequestCode = request.requestCode
        throwErrorBlock = request::throwError
        request.beforeAction.invoke(activity)
        startActivityForResult(request.intent, request.requestCode)
        if (request.animationIn != -1 || request.animationOut != -1) {
            val animIn = if (request.animationIn == -1) R.anim.anim_no else request.animationIn
            val animOut = if (request.animationOut == -1) R.anim.anim_no else request.animationOut
            activity.overridePendingTransition(animIn, animOut)
        }
        request.afterAction.invoke(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == currentRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                onResult?.invoke(requestCode, resultCode, data)
            } else {
                throwErrorBlock?.invoke(NavigationException.InvalidCodeException("Invalid result code: $resultCode"))
            }
        } else {
            throwErrorBlock?.invoke(NavigationException.InvalidCodeException("Invalid request code: $requestCode"))
        }
        parentFragmentManager.transact {
            remove(this@DelegateFragment)
        }
    }

    private fun FragmentManager.transact(block: FragmentTransaction.() -> Unit) {
        beginTransaction().apply(block).commitAllowingStateLoss()
    }
}