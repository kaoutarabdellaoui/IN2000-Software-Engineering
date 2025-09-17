plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

android {
    namespace = "no.uio.ifi.in2000_gruppe3"
    compileSdk = 35

    defaultConfig {
        applicationId = "no.uio.ifi.in2000_gruppe3"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "MAPBOX_ACCESS_TOKEN",
            "\"${properties["MAPBOX_ACCESS_TOKEN"]}\""
        )
        buildConfigField(
            "String",
            "MAPBOX_SECRET_TOKEN",
            "\"${properties["MAPBOX_SECRET_TOKEN"]}\""
        )

        buildConfigField("String", "OPENAI_API_KEY_1", "\"${properties["OPENAI_API_KEY_1"]}\"")
        buildConfigField("String", "OPENAI_API_KEY_2", "\"${properties["OPENAI_API_KEY_2"]}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties",
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/*.md",
                "META-INF/*.txt",
                "META-INF/DEPENDENCIES"
            )
        }
    }

    testOptions {
        animationsDisabled = true
    }
}

dependencies {
    
    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-svg:2.4.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // Custom BottomSheet
    implementation("com.composables:core:1.20.1")

    // Ktor
    implementation("io.ktor:ktor-client-android:2.3.5")
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")

    // Ktx
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)

    // Mapbox
    implementation("com.mapbox.maps:android:11.10.2")
    implementation("com.mapbox.extension:maps-compose:11.10.2")

    // Mapbox search
    val mapboxSearchVersion = "2.8.0-rc.1"
    implementation("com.mapbox.search:autofill:$mapboxSearchVersion")
    implementation("com.mapbox.search:discover:$mapboxSearchVersion")
    implementation("com.mapbox.search:place-autocomplete:$mapboxSearchVersion")
    implementation("com.mapbox.search:offline:$mapboxSearchVersion")
    implementation("com.mapbox.search:mapbox-search-android:$mapboxSearchVersion")
    implementation("com.mapbox.search:mapbox-search-android-ui:$mapboxSearchVersion")

    // Markdown text
    implementation("com.github.jeziellago:compose-markdown:0.5.7")

    // Navigation and compose
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material:material:1.7.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.activity:activity-compose:1.8.2")

    // OpenAI
    implementation(group = "com.azure", name = "azure-ai-openai", version = "1.0.0-beta.3")
    implementation("org.slf4j:slf4j-simple:1.7.9")
    implementation("org.slf4j:slf4j-api:2.0.9")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Room database
    val roomVersion = "2.7.0-rc02"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Standard libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // For testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("org.slf4j:slf4j-nop:1.7.36")
    testImplementation("junit:junit:1.7.8")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4: 1.7.8")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(kotlin("test"))
    testImplementation(kotlin("test"))
}