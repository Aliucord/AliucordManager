val accompanistVersion = "0.24.13-rc"
val composeVersion = "1.3.0-alpha01"
val ktorVersion = "2.0.3"

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "1.7.0-1.0.6"
    kotlin("plugin.serialization") version "1.6.21"
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.aliucord.manager"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "0.0.1"

        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "TAG", "\"AliucordManager\"")
        buildConfigField("String", "SUPPORT_SERVER", "\"EsNDvBaHVU\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }

    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = "1.2.0"
}

dependencies {
    implementation(fileTree("./libs"))

    // core dependencies
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")
    implementation("com.android:zipflinger:7.4.0-alpha07")
//    implementation("com.github.Aliucord:libzip:1.0.0")

    // compose dependencies
    implementation("androidx.compose.ui:ui:${composeVersion}")
    implementation("androidx.compose.ui:ui-tooling:${composeVersion}")
    implementation("androidx.compose.material:material-icons-extended:${composeVersion}")
    implementation("androidx.compose.material3:material3:1.0.0-alpha14")
    implementation("androidx.paging:paging-compose:1.0.0-alpha15")

    // accompanist dependencies
    implementation("com.google.accompanist:accompanist-systemuicontroller:${accompanistVersion}")
    implementation("com.google.accompanist:accompanist-permissions:${accompanistVersion}")

    // compose destinations
    implementation("io.github.raamcosta.compose-destinations:core:1.6.12-beta")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.6.12-beta")

    // other dependencies
    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("de.upb.cs.swt:axml:2.1.2") // 2.1.2 is broken btw
    implementation("com.android.tools.build:apksig:7.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}
