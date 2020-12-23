package com.doist.gradle.changelog

data class Rule(
    val name: String,
    val check: (String) -> Boolean
)

data class CommitConfig(
    var prefix: String? = null,
    var postfix: String? = null,
    var entryPrefix: String? = null,
    var entryPostfix: String? = null,
    var insertAtLine: Int = 0
)
