# Swagger integration

We have a swagger integration so that users can explore the server API through swagger-ui.

**Disclaimer**: This integration can be annoying to work with, it uses Java annotations which end up with lots of repetitive code.

Still, if you decide that swagger is worth for your project, this document explains the known quirks.

Some highlights:

- We are using [sbt-swagger-play](https://github.com/dwickern/sbt-swagger-play) which integrates a [swagger-play](https://github.com/dwickern/swagger-play) fork, while these modules are out of date, they work for most common scenarios.
- [Swagger-Annotations 1.5.x](https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X) are the supported annotations.
- This swagger version does not support cookie-based authentication, while this prevents you from specifying cookies at `SecurityDefinition`, you can still invoke the login endpoint from swagger-ui so that the cookie gets propagated by the browser.
- Swagger-ui is exposed locally at [localhost:9000/docs/index.html](http://localhost:9000/docs/index.html)
- Avoid adding quite a lot of swagger annotation changes at once, when there are problems with those, the error could not be obvious, you are encouraged to check swagger-ui after updating an API.
- Be sure to check existing [controllers](../server/src/main/scala/controllers/) to see real examples.
- Swagger belongs to the http layer, hence, swagger annotations must be only at the [controllers](../server/src/main/scala/controllers/) and [api-models](../lib/api/shared/src/main/scala/net/wiringbits/api/models/) packages.
- Returning arrays can be tricky, reflection notation is required.
- `Option[T]` values need explicit types so that swagger definition is accurate (the default inferred type is wrong).

**NOTE**: Swagger errors are not clear, check previous highlights, and, refresh swagger-ui frequently to make sure everything works the way you expect.

## Expose a controller
Controllers are not exposed by default, in order to do so, you need to annotate your controller with `@Api`, for example:

```scala
@Api("Auth")
class AuthController
```

In this case, `Auth` is the tag for the APIs exposed on `AuthController`, such tag is used to group the APIs on swagger-ui.

**NOTE**: swagger-play will match the controller methods to the endpoints defined at the [routes](../server/src/main/resources/routes) file.

## Controller authentication details
Any controller exposing APIs requiring user authentication must be annotated with `@SwaggerDefinition` to include the available security definitions, you will find yourself mostly writing this once and pasting it on all controllers, for example:

```scala
@SwaggerDefinition(
  securityDefinition = new SecurityDefinition(
    apiKeyAuthDefinitions = Array(
      new ApiKeyAuthDefinition(
        name = "Cookie",
        key = "auth_cookie",
        in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER,
        description =
          "The user's session cookie retrieved when logging into the app, invoke the login API to get the cookie stored in the browser"
      )
    )
  )
)
@Api("Auth")
class AuthController
```

This means that any endpoint can define the auth-definition key to mark the endpoint as protected, in this case `auth_cookie`, for example:

```scala
  @ApiOperation(
    value = "Logout from the app",
    notes = "Clears the session cookie that's stored securely",
    authorizations = Array(new Authorization(value = "auth_cookie"))
  )
  def logout = ???
```

## Controller methods
A controller method would usually look like this (removing pieces when necessary):

```scala
  @ApiOperation(
    value = "Logout from the app",
    notes = "Clears the session cookie that's stored securely",
    authorizations = Array(new Authorization(value = "auth_cookie"))
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON-encoded request",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[Logout.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Successful logout", response = classOf[Logout.Response]),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )
```

- `ApiOperation` defines the summary for the API, including authentication details when necessary.
- `ApiImplicitParams` defines the request body type (such class needs its own swagger-annotations).
- `ApiResponses` defines the potential responses for this method.


## Annotations on models

This is one of the most tricky side from this integration:

- We commonly use classes nested inside objects to define request/response models, we need to declare explicit swagger names for these models.
- Wrapper values get default weird values in swagger, for example, `Option[T]`, our typed models like `class Email private (val string: String) extends WrappedString`, hence, swagger needs an explicit type defined.
- Arrays get weird values too by default, reflection notation needs to be used for these.
- `ApiModel`/`ApiModelProperty` annotation parameters ordering matters! This is one of the more obscure details, if you get any weird error, check the annotation parameter ordering.
- Primitive values work fine.

Let's see an example:

```scala
object CreateUser {
  @ApiModel(value = "CreateUserRequest", description = "Request for the create user API")
  case class Request(
      @ApiModelProperty(dataType = "string")
      email: Email,
      @ApiModelProperty(dataType = "[Ljava.lang.Long;")
      longSeqOpt: Option[Seq[Long]],
      @ApiModelProperty(dataType = "[Ljava.lang.String;")
      stringSeq: Seq[String],
      @ApiModelProperty(dataType = "integer")
      intOpt: Option[Int],
      @ApiModelProperty(dataType = "boolean")
      booleanOpt: Option[Boolean],
      int: Int,
      boolean: Boolean,
      long: Long,
      string: String
  )
}
```