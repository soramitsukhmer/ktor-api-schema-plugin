val jackson_version: String by project
val project_group: String by project
val project_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.1.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
    `maven-publish`
}

group = project_group
version = project_version

repositories {
    mavenCentral()
}

application {
    mainClass.set("$group.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}


dependencies {
    // KTOR Server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-cio-jvm")
    implementation("io.ktor:ktor-server-request-validation")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-auth")

//    // KTOR Client
//    implementation("io.ktor:ktor-client-resources")
//    implementation("io.ktor:ktor-client-core")
//    implementation("io.ktor:ktor-client-cio")
//    implementation("io.ktor:ktor-client-logging")
//    implementation("io.ktor:ktor-client-content-negotiation")

    // Serialization
    implementation("io.ktor:ktor-serialization-jackson")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")

    // OpenAPI
    implementation("io.github.smiley4:ktor-openapi:5.0.2")
    implementation("io.github.smiley4:ktor-swagger-ui:5.0.2")
    implementation("io.github.smiley4:ktor-redoc:5.0.2")
    implementation("io.ktor:ktor-server-core:3.1.2")
    implementation("io.ktor:ktor-server-openapi:3.1.2")
    implementation("io.ktor:ktor-server-core:3.1.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.properties["group"].toString()
            artifactId = project.name
            version = project.properties["version"].toString()
            from(components["kotlin"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/SokkunNORN/ktor-api-plugin")
            credentials {
                username = System.getenv("GIT_PUBLISH_USER")
                password = System.getenv("GIT_PUBLISH_PASSWORD")
            }
        }
    }
}
