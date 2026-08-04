import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

fun Project.stringProperty(name: String): String = property(name).toString()

fun Node.childText(name: String): String {
    val values = get(name) as NodeList
    return (values[0] as Node).text()
}

allprojects {
    apply(plugin = "maven-publish")
    group = "org.slim3"
    version = "2.5.0-SNAPSHOT1"
}

subprojects {
    apply(plugin = "java")

    val javaLanguageVersion = stringProperty("javaLanguageVersion").toInt()

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaLanguageVersion))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(javaLanguageVersion)
    }

    tasks.withType<Test>().configureEach {
        systemProperty("file.encoding", "UTF-8")
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://maven.seasar.org/maven2") }
        maven { url = uri("https://maven.seasar.org/maven2-snapshot") }
    }

    extensions.configure<PublishingExtension>("publishing") {
        repositories {
            val settingsXml =
                XmlParser().parse(File(System.getProperty("user.home"), ".m2/settings.xml"))
            val servers = settingsXml.get("servers") as NodeList
            val serverNodes = (servers[0] as Node).get("server") as NodeList
            val ghServerConfig =
                serverNodes
                    .map { it as Node }
                    .first { it.childText("id") == "piisu.github.io" }

            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/piisu/slim3v2")
                credentials {
                    username = ghServerConfig.childText("username")
                    password = ghServerConfig.childText("password")
                }
            }
        }
    }
}
