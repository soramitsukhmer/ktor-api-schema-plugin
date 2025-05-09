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
        enable = true
    }

    redoc {
        enable = true
    }
}
```

### Usage
```
// Get Method
get(this, "/profile") { auth: UserAuth -> service.profile(auth) }

// Post Method
post(this) { request: UserBodyCreateReq -> service.save(request) }

// Put Method
put(this, "/profile") { auth: UserAuth, requestBody: UserBodyUpdateReq -> service.save(auth.id, requestBody) }
```

### Expose Default Endpoint:
- [Json data](http://0.0.0.0:8080/api/v1/schema)
- [Swagger-UI](http://0.0.0.0:8080/api/v1/schema/swagger)
- [Redoc](http://0.0.0.0:8080/api/v1/schema/redoc)