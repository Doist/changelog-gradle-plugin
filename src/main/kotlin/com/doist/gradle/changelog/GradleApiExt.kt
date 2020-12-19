package com.doist.gradle.changelog

import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import java.io.File

internal fun DirectoryProperty.asPendingChangelogDir(): File? {
    val dir = asFile.orNull?.takeIf { it.exists() } ?: return null

    if (!dir.isDirectory) throw GradleException("${dir.path} is not a directory.")

    return dir
}
