object PluginCoordinates {
    const val ID = "com.doist.gradle.changelog"
    const val GROUP = "com.doist.gradle"
    const val VERSION = "0.1.0"
    const val IMPLEMENTATION_CLASS = "com.doist.gradle.changelog.ChangelogPlugin"
    const val DESCRIPTION = "Gradle plugin to manage changelog."
    const val DISPLAY_NAME = "Changelog plugin"
    val TAGS = setOf("changelog")
}

object PluginBundle {
    const val VCS = "https://github.com/Doist/changelog-gradle-plugin"
    const val WEBSITE = "https://github.com/Doist/changelog-gradle-plugin"
}
