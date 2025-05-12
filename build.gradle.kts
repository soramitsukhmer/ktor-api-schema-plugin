val project_group: String by project
val project_version: String by project
val jackson_version: String by project
val smiley4_version: String by project
val hibernate_validator_version: String by project
val glassfish_jakarta_el_version: String by project
val libphonenumber_version: String by project

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
    implementation("io.ktor:ktor-server-content-negotiation-jvm")

    // Serialization
    implementation("io.ktor:ktor-serialization-jackson")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")

    // OpenAPI
    implementation("io.github.smiley4:ktor-openapi:$smiley4_version")
    implementation("io.github.smiley4:ktor-swagger-ui:$smiley4_version")
    implementation("io.github.smiley4:ktor-redoc:$smiley4_version")

    // Request validating
    api("org.hibernate:hibernate-validator:$hibernate_validator_version")
    implementation("org.glassfish:jakarta.el:$glassfish_jakarta_el_version")
    implementation("com.googlecode.libphonenumber:libphonenumber:$libphonenumber_version")

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
            url = uri("https://maven.pkg.github.com/soramitsukhmer/ktor-api-schema-plugin")
            credentials {
                username = System.getenv("GIT_PUBLISH_USER")
                password = System.getenv("GIT_PUBLISH_PASSWORD")
            }
        }
    }
}
