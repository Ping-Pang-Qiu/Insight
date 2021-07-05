package com.ak.framework.util.ioc

/**
 * 控制反转
 */
object AkIoC {

    /**
     * 查找[AkImpl]标记的接口实现，使用无参构造函数，创建实例
     */
    fun <T> createImpl(service: Class<T>): T? {
        return null
    }
}