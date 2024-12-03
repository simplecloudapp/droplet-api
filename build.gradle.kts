plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.sonatype.central.portal.publisher)
    `maven-publish`
}

val baseVersion = "0.0.30"
val commitHash = System.getenv("COMMIT_HASH")
val snapshotversion = "${baseVersion}-dev.$commitHash"

allprojects {
    group = "app.simplecloud.droplet.api"
    version = if (commitHash != null) snapshotversion else baseVersion

    repositories {
        mavenCentral()
        maven("https://buf.build/gen/maven")
    }
}

dependencies {
    testImplementation(rootProject.libs.kotlin.test)
    implementation(rootProject.libs.kotlin.jvm)
}

publishing {
    repositories {
        maven {
            name = "simplecloud"
            url = uri("https://repo.simplecloud.app/snapshots/")
            credentials {
                username = System.getenv("SIMPLECLOUD_USERNAME")?: (project.findProperty("simplecloudUsername") as? String)
                password = System.getenv("SIMPLECLOUD_PASSWORD")?: (project.findProperty("simplecloudPassword") as? String)
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(22))
}

kotlin {
    jvmToolchain(22)
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_22)
    }
}

tasks.test {
    useJUnitPlatform()
}

centralPortal {
    name = project.name

    username = project.findProperty("sonatypeUsername") as? String
    password = project.findProperty("sonatypePassword") as? String

    pom {
        name.set("SimpleCloud Droplet API")
        description.set("Commonly used classes among droplets/services")
        url.set("https://github.com/theSimpleCloud/droplet-api")
        developers {
            developer {
                id.set("dayyeeet")
                email.set("david@cappell.net")
            }
        }
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        scm {
            url.set("https://github.com/theSimpleCloud/droplet-api.git")
            connection.set("git:git@github.com:theSimpleCloud/droplet-api.git")
        }
    }
}

signing {
    if (commitHash != null) {
        return@signing
    }

    sign(publishing.publications)
    useGpgCmd()
}