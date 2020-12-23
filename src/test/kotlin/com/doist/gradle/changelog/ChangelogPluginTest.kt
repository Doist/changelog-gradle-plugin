package com.doist.gradle.changelog

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class ChangelogPluginTest {

    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.doist.gradle.changelog")

        assert(project.tasks.getByName("checkChangelog") is ChangelogCheckTask)
        assert(project.tasks.getByName("commitChangelog") is ChangelogCommitTask)
    }
}
