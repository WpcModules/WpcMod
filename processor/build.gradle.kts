plugins {
	kotlin("jvm")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.google.devtools.ksp:symbol-processing-api:2.3.6")
}

kotlin {
	jvmToolchain(25)
}