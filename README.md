# ENavigation
一个使用kotlin封装的路由框架，适用于组件化开发场景，目前支持路由自动注册，拦截器，子线程跳转，跳转动画等功能。<br/>ENavigaiton可以用来跳转Activity，内外部Scheme，系统界面等等。

### 跳转流程

![未命名文件 (1).jpg](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/036f863c4f8a4cf58265e351edf860f8~tplv-k3u1fbpfcp-watermark.image?)

# 解决场景
组件化开发时，需要跳转到指定的Activity，但是很多时候，目标Activity类又是不可见的，不能直接通过startActivity跳转，ENavigation便是解决这一场景的方案。另外，很多时候我们访问一个页面，需要先判断用户是否有访问该页面的权限，此时通常的做法就是在跳转之前做一个权限判断，但是如此这般，项目中就会多出很多重复的权限判断代码，使用ENavigation的拦截器便能很好的解决这一问题，只需要定义一个拦截器类即可。

# 依赖配置

首先需要在使用到ENavigation的模块的build.gradle文件中配置host:
```groovy
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'
    id 'com.ricky.enavigation.plugin'
}
android {
    compileSdk 30
    ...
}
kapt {
    arguments {
        arg("host", "app")
    }
}

dependencies {
    ...
    // 核心库
    implementation project(path: ':enavigation_impl')
    kapt project(path: ':enavigation_complier')
}
```

# 使用说明
## 一、初始化
使用ENavigation前请务必先初始化，建议初始化代码放在Application中，初始化方式：
```kotlin
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 自动注册组件，注意需要在app模块添加自动注册插件
        ENavigation.init(this)
        // 手动注册组件，需要传入每个模块的build.gradle中配置的host
        ENavigation.init(this,"base","app","module1","module1")
    }
}
```
自动注册和手动注册选择其中一个即可，注意如果要使用自动注册，需要在app模块的build.gradle文件中apply自动注册插件：
```groovy
plugins {
    id 'com.ricky.enavigation.plugin'
}
// 或者
apply plugin: 'com.ricky.enavigation.plugin'
```
如果报找不到插件，请检查项目的build.gradle中是否添加了插件依赖：
```groovy
buildscript {
    ...
    dependencies {
        classpath "com.ricky.enavigation.plugin:ENavigationPlugin:$lateast_plugin_version"
    }
    
    ...
}

```
## 二、基础跳转
使用路由跳转时需要在目标Activity上添加@HostAndPathAnno注解：
```kotlin
@HostAndPathAnno("app/test")
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)
    }
}
```
这里的***app/test1***即是TestActivity的路由地址，之后便可通过这个地址跳转到TestActivity。<br>注意第一个单词代表host，必须与Activity所在模块的build.gradle中配置的host一致，第二个单词可根据Activity用途随便配置。
### 同模块跳转
MainActivity跳转到TestActivity，两个Activity都在app模块。
```kotlin
ENavigation.with(activity)
           .setHostAndPath(“app/test”）
           .navigate()
```
### 跨模块跳转
* 目标Activity可见
MainActivity跳转到TestActivity1，TestActivity1在module1模块，app模块引用module1模块，因此TestActivity1类对MainActivity可见。
```kotlin
ENavigation.with(activity)
           .setHostAndPath(“app/test1”）
           .navigate()
```
* 目标Activity不可见
MainActivity跳转到TestActivity2，TestActivity2在module2模块，app模块引用base和module2模块，TestActivity2类对base模块不可见，在base模块中执行跳转。
```kotlin
ENavigation.with(activity)
           .setHostAndPath(“app/test1”）
           .navigate()
```
### 子线程跳转
因为ENavigation真正执行跳转是在拦截器中执行的，而拦截器又会转移到UI线程执行，所以在子线程中也能实现跳转界面。
```kotlin
Thread{
    ENavigation.with(activity)
               .setHostAndPath(“app/test1”）
               .navigate()
}.start()
```
但是通常在子线程中是拿不到或者说不好拿activity的，此时不传入activity，也能实现跳转：
```kotlin
Thread{
    ENavigation.with()
               .setHostAndPath(“app/test1”）
               .navigate()
}.start()
```
### Scheme跳转
直接通过setScheme方法设置跳转的scheme即可
```kotlin
// 内部scheme
ENavigation.with(activity)
    .setScheme("enavigation://test")
    .navigate()
   
// 外部scheme
ENavigation.with(activity)
    .setScheme("taobao://item?id=97896794126846128")
    .navigate()
```
支持内部app和外部app的scheme。
### 系统界面跳转
这里举个跳转系统相册选照片的例子
```kotlin
ENavigation.with(activity)
    .setAction(Intent.ACTION_GET_CONTENT)
    .setType("image/*")
    .onResult { _, _, data ->
        // 通过onResult回调取到返回的值，这里下面会讲到
        Toast.makeText(this, "uri=${data?.data}", Toast.LENGTH_SHORT).show()
    }
    .navigate()
```
直接设置action和type即可，ENavigation还封装了其它诸如setData，setFlags，setCategories等方法，其作用于内部的intent，在跳转时传入startActivity方法。
### 跳转监听
ENavigation支持跳转前后的监听，方便执行相关操作。
```kotlin
ENavigation.with(activity)
    .setHostAndPath(PathConfig.Module1.Test4.PATH)
    .beforeAction { activity ->
      // 跳转页面之前
    }
    .afterAction { activity ->
      // 跳转页面之后
    }
    .navigate()
```
注意目前这两个回调都是在UI线程中执行的。
## 三、参数传递
### 参数传入
ENavigation封装了一系列设置传入参数的方法，其都作用于内部的intent，具体方法可参考系统地Intent类。这里举个跳转页面传字符串的例子。
```kotlin
ENavigation.with(activity)
    .setHostAndPath("app/test")
    .putString("text", "hello world")
    .navigate()
```
上面向***app/test***这个路由对应的Activity传入了一个key为text，value为hello world的值。取值的方式和通过intent传值一样。
### 数据回传
可通过封装的onResult回调取到上个页面回传的值。
```kotlin
ENavigation.with(activity)
    .setHostAndPath("app/test")
    .onResult { requestCode, resultCode, data ->
        val text = data?.getStringExtra("result")
        Toast.makeText(this, "requestCode=$requestCode\nresultCode=$resultCode\n收到回传的值：$text", Toast.LENGTH_SHORT).show()
    }
    .navigate()
```
## 四、拦截器
拦截器作用于目标Activity，可用于目标Activity的权限限制，如某些页面需要登录后才能访问，此时就可以使用拦截器来实现这一功能。
### 全局拦截器
全局拦截器作用于所有的页面跳转，实现全局拦截器需要自定义一个类，继承自INavigationInterceptor，重写intercept方法，然后给自定义的类加上@GlobalInterceptorAnno注解。
```kotlin
@GlobalInterceptorAnno
class MyGlobalInterceptor : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        if(isLogin){
            // 如果不拦截，执行chain.proceed
            chain.proceed(chain.request) 
        }
    }
}
```
如果有多个全局拦截器，可以设置拦截器的优先级
```kotlin
@GlobalInterceptorAnno(priority = 1)
class MyGlobalInterceptor : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        if(isLogin){
            // 如果不拦截，执行chain.proceed
            chain.proceed(chain.request) 
        }
    }
}
```
priority的值越大，优先级越高。
### 注解拦截器
注解拦截器作用于单个页面跳转，同样需要自定义一个拦截器类
```kotlin
@InterceptorAnno(name = "app/test_interceptor")
class TestInterceptor1 : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        Toast.makeText(chain.request.activity, "拦截器1", Toast.LENGTH_SHORT).show()
        chain.proceed(chain.request)
    }
}
```
同路由地址一样，拦截器也需要给一个name，第一个单词对应host，第二个对应名称。当然也可以不使用注解，直接定义
```kotlin
class TestInterceptor2 : INavigationInterceptor {
    override fun intercept(chain: INavigationInterceptor.Chain) {
        Toast.makeText(chain.request.activity, "拦截器2", Toast.LENGTH_SHORT).show()
        chain.proceed(chain.request)
    }
}
```
之后需要在目标Activity的注解上添加拦截器
```kotlin
@HostAndPathAnno(
    PathConfig.Module1.Test6.PATH,
    interceptors = [TestInterceptor2::class],
    interceptorNames = ["app/test_interceptor1"],
)
class TestActivity6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test6)
    }
}
```
可以通过interceptors或者interceptorNames的方式为目标Activity添加拦截器。注意通过interceptors设置的拦截器类不需要添加@InterceptorAnno注解，否则就会出现拦截两次的情况。
### 动态添加拦截器
也可以在跳转时添加拦截器
```kotlin
ENavigation.with(activity)
    .setHostAndPath("app/test")
    .setInterceptors(TestInterceptor())
    .navigate()
```
通过setInterceptors设置即可，如果需要添加多个
```kotlin
ENavigation.with(activity)
    .setHostAndPath("app/test")
    .setInterceptors(TestInterceptor1(), TestInterceptor2(), TestInterceptor3())
    .navigate()
```
跳转时添加的拦截器类同样不需要添加@InterceptorAnno注解。
## 五、跳转动画
ENavigation封装了一系列的跳转动画，举个Activity从下面弹出的例子
```kotlin
ENavigation.with(this)
    .setHostAndPath("app/test")
    .bottom()
    .navigate()
```
跳转动画方法说明
| 方法名 | 入场 | 出场 |
| --- | --- | --- |
| fade() | 渐显 | 渐隐 |
| top() | 上面进入 | 渐隐 |
| right() | 右边进入 | 渐隐 |
| bottom() | 底部进入 | 渐隐 |
| left() | 左边进入 | 渐隐 |
| fadeIn() | 渐显 | - |
| topIn() | 上面进入 | - |
| rightIn() | 右边进入 | - |
| bottomIn() | 底部进入 | - |
| leftIn() | 左边进入 | - |
| expandTopLeftIn() | 左上角展开 | - |
| expandTopCenterIn() | 顶部展开 | - |
| expandTopRightIn() | 右上角展开 | - |
| expandCenterLeftIn() | 左边展开 | - |
| expandCenterIn() | 中间展开 | - |
| expandCenterRightIn() | 右边展开 | - |
| expandBottomLeftIn() | 左下角展开 | - |
| expandBottomCenterIn() | 下面展开 | - |
| expandBottomRightIn() | 右下角展开 | - |
| fadeOut() | - | 渐隐 |
| topOut() | - | 顶部退出 |
| rightOut() | - | 右边退出 |
| bottomOut() | - | 下面退出 |
| leftOut() | - | 左边退出 |
| shrinkTopLeftOut() | - | 右上角退出 |
| shrinkTopCenterOut() | - | 上面退出 |
| shrinkTopRightOut() | - | 右上角退出 |
| shrinkCenterLeftOut() | - | 左边退出 |
| shrinkCenterOut() | - | 中间退出 |
| shrinkCenterRightOut() | - | 右边退出 |
| shrinkBottomLeftOut() | - | 左下角退出 |
| shrinkBottomCenterOut() | - | 下面退出 |
| shrinkBottomRightOut() | - | 右下角退出 |

当然也可以使用自定义的动画

```kotlin
ENavigation.with(this)
    .setHostAndPath("app/test")
    .animateIn(R.anim.custom_in)
    .animateOut(R.anim.custom_out)
    .navigate()
```
animateIn作用于跳转的Activity，animateOut作用于当前Activity。
## 六、异常处理
路由跳转常见的就是找不到路由的情况，再就是跳转的时候Activity已经销毁了，ENavigation封装了一些常见的异常，放在NavigationException类中

| 异常 | 说明 |
| --- | --- |
| NavigationException.ActivityDetachedException | Activity已经销毁 |
| NavigationException.NullTargetException | 路由或Scheme不存在 |
| NavigationException.NullActivityException | Activity为空，比如在Fragment中跳转 |
| NavigationException.InvalidActivityException | Activity只能为FragmentActivity |
| NavigationException.InvalidCodeException | resultCode != Activity.RESULT_OK |

通过onError回调监听异常
```kotlin
ENavigation.with(this)
    .setScheme("app/test")
    .onError { exception ->
        when (exception) {
            is NavigationException.ActivityDetachedException -> {
            }
            is NavigationException.NullActivityException -> {
            }
            is NavigationException.NullTargetException -> {
            }
            is NavigationException.InvalidCodeException -> {
            }
            is NavigationException.InvalidActivityException -> {
            }
            else -> {
                // 其它异常
            }
        }
    }
    .navigate()
```


## License

> ```
> Copyright 2021 RickyHal
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
> ```


## License

> ```
> Copyright 2021 RickyHal
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
> ```
