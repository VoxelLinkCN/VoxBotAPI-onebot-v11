import org.jreleaser.model.Active
import org.jreleaser.model.Signing.Mode

plugins {
    id("java")
    id("maven-publish")
    id("org.jreleaser") version "1.20.0"
}

group = "dev.voxellink.api"
version = "1.3.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.java-websocket:Java-WebSocket:1.6.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.json:json:20250517")
    implementation("org.slf4j:slf4j-api:2.0.17")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    compileOnly("org.jetbrains:annotations:24.0.1")
    annotationProcessor("org.jetbrains:annotations:24.0.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])

            pom {
                name = project.name
                description = "A library used to link to some bot client"
                url = "https://github.com/VoxelLinkCN/VoxBotAPI-onebot-v11"
                inceptionYear = "2025"
                licenses {
                    license {
                        name = "LGPL-3.0-or-later"
                        url = "https://spdx.org/licenses/LGPL-3.0-or-later.html"
                    }
                }
                developers {
                    developer {
                        id = "aurelian2842"
                        name = "Aurelian2842"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/VoxelLinkCN/VoxBotAPI-onebot-v11.git"
                    developerConnection = "scm:git:ssh://github.com/VoxelLinkCN/VoxBotAPI-onebot-v11.git"
                    url = "http://github.com/VoxelLinkCN/VoxBotAPI-onebot-v11"
                }
            }
        }
    }

    repositories {
        maven {
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

tasks.publish {
    dependsOn(tasks.named("publishMavenPublicationToMavenLocal"))
}

jreleaser {
    signing {
        active = Active.ALWAYS
        armored = true
        mode = Mode.MEMORY
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}
