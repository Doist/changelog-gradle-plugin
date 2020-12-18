package com.doist.gradle.changelog

import org.junit.Assert.assertEquals
import org.junit.Test

class EntryValidatorTest {
    @Test
    fun `max length rule`() {
        val validator = EntryValidator(ValidatorConfig(maxLength = 10))
        val result = validator.validate("Here is more than 10 characters")

        assert(result is EntryValidator.Result.Invalid)
        val invalidResult = result as EntryValidator.Result.Invalid
        assertEquals(listOf(EntryValidator.Rule.MaxLength(10)), invalidResult.brokenRules)
    }

    @Test
    fun `ends with a dot rule`() {
        val validator = EntryValidator(ValidatorConfig(endsWithDot = true))
        val result = validator.validate("This entry does not end with a dot")

        assert(result is EntryValidator.Result.Invalid)
        val invalidResult = result as EntryValidator.Result.Invalid
        assertEquals(listOf(EntryValidator.Rule.EndsWithDot), invalidResult.brokenRules)
    }

    @Test
    fun `does not end with a dot rule`() {
        val validator = EntryValidator(ValidatorConfig(doesNotEndWithDot = true))
        val result = validator.validate("This entry ends with a dot.")

        assert(result is EntryValidator.Result.Invalid)
        val invalidResult = result as EntryValidator.Result.Invalid
        assertEquals(listOf(EntryValidator.Rule.DoesNotEndWithDot), invalidResult.brokenRules)
    }

    @Test
    fun `max length rule with ignored parts`() {
        val validator = EntryValidator(
            ValidatorConfig(
                maxLength = 10,
                ignorePart = """ - \[.*]\(.*\)"""
            )
        )
        val result = validator.validate("Short - [ref](http://example.com)")

        assert(result is EntryValidator.Result.Valid)
    }
}
