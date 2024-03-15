plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "es.ucm.fdi.boxit"
    compileSdk = 34

    defaultConfig {
        applicationId = "es.ucm.fdi.boxit"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "33.0.2"
}



dependencies {

    implementation ("com.spotify.android:auth:1.2.5")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    // implementation("com.google.android.gms:play-services-ads:22.5.0")

    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.github.bumptech.glide:glide:4.13.0")

    //implementation(files("libs/spotify-auth-release-2.1.0.aar"))
    //implementation(files("libs/spotify-auth-store-release-2.1.0.aar"))
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.code.gson:gson:2.6.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")


}