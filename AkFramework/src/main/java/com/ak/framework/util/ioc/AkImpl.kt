package com.ak.framework.util.ioc

import java.lang.annotation.*
import java.lang.annotation.Retention
import java.lang.annotation.Target

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Documented
annotation class AkImpl