package com.ricky.enavigation.error

/**
 *
 * @author haiyanghou
 * @date 2021/9/23
 */

sealed class NavigationException(val code: Int, val exception: Exception) : Exception() {
    class ActivityDetachedException(message: String) : NavigationException(ErrorCode.CODE_ACTIVITY_DETACHED, IllegalStateException(message))
    class NullTargetException(message: String) : NavigationException(ErrorCode.CODE_NULL_TARGET, IllegalArgumentException(message))
    class NullActivityException(message: String) : NavigationException(ErrorCode.CODE_NULL_ACTIVITY, IllegalArgumentException(message))
    class InvalidActivityException(message: String) : NavigationException(ErrorCode.CODE_INVALID_ACTIVITY, IllegalArgumentException(message))
    class InvalidCodeException(message: String) : NavigationException(ErrorCode.CODE_INVALID_CODE, IllegalStateException(message))
}