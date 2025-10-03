# Ktor Api Schema Plugin
This guide will help you integrate the `Open Api Schema` Plugin into your service.

[#ktor-openapi-tools](https://github.com/SMILEY4/ktor-openapi-tools)

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
    // open api schema
    implementation("com.skh.ktor:api-schema-plugin:$api_scheme_version")
}
```

### Install Plugin
```
install(ApiSchema) {
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

Support with json converter
```
data class Response(
    @field:JsonAlias("userId") val id: Long,
    ...
)

val response = Any.ct(Response::class.java)
```

### Usage [Inline function]
```
// Get Method
import com.skh.api_schema.route.inline.get
import com.skh.api_schema.dto.route.inline.impl.RequestBody

get { auth: Auth<UserAuth> -> ... }


// Post Method
import com.skh.api_schema.route.inline.post
import com.skh.api_schema.dto.route.inline.impl.RequestBody

post { auth: Auth<UserAuth>, requestBody: RequestBody<Reauest> -> ... }


// Put Method
import com.skh.api_schema.route.inline.put
import com.skh.api_schema.dto.route.inline.impl.RequestBody

put { auth: Auth<UserAuth>, requestBody: RequestBody<UserBodyUpdateReq> -> ... }
```

### Usage [Extension function]
```
// Get Method
import com.skh.api_schema.route.extension.get
import com.skh.api_schema.route.extension.core.map

get("/user").auth(UserAuth::class).map { auth -> ... }


// Post Method
import com.skh.api_schema.route.extension.post
import com.skh.api_schema.route.extension.core.map

post("/user").auth(UserAuth::class).map { auth -> ... }


// Put Method
import com.skh.api_schema.route.extension.put
import com.skh.api_schema.route.extension.core.map

put("/user/{id}").auth(UserAuth::class).pathVariable(Long::class).map { t2: Tuple2<UserAuth, Long> -> ... }
```

### Expose Default Endpoint:
- Json data: `/schema`
- Download: `/schema/download`
- Swagger-UI: `/schema/swagger`
- Redoc: `/schema/redoc`

### Config application.yml:
```
api-schema:
  enabled: true
  path: "schema"
  base-urls: "http://localhost:8080,https://api.example.com"
  max-file-size-mb: 100
  datetime-format: "MM/dd/yyyy HH:mm:ss"
  date-format: "MM/dd/yyyy"
  time-format: "HH:mm:ss"
  
  info:
    title: "My API"
    version: "1.0.0"
    description: "Comprehensive API documentation for My Application"
    summary: "Quick overview of the API"
  
  download:
    enabled: true
    path: "download"
    hidden-route: false
    filename: "api-schema"
  
  redoc:
    enabled: true
    path: "redoc"
  
  swagger:
    enabled: true
    path: "swagger"
```
