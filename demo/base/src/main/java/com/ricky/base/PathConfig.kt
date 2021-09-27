package com.ricky.base

/**
 *
 * @author RickyHal
 * @date 2021/9/17
 */
object PathConfig {
    const val KEY_RESULT = "result"

    object App {
        const val host = "app"

        object Main {
            const val PATH = "$host/main"
        }

        object Test1 {
            const val PATH = "$host/test1"
        }
    }

    object Module1 {
        const val host = "module1"

        object Test2 {
            const val PATH = "$host/test2"
        }

        object Test4 {
            const val PATH = "$host/test4"
            const val KEY_TEXT = "text"
        }

        object Test5 {
            const val PATH = "$host/test5"
        }

        object Test6 {
            const val PATH = "$host/test6"
            const val INTERCEPTOR1 = "$host/interceptor1"
            const val INTERCEPTOR2 = "$host/interceptor2"
        }
    }

    object Module2 {
        const val host = "module2"

        object Test3 {
            const val PATH = "$host/test3"
        }
    }
}