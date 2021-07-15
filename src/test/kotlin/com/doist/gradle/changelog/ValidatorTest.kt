package com.doist.gradle.changelog

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ValidatorTest {
    @get:Rule
    var testFolder = TemporaryFolder()

    @Test
    fun validation() {
        val notTooLongRule = Rule("cannot be too long") { it.length <= 40 }
        val notTooShortRule = Rule("cannot be too short") { it.length > 5 }
        val noDotRule = Rule("should end with a dot") { it.endsWith(".") }
        val validator = Validator(listOf(notTooLongRule, notTooShortRule, noDotRule))

        val file = testFolder.newFile().apply {
            writeText(
                """
                    Here is an entry with more than 40 characters.
                    This entry does not end with a dot
                """.trimIndent()
            )
        }

        val result = validator.findInvalidEntriesIn(file)

        assertEquals(2, result.size)
        assertEquals(Validator.InvalidEntry(file, 0, notTooLongRule), result[0])
        assertEquals(Validator.InvalidEntry(file, 1, noDotRule), result[1])
    }
}
