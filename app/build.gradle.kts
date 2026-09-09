import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.ksp.plugin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.hilt.android.gradle.plugin)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.kotlin.dokka)
    id("org.jetbrains.kotlinx.kover")
}

android {
    namespace = "com.imecatro.demosales"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.imecatro.demosales"
        minSdk = 26
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 28
        versionName = "1.15.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        // Add your own apikey from GCP in local.properties
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY", "")
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    flavorDimensions += "distribution"

    productFlavors {
        create("google") {
            dimension = "distribution"
        }

        create("huawei") {
            dimension = "distribution"
            applicationIdSuffix = ".hw"
        }
    }
}

// Disable google-services plugin for huawei flavor
afterEvaluate {
    tasks.matching {
        it.name.contains("googleServices", ignoreCase = true) &&
                it.name.contains("huawei", ignoreCase = true)
    }.configureEach {
        enabled = false
    }

    tasks.matching {
        it.name.contains("crashlytics", ignoreCase = true) &&
                it.name.contains("huawei", ignoreCase = true)
    }.configureEach {
        enabled = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.browser)

    implementation(project(":demosales-data:products"))
    implementation(project(":demosales-data:sales"))
    implementation(project(":demosales-ui:products"))
    implementation(project(":demosales-ui:sales"))
    implementation(project(":demosales-ui:theme"))
    implementation(project(":demosales-domain:products"))
    implementation(project(":demosales-domain:sales"))
    implementation(project(":demosales-ui:clients"))
    implementation(project(":demosales-domain:clients"))
    implementation(project(":demosales-domain:core"))
    implementation(project(":demosales-data:clients"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4) // Use latest stable version

    //NAV
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //hilt for dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //Coil for images in compose
    implementation(libs.coil.compose)

    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    androidTestImplementation(libs.androidx.room.testing)

    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    add("googleImplementation", platform(libs.firebase.bom))
    add("googleImplementation", libs.firebase.analytics)
    add("googleImplementation", "com.google.firebase:firebase-crashlytics")
    add("googleImplementation", libs.firebase.storage)
    add("googleImplementation", libs.firebase.auth)
    add("googleImplementation", libs.firebase.config)
    add("googleImplementation", libs.firebase.firestore)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}

dokka {
    dokkaPublications.configureEach {
        suppressInheritedMembers.set(true)
    }

    dokkaSourceSets.configureEach {
        // Point to the markdown file for the home page (module/package documentation)
        includes.from(project.layout.projectDirectory.file("src/main/dokka/module.md"))
    }
}
