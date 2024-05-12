import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.devtools.ksp)
}

group = "com.chungchungdev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jansi)
    implementation(libs.okio)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jsoup)
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    // Logging
    implementation(libs.log4j2)
    implementation(libs.log4j2.slf4j.impl)
    implementation(libs.kotlin.logging)
    implementation(libs.jansi)
    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.ktor)
    testImplementation(libs.koin.test)
    // Test
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.kotlinx.datetime)
    testImplementation(libs.kotest.assertions.json)
    testImplementation(libs.kotest.property)
}

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
    compilerOptions.freeCompilerArgs.apply {
        add("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
        add("-opt-in=kotlin.contracts.ExperimentalContracts")
        add("-opt-in=kotlin.ExperimentalUnsignedTypes")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}