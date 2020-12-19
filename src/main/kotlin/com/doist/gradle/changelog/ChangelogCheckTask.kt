package com.doist.gradle.changelog

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask
import org.gradle.kotlin.dsl.listProperty

@Suppress("UnstableApiUsage")
abstract class ChangelogCheckTask : DefaultTask(), VerificationTask {
    init {
        description = "Checks pending changelog entries."
        group = "changelog"
    }

    @get:InputDirectory
    val pendingChangelogDir: DirectoryProperty = project.objects.directoryProperty()

    @get:Input
    val rules: ListProperty<Rule> = project.objects.listProperty()

    @TaskAction
    fun checkChangelogEntries() {
        val changelogDir = pendingChangelogDir.asPendingChangelogDir() ?: return

        val validator = Validator(rules.get())

        val invalidEntries = changelogDir
            .walk()
            .filter { it.isFile }
            .map { validator.findInvalidEntriesIn(it) }
            .flatten()
            .toList()

        if (invalidEntries.isNotEmpty()) {
            invalidEntries.forEach { logger.error(it.toErrorString()) }

            throw GradleException("Changelog entries check failed.")
        }
    }

    private fun Validator.InvalidEntry.toErrorString(): String {
        val path = file.absolutePath
        val line = entryLine + 1
        val rule = brokenRule.name
        return "e: $path: ($line, 0): breaks: $rule"
    }
}
