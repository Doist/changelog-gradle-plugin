package com.doist.gradle.changelog

import java.io.File
import java.io.RandomAccessFile
import java.util.LinkedList

internal class ChangelogProcessor(private val config: CommitConfig) {
    private val lineSeparator = System.lineSeparator()

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

        val changelog = LinkedList(changelogFile.readLines())
        var position = config.insertAtLine

        config.prefix?.let { changelog.add(position++, it) }

        val entryPrefix = config.entryPrefix ?: ""
        val entryPostfix = config.entryPostfix ?: ""
        entries.sorted().forEach { changelog.add(position++, entryPrefix + it + entryPostfix) }

        config.postfix?.let { changelog.add(position++, it) }

        if (changelogFile.endsWithEmptyLine()) {
            changelog.add("")
        }

        changelogFile.writeText(changelog.joinToString(lineSeparator))
    }

    fun removePendingEntries(file: File) {
        file.listFiles()?.forEach { it.deleteRecursively() }
    }

    private fun File.endsWithEmptyLine(): Boolean {
        RandomAccessFile(this, "r").use { handler ->
            val fileLength = handler.length()
            if (fileLength == 0L) return true
            handler.seek(fileLength - 1)
            val lastChar = handler.readByte().toChar()
            return lastChar == '\n'
        }
    }
}
