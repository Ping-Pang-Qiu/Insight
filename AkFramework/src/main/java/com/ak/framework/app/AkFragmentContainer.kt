package com.ak.framework.app

interface AkFragmentContainer {

    fun onBackPressed(): Boolean

    fun startFragment(fragment: AkFragment)
}