package com.doist.gradle.changelog

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ChangelogProcessorTest {
    @get:Rule
    var testFolder = TemporaryFolder()

    @Test
    fun `inserting entries`() {
        val changelogFile = testFolder.newFile()
        val changelog = """
            # Changelog

            ## 0.0.1
            - Bug fix 1
            - Bug fix 2

        """.trimIndent()
        changelogFile.writeText(changelog)

        val changelogProcessor = ChangelogProcessor(
            CommitConfig(
                prefix = "## 0.0.2",
                postfix = "",
                entryPrefix = "- ",
                insertAtLine = 2
            )
        )

        changelogProcessor.insertEntries(changelogFile, listOf("Feature 1", "Feature 2"))

        val expectedChangelog = """
            # Changelog

            ## 0.0.2
            - Feature 1
            - Feature 2

            ## 0.0.1
            - Bug fix 1
            - Bug fix 2

        """.trimIndent()
        assertEquals(expectedChangelog, changelogFile.readText())
    }

    @Test
    fun `collect pending entries`() {
        testFolder.newFile("fix.something").apply { writeText("Fix something") }
        testFolder.newFile(".gitkeep").apply { writeText("Don't remove this file") }

        val changelogProcessor = ChangelogProcessor(CommitConfig())
        val entries = changelogProcessor.collectPendingEntries(testFolder.root, listOf(".gitkeep"))

        assertEquals(listOf("Fix something"), entries)
    }

    @Test
    fun `removing pending entries`() {
        val entry = testFolder.newFile("fix.something")
        val keep = testFolder.newFile(".gitkeep")
        entry.writeText("")
        keep.writeText("")

        val changelogProcessor = ChangelogProcessor(
            CommitConfig(
                prefix = "## 0.0.2",
                postfix = "",
                entryPrefix = "- ",
                insertAtLine = 2
            )
        )

        changelogProcessor.removePendingEntries(testFolder.root, listOf(".gitkeep"))

        assertEquals(listOf(".gitkeep"), testFolder.root.listFiles().map { it.name })
    }
}
