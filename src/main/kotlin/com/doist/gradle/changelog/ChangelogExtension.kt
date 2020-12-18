package com.doist.gradle.changelog

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty

@Suppress("unused", "UnstableApiUsage")
open class ChangelogExtension(private val project: Project) {

    internal val validatorConfig = ValidatorConfig()

    internal val commitConfig = CommitConfig()

    val pendingChangelogDir: DirectoryProperty =
        project.objects.directoryProperty().convention(
            project.layout.projectDirectory.dir("changelog")
        )

    val changelogFile: RegularFileProperty =
        project.objects.fileProperty().convention { project.file("changelog.md") }

    fun validator(config: ValidatorConfig.() -> Unit) {
        project.configure(validatorConfig, closureOf(config))
    }

    fun validator(closure: Closure<*>) {
        project.configure(validatorConfig, closure)
    }

    fun commit(config: CommitConfig.() -> Unit) {
        project.configure(commitConfig, closureOf(config))
    }

    fun commit(closure: Closure<*>) {
        project.configure(commitConfig, closure)
    }
}
