package com.doist.gradle.changelog

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask
import org.gradle.kotlin.dsl.property
import org.gradle.language.base.plugins.LifecycleBasePlugin

@Suppress("UnstableApiUsage")
abstract class ChangelogCheckTask : DefaultTask(), VerificationTask {
    init {
        description = "Checks pending changelog entries."
        group = "changelog"
    }

    @get:InputDirectory
    val pendingChangelogDir: DirectoryProperty = project.objects.directoryProperty()

    @get:Input
    val validatorConfig: Property<ValidatorConfig> = project.objects.property()

    @TaskAction
    fun checkChangelogEntries() {
        val changelogDir = pendingChangelogDir.asPendingChangelogDir() ?: return

        val validatorConfig = validatorConfig.get()
        val validator = FileValidator(EntryValidator(validatorConfig))

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

    /**
     * Returns for example: "Some entry" at changelog_entry.txt exceeds 5 characters limit.
     */
    private fun FileValidator.InvalidChangelogEntry.toErrorString(): String {
        val error = when (brokenRule) {
            is EntryValidator.Rule.MaxLength -> "exceeds ${brokenRule.limit} characters limit"
            is EntryValidator.Rule.EndsWithDot -> "does not end with a dot"
            is EntryValidator.Rule.DoesNotEndWithDot -> "ends with a dot"
        }

        return "e: ${file.absolutePath}: \"$entry\" $error."
    }
}
