plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinAllopen)
    alias(libs.plugins.ksp)
    alias(libs.plugins.shadow)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.detekt)
    application
}

group = "com.tonihacks"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.kotlin)

    // Core
    implementation(libs.temporal)

    // Logging
    implementation(libs.logging)
    implementation(libs.logback.classic)

    // Testing
    testImplementation(libs.kotest)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
}

tasks {
    // Test configuration
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("skipped", "failed")
        }
        jvmArgs(
            "-XX:+EnableDynamicAgentLoading",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "-Xshare:off"
        )
    }

    // Shadow JAR configuration
    shadowJar {
        archiveBaseName.set("temporal-poc")
        archiveClassifier.set("")
        archiveVersion.set("")
        mergeServiceFiles()
    }

    // Build configuration
    build {
        dependsOn(shadowJar)
    }

    // Code formatting
    named("check") {
        dependsOn("ktfmtFormat")
        dependsOn("detekt")
    }

    // Disable unnecessary Java compilation
    named("compileJava") {
        enabled = false
    }
    named("compileTestJava") {
        enabled = false
    }
}

detekt {
    parallel = true
}

// Code style configuration
ktfmt {
    kotlinLangStyle()
}

kotlin {
    jvmToolchain(21)
}

