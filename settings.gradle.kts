pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    // AGP 9.x resolution strategy
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android.")) {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            if (requested.id.id == "org.jetbrains.kotlin.android") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Maven Central for Ktor, Coil, Koin, Markwon
    }
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "CYSTEM"
include(":core", ":data", ":app")
