import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

lateinit var accessKey: String
lateinit var secretKey: String

android {
    namespace = "com.andrii_a.walleria"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.andrii_a.walleria"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val paginationPageSize = gradleLocalProperties(rootDir, providers).getProperty("pagination_page_size").toInt()
        buildConfigField("Integer", "PAGINATION_PAGE_SIZE", paginationPageSize.toString())
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            val gradleLocalProperties = gradleLocalProperties(rootDir, providers)

            accessKey = gradleLocalProperties.getProperty("unsplash_access_key_debug")
            buildConfigField("String", "UNSPLASH_ACCESS_KEY", accessKey)

            secretKey = gradleLocalProperties.getProperty("unsplash_secret_key_debug")
            buildConfigField("String", "UNSPLASH_SECRET_KEY", secretKey)
        }

        release {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val gradleLocalProperties = gradleLocalProperties(rootDir, providers)

            accessKey = gradleLocalProperties.getProperty("unsplash_access_key_release")
            buildConfigField("String", "UNSPLASH_ACCESS_KEY", accessKey)

            secretKey = gradleLocalProperties.getProperty("unsplash_secret_key_release")
            buildConfigField("String", "UNSPLASH_SECRET_KEY", secretKey)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.material3.adaptive.navigation.suite)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation + compose
    implementation(libs.androidx.navigation.compose)

    // Paging + compose
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Constraint layout
    implementation(libs.androidx.constraintlayout.compose)

    // Coil + compose
    implementation(libs.coil)
    implementation(libs.coil.compose.core)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.navigation)

    // Ktor client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Android splash screen
    implementation(libs.androidx.core.splashscreen)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Room DB
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Custom tabs
    implementation(libs.androidx.browser)

    implementation(libs.kotlinx.serialization.json)

    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)

    // Tests
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.koin.test.junit4)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}

tasks.withType<Test> {
    useJUnitPlatform()
}