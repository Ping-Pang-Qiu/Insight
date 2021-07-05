package com.ak.framework.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.ak.framework.util.AkLog

abstract class AkFragment : Fragment() {
    companion object {
        private const val TAG = "AkFragment"

        fun createFragmentByClassName(clsName: String): AkFragment? {
            try {
                val firstFragmentClass = Class.forName(clsName) as Class<out AkFragment>
                return firstFragmentClass.newInstance()
            } catch (e: Exception) {
                AkLog.e(e)
            }

            return null
        }
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun onViewCreated(root: View)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun startFragment(fragment: AkFragment) {
        val activity = activity
        if (activity is AkActivity) {
            activity.getAkFragmentContainer().startFragment(fragment)
        }
    }

    fun onBackPressed(): Boolean {
        return false
    }
}