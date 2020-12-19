package com.doist.gradle.changelog

import java.io.File

internal class ChangelogProcessor(private val config: CommitConfig) {

    fun collectPendingEntries(file: File): List<String> {
        return file.walk()
            .filter { it.isFile }
            .map { it.readLines() }
            .flatten()
            .filter { it.isNotBlank() }
            .toList()
    }

    fun insertEntries(changelogFile: File, entries: List<String>) {
        if (entries.isEmpty()) return

        val changelog = changelogFile.readLines().toMutableList()
        val prefix = config.prefix?.plus("\n") ?: ""
        val postfix = config.postfix?.plus("\n") ?: ""
        val entryPrefix = config.entryPrefix ?: ""
        val entryPostfix = config.entryPostfix ?: ""
        val entriesBlock = entries.joinToString("\n", prefix, postfix) {
            entryPrefix + it + entryPostfix
        }
        changelog.add(config.insertAtLine, entriesBlock)
        changelogFile.writeText(changelog.joinToString("\n"))
    }

    fun removePendingEntries(file: File) {
        file.listFiles()?.forEach { it.deleteRecursively() }
    }
}
