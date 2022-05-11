plugins {
    kotlin("multiplatform") version Versions.KOTLIN
    id("maven-publish")
    signing
    id("org.jetbrains.dokka") version Versions.DOKKA
    id("io.codearte.nexus-staging") version Versions.NEXUS_STAGING
}

group = "io.github.jan-tennert.dnsplugin"
version = "1.0"

repositories {
    mavenCentral()
}

repositories {
    mavenCentral()
}

nexusStaging {
    stagingProfileId = Publishing.PROFILE_ID
    stagingRepositoryId.set(Publishing.REPOSITORY_ID)
    username = Publishing.SONATYPE_USERNAME
    password = Publishing.SONATYPE_PASSWORD
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
}

signing {
    val signingKey = providers
        .environmentVariable("GPG_SIGNING_KEY")
        .forUseAtConfigurationTime()
    val signingPassphrase = providers
        .environmentVariable("GPG_SIGNING_PASSPHRASE")
        .forUseAtConfigurationTime()

    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
        val extension = extensions
            .getByName("publishing") as PublishingExtension
        sign(extension.publications)
    }
}

publishing {
    repositories {
        maven {
            name = "Oss"
            setUrl {
                "https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/${Publishing.REPOSITORY_ID}"
            }
            credentials {
                username = Publishing.SONATYPE_USERNAME
                password = Publishing.SONATYPE_PASSWORD
            }
        }
        maven {
            name = "Snapshot"
            setUrl { "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
            credentials {
                username = Publishing.SONATYPE_USERNAME
                password = Publishing.SONATYPE_PASSWORD
            }
        }
    }
//val dokkaOutputDir = "H:/Programming/Other/DiscordKMDocs"
    val dokkaOutputDir = "$buildDir/dokka/${name}"

    tasks.dokkaHtml {
        outputDirectory.set(file(dokkaOutputDir))
    }

    val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
        delete(dokkaOutputDir)
    }

    val javadocJar = tasks.register<Jar>("javadocJar") {
        dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaOutputDir)
    }

    publications {
        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("DnsPlugin")
                description.set("A Ktor plugin for retrieving dns records when requesting")
                url.set("https://github.com/jan-tennert/DnsPlugin")
                licenses {
                    license {
                        name.set("GPL-3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/jan-tennert/DnsPlugin/issues")
                }
                scm {
                    connection.set("https://github.com/jan-tennert/DnsPlugin.git")
                    url.set("https://github.com/jan-tennert/DnsPlugin")
                }
                developers {
                    developer {
                        name.set("TheRealJanGER")
                        email.set("jan.m.tennert@gmail.com")
                    }
                }
            }
        }
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
        val commonMain by getting {
            dependencies {
                //implement ktor core
                implementation("io.ktor:ktor-client-core:${Versions.KTOR}")
                implementation("io.github.reactivecircus.cache4k:cache4k:${Versions.CACHE4K}")
            }
        }
        val commonTest by getting
        val jvmMain by getting {
            dependencies {
                implementation("org.minidns:minidns-hla:${Versions.MINIDNS}")
            }
        }
        val jvmTest by getting
    }
}
