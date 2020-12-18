package com.doist.gradle.changelog

internal class EntryValidator(private val config: ValidatorConfig) {
    private val localValidators = buildLocalValidators()
    private val ignorePartRegex = config.ignorePart?.toRegex()

    fun validate(entry: String): Result {
        val entryWithoutIgnoredPart = ignorePartRegex?.let {
            entry.replace(ignorePartRegex, "")
        } ?: entry

        val brokenRules = localValidators
            .filterNot { it.isValid(entryWithoutIgnoredPart) }
            .map { it.rule }

        return when {
            brokenRules.isEmpty() -> Result.Valid
            else -> Result.Invalid(brokenRules)
        }
    }

    private fun buildLocalValidators(): List<LocalValidator> {
        val validators = mutableListOf<LocalValidator>()
        if (config.maxLength > 0) {
            validators.add(
                LocalValidator({ it.length <= config.maxLength }, Rule.MaxLength(config.maxLength))
            )
        }
        if (config.endsWithDot) {
            validators.add(LocalValidator({ it.endsWith(".") }, Rule.EndsWithDot))
        }
        if (config.doesNotEndWithDot) {
            validators.add(LocalValidator({ !it.endsWith(".") }, Rule.DoesNotEndWithDot))
        }

        return validators
    }

    sealed class Rule {
        data class MaxLength(val limit: Int) : Rule()
        object EndsWithDot : Rule()
        object DoesNotEndWithDot : Rule()
    }

    sealed class Result {
        object Valid : Result()
        data class Invalid(val brokenRules: List<Rule>) : Result()
    }

    private data class LocalValidator(
        val isValid: (String) -> Boolean,
        val rule: Rule
    )
}
