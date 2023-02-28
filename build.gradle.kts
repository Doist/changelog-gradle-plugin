import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version BuildPluginsVersion.KOTLIN
    id("com.gradle.plugin-publish") version BuildPluginsVersion.PLUGIN_PUBLISH
    id("io.gitlab.arturbosch.detekt") version BuildPluginsVersion.DETEKT
    id("org.jlleitschuh.gradle.ktlint") version BuildPluginsVersion.KTLINT
    id("com.github.ben-manes.versions") version BuildPluginsVersion.VERSIONS_PLUGIN
    `kotlin-dsl`
}

group = PluginCoordinates.GROUP
version = PluginCoordinates.VERSION

repositories {
    google()
    mavenCentral()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

gradlePlugin {
    website.set(PluginBundle.WEBSITE)
    vcsUrl.set(PluginBundle.VCS)

    plugins {
        create(PluginCoordinates.ID) {
            id = PluginCoordinates.ID
            implementationClass = PluginCoordinates.IMPLEMENTATION_CLASS
            version = PluginCoordinates.VERSION
            displayName = PluginCoordinates.DISPLAY_NAME
            description = PluginCoordinates.DESCRIPTION
            tags.set(PluginCoordinates.TAGS)
        }
    }
}

ktlint {
    debug.set(false)
    version.set(Versions.KTLINT)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

detekt {
    config = rootProject.files("config/detekt/detekt.yml")
    reports {
        html {
            enabled = true
            destination = file("build/reports/detekt.html")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))
    implementation(gradleApi())

    testImplementation(TestingLib.JUNIT)
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

fun isNonStable(version: String) = "^[0-9,.v-]+(-r)?$".toRegex().matches(version).not()

tasks.register("preMerge") {
    description = "Runs all the tests/verification tasks."

    dependsOn("check")
    dependsOn("validatePlugins")
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}
