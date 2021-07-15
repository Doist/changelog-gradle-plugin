package com.doist.gradle.changelog

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask
import org.gradle.kotlin.dsl.property

@Suppress("UnstableApiUsage")
abstract class ChangelogCommitTask : DefaultTask(), VerificationTask {
    init {
        description = "Adds pending changelog entries into the changelog file."
        group = "changelog"
    }

    @get:Input
    val pendingChangelogDir: DirectoryProperty = project.objects.directoryProperty()

    @get:InputFile
    val changelogFile: RegularFileProperty = project.objects.fileProperty()

    @get:Input
    val commitConfig: Property<CommitConfig> = project.objects.property()

    @get:Input
    val emptyChangelogMessage: Property<String> = project.objects.property()

    @TaskAction
    fun commitChangelogEntries() {
        val pendingChangelogDir = pendingChangelogDir.get().asFile
        val changelogFile = changelogFile.get().asFile

        if (!changelogFile.canWrite()) {
            throw GradleException("Cannot modify ${changelogFile.name} file.")
        }

        val commitConfig = commitConfig.get()
        with(ChangelogProcessor(commitConfig)) {
            var pendingEntries = collectPendingEntries(pendingChangelogDir)
            if (pendingEntries.isEmpty()) {
                pendingEntries = listOf(emptyChangelogMessage.get())
            }
            insertEntries(changelogFile, pendingEntries)
            removePendingEntries(pendingChangelogDir)
        }
    }
}
