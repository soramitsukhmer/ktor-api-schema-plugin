# Ktor Api Schema Plugin
This guide will help you integrate the `Open Api Schema` Plugin into your service.

[#Smiley4](https://github.com/SMILEY4/ktor-openapi-tools)

### Add SoramitsuKhmer GitHub Repository
Add the following repository configuration to your `build.gradle.kts` file:

```
repositories {
    // Other repositories
    maven {
        name = "GitHubSoramitsuKhmerApacheMavenPackages"
        url = uri("https://maven.pkg.github.com/soramitsukhmer/*")
        credentials {
            username = System.getenv("GIT_PUBLISH_USER")
            password = System.getenv("GIT_PUBLISH_PASSWORD")
        }
    }
    // Other repositories
}
```

### Dependency
Add the following dependency to your `build.gradle.kts` file:
```
dependencies {
    // OpenApiShema
    implementation("com.skh.ktor:api-schema-plugin:$api_scheme_version")
}
```

### Install Plugin
```
install(ApiSchema) {
    info {
        title = "Ktor - Basic API"
        version = "1.0.0"
    }

    server {
        url = "http://0.0.0.0:8080"
        description = "This is the development server"
    }

    swagger {
        enabled = true
    }

    redoc {
        enabled = true
    }

    // Supported exception handler
    handler {
        config {
            exception<BadRequestException> { call, cause ->
                ...
            }
        }
    }
}
```

Provided security principle context
```
inline fun <reified T : Any> ApplicationCall.auth(): T { /* compiled code */ }
```

Supported request body validation
```
import jakarta.validation.constraints.NotBlank

data class Request(
    @field:NotBlank val field: String
    ...
)
```

### Usage [Inline function]
```
// Get Method
import me.learning.api_schema.route.inline.GET

GET { auth: UserAuth -> ... }


// Post Method
import me.learning.api_schema.route.inline.POST

POST { auth: UserAuth, request: Reauest -> ... }


// Put Method
import me.learning.api_schema.route.inline.PUT

PUT { auth: UserAuth, requestBody: UserBodyUpdateReq -> ... }
```

### Usage [Extension function]
```
// Get Method
import me.learning.api_schema.route.extension.GET

GET("/user").auth(UserAuth::class).map { auth -> ... }


// Post Method
import me.learning.api_schema.route.extension.POST

POST("/user").auth(UserAuth::class).map { auth -> ... }


// Put Method
import me.learning.api_schema.route.extension.PUT
import me.learning.api_schema.route.extension.core.map

PUT("/user/{id}").auth(UserAuth::class).pathVariable(Long::class).map { t2: Tuple2<UserAuth, Long> -> ... }
```

### Expose Default Endpoint:
- Json data: `/api/v1/schema`
- Swagger-UI: `/api/v1/schema/swagger`
- Redoc: `/api/v1/schema/redoc`