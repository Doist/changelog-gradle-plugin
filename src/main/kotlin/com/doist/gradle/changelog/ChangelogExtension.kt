package com.doist.gradle.changelog

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.kotlin.dsl.closureOf

@Suppress("unused", "UnstableApiUsage")
open class ChangelogExtension(private val project: Project) {

    internal val rules = mutableListOf<Rule>()

    internal val commitConfig = CommitConfig()

    val pendingChangelogDir: DirectoryProperty =
        project.objects.directoryProperty().convention(
            project.layout.projectDirectory.dir("changelog")
        )

    val changelogFile: RegularFileProperty =
        project.objects.fileProperty().convention { project.file("changelog.md") }

    fun addRule(description: String, check: (String) -> Boolean) {
        rules.add(Rule(description, check))
    }

    fun addRule(description: String, check: Closure<Boolean>) {
        rules.add(Rule(description) { check.call(it) })
    }

    fun commit(config: CommitConfig.() -> Unit) {
        project.configure(commitConfig, closureOf(config))
    }

    fun commit(closure: Closure<*>) {
        project.configure(commitConfig, closure)
    }
}
