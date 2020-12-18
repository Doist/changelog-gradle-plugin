package com.doist.gradle.changelog

import groovy.lang.Closure

internal class KotlinClosure<in T : Any?, V : Any>(
    val function: T.() -> V?,
    owner: Any? = null,
    thisObject: Any? = null
) : Closure<V?>(owner, thisObject) {

    @Suppress("unused") // to be called dynamically by Groovy
    fun doCall(it: T): V? = it.function()
}

internal fun <T> Any.closureOf(action: T.() -> Unit): Closure<Any?> =
    KotlinClosure(action, this, this)
