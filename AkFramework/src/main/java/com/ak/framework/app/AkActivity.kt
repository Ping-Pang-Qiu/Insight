package com.ak.framework.app

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.ak.framework.R
import com.ak.framework.developer.AkDeveloper
import com.ak.framework.developer.activity.ActivityMonitor
import com.ak.framework.util.os.AkBuildUtils
import com.ak.framework.widget.fitwindow.AkFitWindowFrameLayout

open class AkActivity : AppCompatActivity() {
    companion object {
        const val KEY_FIRST_FRAGMENT_CLASS_NAME = "ak_first_fragment_class_name"
    }

    // 全屏
    open val activityFullWindow: Boolean = false

    // 状态栏浅色模式
    open val activityLightStatusBar: Boolean = true

    // 默认fragment
    open val activityDefaultFragment: AkFragment? = null

    // 根布局
    private lateinit var mRootView: ViewGroup
    private lateinit var mFragmentContainer: AkFragmentContainer

    // 修复隐藏状态栏时，切换界面返回后状态栏又出现问题
    private var mSavedSystemUiVisibility: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRootView()
        setupSystemWindow()
        setupDevelopEnv()
        if (savedInstanceState == null) {
            setupFirstFragment()
        }
    }

    private fun setupRootView() {
        mRootView = AkFitWindowFrameLayout(this)
        mRootView.id = R.id.ak_activity_root_view_id
        super.setContentView(mRootView)
        mFragmentContainer = FragmentContainerImpl(this, mRootView.id, supportFragmentManager)
    }

    private fun setupSystemWindow() {
        if (activityFullWindow) {
            setFullscreenWindow()
        } else {
            translucentSystemStatusBar()
        }
        setStatusBarLight(activityLightStatusBar)
    }

    private fun setupDevelopEnv() {
        if (AkDeveloper.isEnable()) {
            Choreographer.getInstance().postFrameCallback { }
            window.decorView.viewTreeObserver.addOnDrawListener {
                ActivityMonitor.onDraw()
            }
        }
    }

    private fun setupFirstFragment() {
        val firstFragment = getFirstFragment()
        if (firstFragment != null) {
            mFragmentContainer.startFragment(firstFragment)
        }
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        LayoutInflater.from(this).inflate(layoutResID, mRootView, true)
    }

    override fun setContentView(view: View) {
        mRootView.addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (mSavedSystemUiVisibility != 0) {
            window.decorView.systemUiVisibility = mSavedSystemUiVisibility
        }
    }

    override fun onPause() {
        super.onPause()
        mSavedSystemUiVisibility = window.decorView.systemUiVisibility
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (AkDeveloper.isEnable()) {
            ActivityMonitor.onDispatchTouchEvent(this, event)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        if (mFragmentContainer.onBackPressed()) {
            return
        }
        return super.onBackPressed()
    }

    fun getAkFragmentContainer(): AkFragmentContainer {
        return mFragmentContainer
    }

    /**
     * 全屏
     */
    private fun setFullscreenWindow() {
        if (AkBuildUtils.AT_LEAST_28) {
            // 刘海屏
            val params = window.attributes
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = params
        }

        val decorView = window.decorView
        val systemUi: Int = decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = systemUi
    }

    /**
     * 状态栏透明
     */
    private fun translucentSystemStatusBar() {
        if (AkBuildUtils.AT_LEAST_28) {
            // 刘海屏
            val params = window.attributes
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = params
        }

        if (AkBuildUtils.AT_LEAST_21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }

        val decorView = window.decorView
        val systemUi: Int = decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        decorView.systemUiVisibility = systemUi
    }

    /**
     * 设置状态栏浅色模式
     * 浅色模式下状态栏字体显示为黑色
     */
    private fun setStatusBarLight(enable: Boolean) {
        val decorView = window.decorView
        var systemUi = decorView.systemUiVisibility
        systemUi = if (enable) {
            systemUi or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            systemUi and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }

        if (systemUi != decorView.systemUiVisibility) {
            decorView.systemUiVisibility = systemUi
        }
    }

    private fun getFirstFragment(): AkFragment? {
        if (activityDefaultFragment != null) {
            return activityDefaultFragment
        }

        val clsName: String? = intent.getStringExtra(KEY_FIRST_FRAGMENT_CLASS_NAME)
        if (clsName.isNullOrEmpty()) {
            return null
        }

        return AkFragment.createFragmentByClassName(clsName)
    }

    fun getRootView(): View {
        return mRootView
    }
}