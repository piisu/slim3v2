import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardExtension
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath(
            "com.google.cloud.tools:appengine-gradle-plugin:" +
                project.property("appengineGradlePluginVersion")
        )
    }
}

plugins {
    java
    war
}

group = "com.example.appengine"
version = "1.0-SNAPSHOT"

val appengineApiVersion: String by project
val appengineLabsVersion: String by project
val appengineLocalRuntimeVersion: String by project
val junitVersion: String by project
val servletApiVersion: String by project

val appEngineGradlePluginCompatible =
    GradleVersion.current() < GradleVersion.version("9.0")

if (appEngineGradlePluginCompatible) {
    apply(plugin = "com.google.cloud.tools.appengine")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
    systemProperty("file.encoding", "UTF-8")
}

extra["slim3Version"] = "2.5.0-SNAPSHOT1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("javax.servlet:javax.servlet-api:$servletApiVersion")
    compileOnly("com.google.appengine:appengine-api-1.0-sdk:$appengineApiVersion")

    // Add your dependencies here.
    // implementation("com.google.appengine:appengine-endpoints:$appengineApiVersion")
    // implementation("com.google.appengine:appengine-endpoints-deps:$appengineApiVersion")
    implementation(fileTree("libs") { include("*.jar") })

    testImplementation("junit:junit:$junitVersion")
    testImplementation("com.google.appengine:appengine-local-runtime:$appengineLocalRuntimeVersion")
    testImplementation("com.google.appengine:appengine-api-stubs:$appengineApiVersion")
    testImplementation("com.google.appengine:appengine-api-1.0-sdk:$appengineApiVersion")
    testImplementation("com.google.appengine:appengine-api-labs:$appengineLabsVersion")

    testImplementation("com.google.appengine:appengine-testing:$appengineApiVersion")

    annotationProcessor(project(":slim3-gen-jsr269"))
    implementation(project(":slim3"))
    // apt("org.slim3:slim3-gen-jsr269:${extra["slim3Version"]}")
    // implementation("org.slim3:slim3:${extra["slim3Version"]}")
}

fun timestampVersion(): String =
    DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").format(LocalDateTime.now())

if (appEngineGradlePluginCompatible) {
    configure<AppEngineStandardExtension> {
        deploy {
            projectId = "slim3-blank"
            version = timestampVersion()
            stopPreviousVersion = true
            promote = true
        }
    }
}

// ant task
if (!fileTree("lib") { include("*.jar") }.isEmpty) {
    ant.importBuild("build.xml")
}
