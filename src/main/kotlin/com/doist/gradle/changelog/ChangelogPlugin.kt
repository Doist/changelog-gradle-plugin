package com.doist.gradle.changelog

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

@Suppress("unused")
class ChangelogPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val extension = extensions.create<ChangelogExtension>("changelog", this)

        tasks.register<ChangelogCheckTask>("checkChangelog") {
            pendingChangelogDir.set(extension.pendingChangelogDir)
            rules.set(extension.rules)
        }

        tasks.register<ChangelogCommitTask>("commitChangelog") {
            pendingChangelogDir.set(extension.pendingChangelogDir)
            changelogFile.set(extension.changelogFile)
            commitConfig.set(extension.commitConfig)
        }
    }
}
