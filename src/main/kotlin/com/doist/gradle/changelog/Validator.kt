package com.doist.gradle.changelog

import java.io.File

internal class Validator(private val rules: List<Rule>) {
    fun findInvalidEntriesIn(file: File): List<InvalidEntry> {
        val invalidChangelogEntries = mutableListOf<InvalidEntry>()
        file.readLines().forEachIndexed { index, line ->
            rules.forEach { rule ->
                if (!rule.check(line)) {
                    invalidChangelogEntries.add(InvalidEntry(file, index, rule))
                }
            }
        }

        return invalidChangelogEntries
    }

    data class InvalidEntry(
        val file: File,
        val entryLine: Int,
        val brokenRule: Rule
    )
}
