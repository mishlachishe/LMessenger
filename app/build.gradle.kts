plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.dagger.hilt.android")
	id("com.google.devtools.ksp")
	id("org.jetbrains.kotlin.plugin.compose")
	id("org.jetbrains.kotlin.plugin.serialization")
}

android {
	namespace = "ru.mishlachok.LMessageClient"
	compileSdk = 36

	defaultConfig {
		applicationId = "ru.mishlachok.LMessageClient"
		minSdk = 26
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables { useSupportLibrary = true }
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	kotlinOptions {
		jvmTarget = "17"
	}

	buildFeatures {
		compose = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.15"
	}
}

dependencies {
	implementation("androidx.compose.foundation:foundation:1.11.2")
	// Compose BOM – свежий, но совместимый
	val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
	implementation(composeBom)
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.compose.material:material-icons-extended")
	implementation("androidx.activity:activity-compose:1.9.3")
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
	implementation("com.google.android.material:material:1.11.0")
	// Navigation
	implementation("androidx.navigation:navigation-compose:2.8.5")

	// Hilt
	implementation("com.google.dagger:hilt-android:2.51.1")
	ksp("com.google.dagger:hilt-android-compiler:2.51.1")
	implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

	// Ktor Client
	implementation("io.ktor:ktor-client-android:2.3.8")
	implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
	implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
	implementation("io.ktor:ktor-client-auth:2.3.8")
	implementation("io.ktor:ktor-client-logging:2.3.8")

	// DataStore
	implementation("androidx.datastore:datastore-preferences:1.1.2")

	// Coil
	implementation("io.coil-kt:coil-compose:2.7.0")

	// Testing
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
	androidTestImplementation(composeBom)
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
	testImplementation("org.junit.platform:junit-platform-launcher")
}
tasks.withType<Test> {
	useJUnitPlatform()
}