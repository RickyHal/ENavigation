package com.ricky.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ricky.base.BaseApplication
import com.ricky.base.NavigateTest
import com.ricky.base.PathConfig
import com.ricky.demo.databinding.ActivityMainBinding
import com.ricky.enavigation.ENavigation
import com.ricky.enavigation.api.anno.HostAndPathAnno
import com.ricky.enavigation.core.NavigationRequest
import com.ricky.enavigation.error.NavigationException

@HostAndPathAnno(PathConfig.App.Main.PATH)
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var isUsingWrappedAnim = true
    private var animGroupCheckedId = -1
    private var animInCheckedId = -1
    private var animOutCheckedId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.activity = this
        binding.sc7.setOnCheckedChangeListener { _, isChecked -> BaseApplication.application.isEnableGlobalInterceptor = isChecked }
        animGroupCheckedId = binding.animFadeIn.id
        binding.animGroup.setOnCheckedChangeListener { _, checkedId ->
            isUsingWrappedAnim = true
            animGroupCheckedId = checkedId
        }
        binding.animInGroup.setOnCheckedChangeListener { _, checkedId ->
            isUsingWrappedAnim = false
            animInCheckedId = checkedId
            if (animOutCheckedId == -1) binding.animFadeOut.isChecked = true
        }
        binding.animOutGroup.setOnCheckedChangeListener { _, checkedId ->
            isUsingWrappedAnim = false
            animOutCheckedId = checkedId
            if (animInCheckedId == -1) binding.animFadeIn.isChecked = true
        }
    }

    /**
     * 同模块
     * [com.ricky.demo.TestActivity1]
     * */
    fun onNavigate1Click() {
        ENavigation.with(this).setHostAndPath(PathConfig.App.Test1.PATH).navigate()
    }

    /**
     * 不同模块，但类可见
     * [com.ricky.module1.TestActivity2]
     * */
    fun onNavigate2Click() {
        ENavigation.with(this).setHostAndPath(PathConfig.Module1.Test2.PATH).navigate()
    }

    /**
     * 不同模块，且类不可见[com.ricky.module2.TestActivity3]
     * */
    fun onNavigate3Click() {
        NavigateTest.navigateToInVisibleClassPage(this)
    }

    /**
     * 不依赖Activity跳转
     * */
    fun onNavigate4Click() {
        ENavigation.with().setHostAndPath(PathConfig.Module1.Test2.PATH).navigate()
    }

    /**
     * 子线程跳转
     */
    fun onNavigate5Click() {
        Thread {
            ENavigation.with().setHostAndPath(PathConfig.Module1.Test2.PATH).navigate()
        }.start()
    }

    /**
     * 传递参数
     */
    fun onBringDataClick() {
        ENavigation.with(this)
            .setHostAndPath(PathConfig.Module1.Test4.PATH)
            .putString(PathConfig.Module1.Test4.KEY_TEXT, binding.etText.text.toString())
            .navigate()
    }

    /**
     * 数据回传
     */
    fun onDataCallbackClick() {
        ENavigation.with(this)
            .setHostAndPath(PathConfig.Module1.Test5.PATH)
            .onResult { requestCode, resultCode, data ->
                val text = data?.getStringExtra(PathConfig.KEY_RESULT)
                Toast.makeText(this, "requestCode=$requestCode\nresultCode=$resultCode\n收到回传的值：$text", Toast.LENGTH_SHORT).show()
            }
            .navigate()
    }

    /**
     * 跳转前后监听
     */
    fun onActionListenerClick() {
        var time: Long = System.currentTimeMillis()
        ENavigation.with(this)
            .setHostAndPath(PathConfig.Module1.Test4.PATH)
            .beforeAction {
                time = System.currentTimeMillis()
                Toast.makeText(this, "跳转之前，time=${time}", Toast.LENGTH_SHORT).show()
            }
            .afterAction {
                Toast.makeText(this, "跳转之后，耗时：${System.currentTimeMillis() - time}ms", Toast.LENGTH_SHORT).show()
            }
            .navigate()
    }

    /**
     * 注解拦截器
     */
    fun onAnnoInterceptorClick() {
        ENavigation.with(this)
            .setHostAndPath(PathConfig.Module1.Test6.PATH)
            .navigate()
    }

    /**
     * 运行时添加拦截器
     */
    fun onAddInterceptorClick() {
        ENavigation.with(this)
            .setHostAndPath(PathConfig.App.Test1.PATH)
            .setInterceptors(DynamicAddedInterceptor())
            .navigate()
    }

    /**
     * 跳转动画
     */
    fun onNavigateAnimClick() {
        ENavigation.with(this)
            .setHostAndPath(PathConfig.App.Test1.PATH)
            .apply {
                if (isUsingWrappedAnim) {
                    setupAnim(this)
                } else {
                    setupInAnim(this)
                    setupOutAnim(this)
                }
            }
            .navigate()
    }

    /**
     * 内部scheme跳转
     */
    fun onInsideSchemeClick() {
        ENavigation.with(this)
            .setScheme("enavigation://test")
            .onError {
                it.printStackTrace()
            }
            .navigate()

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
    }

    /**
     * 外部scheme跳转
     */
    fun onOutsideSchemeClick() {
        ENavigation.with(this)
            .setScheme(binding.etScheme.text.toString())
            .navigate()
    }

    /**
     * 系统界面跳转
     */
    fun onChooseImageClick() {
        ENavigation.with(this)
            .setAction(Intent.ACTION_GET_CONTENT)
            .setType("image/*")
            .onResult { _, _, data ->
                Toast.makeText(this, "uri=${data?.data}", Toast.LENGTH_SHORT).show()
            }
            .navigate()
    }

    private fun setupAnim(request: NavigationRequest) {
        when (binding.animGroup.checkedRadioButtonId) {
            R.id.anim_fade -> request.fade()
            R.id.anim_expand -> request.expand()
            R.id.anim_right -> request.right()
            R.id.anim_top -> request.top()
            R.id.anim_bottom -> request.bottom()
            R.id.anim_left -> request.left()
        }
    }

    private fun setupInAnim(request: NavigationRequest) {
        when (binding.animInGroup.checkedRadioButtonId) {
            R.id.anim_fade_in -> request.fadeIn()
            R.id.anim_left_in -> request.leftIn()
            R.id.anim_top_in -> request.topIn()
            R.id.anim_right_in -> request.rightIn()
            R.id.anim_bottom_in -> request.bottomIn()
            R.id.anim_expand_top_left_in -> request.expandTopLeftIn()
            R.id.anim_expand_top_center_in -> request.expandTopCenterIn()
            R.id.anim_expand_top_right_in -> request.expandTopRightIn()
            R.id.anim_expand_center_left_in -> request.expandCenterLeftIn()
            R.id.anim_expand_center_in -> request.expandCenterIn()
            R.id.anim_expand_center_right_in -> request.expandCenterRightIn()
            R.id.anim_expand_bottom_left_in -> request.expandBottomLeftIn()
            R.id.anim_expand_bottom_center_in -> request.expandBottomCenterIn()
            R.id.anim_expand_bottom_right_in -> request.expandBottomRightIn()
        }
    }

    private fun setupOutAnim(request: NavigationRequest) {
        when (binding.animOutGroup.checkedRadioButtonId) {
            R.id.anim_fade_out -> request.fadeOut()
            R.id.anim_left_out -> request.leftOut()
            R.id.anim_top_out -> request.topOut()
            R.id.anim_right_out -> request.rightOut()
            R.id.anim_bottom_out -> request.bottomOut()
            R.id.anim_shrink_top_left_out -> request.shrinkTopLeftOut()
            R.id.anim_shrink_top_center_out -> request.shrinkTopCenterOut()
            R.id.anim_shrink_top_right_out -> request.shrinkTopRightOut()
            R.id.anim_shrink_center_left_out -> request.shrinkCenterLeftOut()
            R.id.anim_shrink_center_out -> request.shrinkCenterOut()
            R.id.anim_shrink_center_right_out -> request.shrinkCenterRightOut()
            R.id.anim_shrink_bottom_left_out -> request.shrinkBottomLeftOut()
            R.id.anim_shrink_bottom_center_out -> request.shrinkBottomCenterOut()
            R.id.anim_shrink_bottom_right_out -> request.shrinkBottomRightOut()
        }
    }
}
