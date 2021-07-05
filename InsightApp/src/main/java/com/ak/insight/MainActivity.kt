package com.ak.insight

import com.ak.framework.app.AkActivity
import com.ak.framework.app.AkFragment
import com.ak.insight.fragment.HomeFragment

class MainActivity : AkActivity() {

    override val activityDefaultFragment: AkFragment = HomeFragment()
}