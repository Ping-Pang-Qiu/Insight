package com.ak.framework.task

class AkThread : Thread {

    constructor(name: String) : this(name, null)

    constructor(name: String, runnable: Runnable?) : this(name, runnable, null, 0)

    constructor(name: String, runnable: Runnable?, group: ThreadGroup?, stackSize: Long) : super(
        group,
        runnable,
        name,
        stackSize
    )
}