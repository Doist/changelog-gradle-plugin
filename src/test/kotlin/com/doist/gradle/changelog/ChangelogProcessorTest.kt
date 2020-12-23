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
}
