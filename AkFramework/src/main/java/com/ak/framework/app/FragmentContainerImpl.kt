package com.ak.framework.app

import androidx.fragment.app.FragmentManager
import com.ak.framework.util.AkLog
import java.util.*

class FragmentContainerImpl(
    private val mActivity: AkActivity,
    private val mFragmentContainerId: Int,
    private val mFragmentManager: FragmentManager
) : AkFragmentContainer {
    private companion object {
        private const val TAG = "FragmentContainerImpl"
    }

    private var mFragmentStack: LinkedList<AkFragment> = LinkedList()
    private var mCurrentFragment: AkFragment? = null

    override fun startFragment(fragment: AkFragment) {
        if (mFragmentManager.isStateSaved) {
            AkLog.t(TAG).e("startFragment can not be invoked after onSaveInstanceState")
            return
        }

        val curFragment = mCurrentFragment
        if (curFragment == null) {
            mCurrentFragment = fragment
            mFragmentManager
                .beginTransaction()
                .add(mFragmentContainerId, fragment)
                .commit()
            return
        }

        val transaction = mFragmentManager.beginTransaction()
        transaction.hide(curFragment)
        mFragmentStack.push(curFragment)
        if (fragment.isAdded) {
            if (fragment.activity == mActivity) {
                transaction.show(fragment)
            } else {
                throw IllegalStateException("fragment has added to another activity")
            }
        } else {
            transaction.add(mFragmentContainerId, fragment)
        }

        mCurrentFragment = fragment
        transaction.commit()
    }

    private fun popLastFragment(): Boolean {
        val curFragment = mCurrentFragment
        if (mFragmentStack.size == 0 || curFragment == null) {
            return false
        }

        val fragment = mFragmentStack.pop()
        val transaction = mFragmentManager.beginTransaction()
        transaction.remove(curFragment)
        if (fragment.isAdded) {
            if (fragment.activity == mActivity) {
                transaction.show(fragment)
            } else {
                throw IllegalStateException("fragment has added to another activity")
            }
        } else {
            transaction.add(mFragmentContainerId, fragment)
        }

        mCurrentFragment = fragment
        transaction.commit()
        return true
    }

    override fun onBackPressed(): Boolean {
        return popLastFragment()
    }
}