# Changelog Gradle plugin

This Gradle plugin adds tasks to manage changelog.

The expected changelog consists of two parts:
- pending changelog entries
- changelog file

Pending changelog entries are stored in a pending changelog folder, one entry per line. For example:
```text
ls pending-changelog
ft.feature_1.txt
ft.feature_2.txt
    
cat ft.feature_1.txt
Refactor sample class
Add feature 1

cat ft.feature_2.txt
Add feature 2
```

Changelog file is, for example:
```
# Changelog

## v15.0.1 - 2020-12-15
- Fix some bug
- Add some functionality

## v15.0.0 - 2020-12-10
- Refactor some class

...
```

## How to use it 👣

Include and configure the plugin in `build.gradle.kts` file:

```
plugins {
    id("com.doist.gradle.changelog") version "<latest-version>"
}

configure<ChangelogExtension> {
    pendingChangelogDir.set(project.file("pending-changelog"))
    changelogFile.set(project.file("CHANGELOG.md"))

    addRule("max length is 72 characters") { it.length <= 72 }
    addRule("cannot end with a dot") { !it.endsWith(".") }

    commit {
        val version = getVersion()
        val date = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        prefix = "## $version - $date"
        entryPrefix = "- "
        insertAtLine = 2
    }
}
```

You can now run:
```
./gradlew checkChangelog
```
This task reads every line in every file in the `pendingChangelogDir` folder and checks if the line
does not break any rule.

You can also run:
```
./gradlew commitChangelog
``` 
This task appends every line from every file in the `pendingChangelogDir` folder to the
`changelogFile` file. It uses the configuration specified in the `commit {...}` section. It also 
removes every file in the `pendingChangelogDir`. To avoid removing the folder itself, we recommend
putting a file named `.gitkeep.` in `pendingChangelogDir`.

## Configuration ⚙️

Available configuration is:
```
configure<ChangelogExtension> {
    // Changelog directory with pending entries.
    pendingChangelogDir.set(project.file("changelog"))
    
    // Files to ignore in pendingChangelogDir.
    ignoreFiles.set(listOf(".gitkeep"))
    
    // Changelog file.
    changelogFile.set(project.file("CHANGELOG.md"))
    
    // Fallback message when there are no pending changes.
    emptyChangelogMessage.set("No major changes")

    // Rules.
    addRule("max length is 72 characters") { it.length <= 72 }

    // commitChangelog task configuration. 
    commit {
        val version = getVersion()
        val date = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        
        // String to prepend to the changelog commit.
        prefix = "## $version - $date"
        
        // String to append to the changelog commit.
        postfix = "---"
        
        // String to prepend to every changelog entry.
        entryPrefix = "- "
        
        // String to append to every changelog entry.
        entryPostfix = ""
        
        // Position where the changelog entries are inserted. 
        insertAtLine = 2
    }
}
``` 

## Contributing 🤝

Feel free to open an issue or submit a pull request for any bugs/improvements.

## Acknowledgements 🙏
This plugin is based on [kotlin-gradle-plugin-template 🐘](https://github.com/cortinico/kotlin-gradle-plugin-template)
