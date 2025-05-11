# Ktor Api Schema Plugin
This guide will help you integrate the `Open Api Schema` Plugin into your service.

[#Smiley4](https://github.com/SMILEY4/ktor-openapi-tools)

### Dependency
```
dependencies {
    // OpenApiShema
    implementation("me.learning:api-plugin:$api_scheme_version")
}
```

### Install Plugin
```
install(OpenApiSchema) {
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
}
```

Provided security principle context:
```
inline fun <reified T : Any> ApplicationCall.auth(): T { /* compiled code */ }
```
Support request body validation
```
import jakarta.validation.constraints.NotBlank

data class Request(
    @field:NotBlank val field: String
    ...
)
```

### Usage
```
// Get Method
import me.learning.api_schema.route.methods.get
get { auth: UserAuth -> ... }


// Post Method
import me.learning.api_schema.route.methods.post
post { auth: UserAuth, request: Reauest -> ... }


// Put Method
import me.learning.api_schema.route.methods.put
put { auth: UserAuth, requestBody: UserBodyUpdateReq -> ... }
```

### Expose Default Endpoint:
- [Json data](http://0.0.0.0:8080/api/v1/schema)
- [Swagger-UI](http://0.0.0.0:8080/api/v1/schema/swagger)
- [Redoc](http://0.0.0.0:8080/api/v1/schema/redoc)