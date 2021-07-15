package com.doist.gradle.changelog

import java.io.File

internal class ChangelogProcessor(private val config: CommitConfig) {
    fun collectPendingEntries(file: File, ignoreFiles: List<String>): List<String> {
        return file.walk()
            .filter { it.isFile }
            .filter { it.name !in ignoreFiles }
            .map { it.readLines() }
            .flatten()
            .filter { it.isNotBlank() }
            .toList()
    }

    fun insertEntries(changelogFile: File, entries: List<String>) {
        if (entries.isEmpty()) return

        val changelog = changelogFile.readText()
        val lineSeparator = changelog.getLineSeparator()

        val entriesBlock = buildEntriesString(entries, lineSeparator)
        val newChangelog = changelog.insertAtLine(entriesBlock, config.insertAtLine)

        changelogFile.writeText(newChangelog)
    }

    fun insertMessage(changelogFile: File, message: String) {
        val changelog = changelogFile.readText()
        val lineSeparator = changelog.getLineSeparator()

        val entriesBlock = buildMessageString(message, lineSeparator)
        val newChangelog = changelog.insertAtLine(entriesBlock, config.insertAtLine)

        changelogFile.writeText(newChangelog)
    }

    private fun buildEntriesString(entries: List<String>, lineSeparator: String): String {
        val entryPrefix = config.entryPrefix ?: ""
        val entryPostfix = config.entryPostfix ?: ""

        return buildString {
            config.prefix?.let { append(it).append(lineSeparator) }
            entries.sorted().forEach {
                append(entryPrefix).append(it).append(entryPostfix).append(lineSeparator)
            }
            config.postfix?.let { append(it).append(lineSeparator) }
        }
    }

    private fun buildMessageString(message: String, lineSeparator: String): String {
        return buildString {
            config.prefix?.let { append(it).append(lineSeparator) }
            append(message).append(lineSeparator)
            config.postfix?.let { append(it).append(lineSeparator) }
        }
    }

    fun removePendingEntries(file: File, ignoreFiles: List<String>) {
        file.listFiles()
            ?.filter { it.name !in ignoreFiles }
            ?.forEach { it.deleteRecursively() }
    }

    private fun String.insertAtLine(text: String, line: Int): String {
        if (line == 0) return text + this

        var passedLines = 0
        val position = indexOfFirst { it == '\n' && ++passedLines == line } + 1
        return this.substring(0, position) + text + this.substring(position)
    }

    private fun String.getLineSeparator() =
        findAnyOf(listOf("\n", "\r\n"))?.second ?: System.lineSeparator()
}
