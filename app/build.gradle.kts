plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Firebase needs the project's own google-services.json. Until the owner has created the Firebase
// project the plugin is simply not applied, so the build stays green and AnalyticsManager logs
// nothing; dropping the file into app/ is the whole switch.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    namespace = "arcadia.com.confucius"
    compileSdk {
        // androidx.core 1.19.0 / lifecycle 2.11.0 require compileSdk >= 37, and every app pulling
        // arcadia-core :android must match it or checkDebugAarMetadata fails.
        version = release(37)
    }

    defaultConfig {
        applicationId = "arcadia.com.confucius"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Play measures "app optimisation" by whether the release really went through R8.
            // The keep rules for reflection (Play Billing, AdView) come from arcadia-core as
            // consumer-rules.pro and are not restated here.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        // Not optional, although there is no astronomy here: Practice decides which calendar day
        // a moment belongs to through java.time (the reader's local midnight, and the 23- and
        // 25-hour days a daylight-saving change makes), and minSdk is 24.
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    // No lint suppressions. Confucius posts an optional daily reminder when the reader switches it on, so it
    // declares POST_NOTIFICATIONS and satisfies the NotificationPermission check.
}

dependencies {
    constraints {
        // play-services-basement pulls androidx.fragment 1.1.0, which Play reports as outdated.
        // The app uses no fragments - this only raises the resolved version in the graph.
        implementation(libs.androidx.fragment) {
            because("play-services-basement pulls androidx.fragment 1.1.0, flagged as outdated by Play")
        }
    }

    // Roster and the rules of Practice.
    implementation("com.arcadia:core:1.0.0")
    // Premium, billing, ads, banner, widget, notifications, and the Practice store.
    implementation("com.arcadia:android:1.0.0")
    // Deliberately neither :astro nor :astrology: there is no celestial arithmetic in this app.
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.play.services.ads)
    implementation(libs.billing.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    testImplementation(libs.junit)
    // org.json in android.jar is a stub that throws; the real artifact is needed for JVM tests.
    testImplementation(libs.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
