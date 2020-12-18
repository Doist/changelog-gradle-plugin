package com.doist.gradle.changelog

import java.io.File

internal class FileValidator(private val entryValidator: EntryValidator) {
    fun findInvalidEntriesIn(file: File): List<InvalidChangelogEntry> {
        val invalidChangelogEntries = mutableListOf<InvalidChangelogEntry>()
        for (entry in file.readLines()) {
            val result = entryValidator.validate(entry)
            if (result is EntryValidator.Result.Invalid) {
                result.brokenRules.forEach {
                    invalidChangelogEntries.add(InvalidChangelogEntry(file, entry, it))
                }
            }
        }

        return invalidChangelogEntries
    }

    data class InvalidChangelogEntry(
        val file: File,
        val entry: String,
        val brokenRule: EntryValidator.Rule
    )
}
