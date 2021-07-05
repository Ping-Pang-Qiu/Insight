package com.ak.framework.developer

import java.lang.RuntimeException

class AkDevelopException : RuntimeException {
    constructor(msg: String) : super(msg)
}