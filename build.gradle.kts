plugins {
    // Update AGP to a version that supports Kotlin 2.1
    id("com.android.application") version "8.7.2" apply false

    // Update Kotlin to 2.1.0 (Matches the YouTube Library)
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false

    id("com.google.gms.google-services") version "4.4.2" apply false

    // Update KSP to the version matching Kotlin 2.1.0
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false

    // IMPORTANT: Add the new Compose Compiler plugin (Required for Kotlin 2.0+)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}