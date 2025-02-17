plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(projects.shared.appresult)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
}