import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


buildscript {
    val _kotlin_version = "1.2.40"

    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin", _kotlin_version))
    }

}

subprojects {

    group = "pt.um.tf.lab2"
    version = "1.0-SNAPSHOT"

    apply {
        plugin("kotlin")
        plugin("application")
    }

    ext {
        set("spread_version", "4.4.0")
        set("kotlin_version", "1.2.40")
        set("catalyst_version", "1.2.1")
        set("ekit_version", "1.2-SNAPSHOT")
    }

    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.10"
    }
}

