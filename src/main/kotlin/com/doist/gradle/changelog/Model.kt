package com.doist.gradle.changelog

data class ValidatorConfig(
    var ignorePart: String? = null,
    var maxLength: Int = 0,
    var endsWithDot: Boolean = false,
    var doesNotEndWithDot: Boolean = false
)

data class CommitConfig(
    var prefix: String? = null,
    var postfix: String? = null,
    var entryPrefix: String? = null,
    var entryPostfix: String? = null,
    var insertAtLine: Int = 0
)
