package com.ak.framework.util.os


object AkDevice {
    const val BRAND_UNKNOW: Int = -1
    const val BRAND_HUAWEI: Int = 1

    val brand: Int
    val brandName: String

    init {
        brand = initBrand()
        brandName = initBrandName(brand)
    }

    private fun initBrand(): Int {
        if (hasSystemProperty("ro.build.version.emui")) {
            return BRAND_HUAWEI
        }

        return BRAND_UNKNOW
    }

    private fun initBrandName(brand: Int): String {
        return when (brand) {
            BRAND_HUAWEI -> "huawei"
            else -> "unknow"
        }
    }

    private fun hasSystemProperty(key: String): Boolean {
        return getSystemProperty(key, null) != null
    }

    private fun getSystemProperty(key: String, defValue: String?): String? {
        try {
            val method = Class.forName("android.os.SystemProperties").getMethod(
                "get", String::class.java, String::class.java
            )
            return (method.invoke(null, key, defValue) as String)
        } catch (e: Exception) {
        }
        return defValue
    }
}