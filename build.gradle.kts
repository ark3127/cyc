// Top-level build file - AGP 9.x & Kotlin 2.4 Convention
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.ksp) apply false
}

allprojects {
    // Enforce JDK 21 Toolchain for all modules (AGP 9.x requirement for SDK 36)
    kotlin {
        jvmToolchain(21)
    }
    
    // Centralized JVM Target
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = libs.versions.jvmTarget.get()
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
