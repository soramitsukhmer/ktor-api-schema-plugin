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
    implementation("io.ktor:ktor-server-request-validation")
    implementation("io.ktor:ktor-server-auth-jwt")

    // Serialization
    implementation("io.ktor:ktor-serialization-jackson")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")

    // OpenAPI
    implementation("io.github.smiley4:ktor-openapi:5.0.2")
    implementation("io.github.smiley4:ktor-swagger-ui:5.0.2")
    implementation("io.github.smiley4:ktor-redoc:5.0.2")

    // Request validating
    api("org.hibernate:hibernate-validator:8.0.2.Final")
    implementation("org.glassfish:jakarta.el:5.0.0-M1")

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
