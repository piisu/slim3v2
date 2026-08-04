description = "Slim3"

val appengineApiVersion: String by project
val appengineLabsVersion: String by project
val appengineLocalRuntimeVersion: String by project
val servletApiVersion: String by project
val jspApiVersion: String by project
val junitVersion: String by project

repositories {
    mavenCentral()
    maven { url = uri("https://maven.seasar.org/maven2") }
    maven { url = uri("https://maven.seasar.org/maven2-snapshot") }
}

dependencies {
    implementation("com.google.appengine:appengine-api-1.0-sdk:$appengineApiVersion")

    compileOnly("com.google.appengine:appengine-api-stubs:$appengineApiVersion")
    testImplementation("com.google.appengine:appengine-api-stubs:$appengineApiVersion")

    compileOnly("com.google.appengine:appengine-testing:$appengineApiVersion")
    testImplementation("com.google.appengine:appengine-testing:$appengineApiVersion")

    compileOnly("com.google.appengine:appengine-api-labs:$appengineLabsVersion")
    testImplementation("com.google.appengine:appengine-api-labs:$appengineLabsVersion")

    testImplementation("com.google.appengine:appengine-local-runtime:$appengineLocalRuntimeVersion")

    compileOnly("javax.servlet:javax.servlet-api:$servletApiVersion")
    compileOnly("javax.servlet.jsp:javax.servlet.jsp-api:$jspApiVersion")

    compileOnly("junit:junit:$junitVersion")
    testImplementation("junit:junit:$junitVersion")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "slim3"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
