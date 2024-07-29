plugins {
    id("com.android.application")
}

android {
    namespace = "com.fengyuegames.packager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fengyuegames.packager"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = "key0"
            keyPassword = "28p7e3tp"
            storeFile = file("../keystore.jks")
            storePassword = "28p7e3tp"
        }
    }

    buildTypes {
        release {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type. Make sure to use a build
            // variant with `isDebuggable=false`.
            isMinifyEnabled = true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            isShrinkResources = true

            // Includes the default ProGuard rules files that are packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.android.gms:play-services-ads:22.6.0")
    implementation ("com.github.lzyzsd:jsbridge:1.0.4")
    implementation ("com.google.code.gson:gson:2.10")
    implementation ("org.apache.commons:commons-lang3:3.7")
    implementation ("com.google.android.play:integrity:1.3.0")
    implementation ("androidx.core:core:1.10.1")

}