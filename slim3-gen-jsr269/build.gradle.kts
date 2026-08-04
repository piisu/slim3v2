description = "slim3-gen-jsr269"

val appengineApiVersion: String by project
val antVersion: String by project
val junitVersion: String by project

repositories {
    mavenCentral()
    maven { url = uri("https://maven.seasar.org/maven2") }
    maven { url = uri("https://maven.seasar.org/maven2-snapshot") }
}

dependencies {
    implementation("org.apache.ant:ant:$antVersion")
    implementation(project(":slim3"))
    testImplementation("com.google.appengine:appengine-api-1.0-sdk:$appengineApiVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.seasar.aptina:aptina-commons:1.0.0")
    testImplementation("org.seasar.aptina:aptina-unit:1.0.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "slim3-gen-jsr269"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
