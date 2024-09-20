// build.gradle (Module: app)

plugins {
    id("com.google.gms.google-services") // Plugin de Google Services para Firebase
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.mx.votodigitalmx"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mx.votodigitalmx"
        minSdk = 24
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
}

dependencies {
    // Dependencias básicas de la app
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Importar Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // Agregar las dependencias de Firebase que quieras usar
    implementation("com.google.firebase:firebase-analytics")
    // Ejemplo: implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation ("com.google.firebase:firebase-firestore:24.0.0")

}
