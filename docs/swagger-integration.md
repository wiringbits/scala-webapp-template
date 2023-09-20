# Swagger integration

We have a swagger integration so that users can explore the server API through swagger-ui.

Some highlights:

- We are using [tapir](https://tapir.softwaremill.com/) which integrates an [open-api](https://tapir.softwaremill.com/en/latest/docs/openapi.html) module for swagger.
- Swagger-ui is exposed locally at [http://localhost:9000/docs](http://localhost:9000/docs).
- Be sure to check existing [endpoints](../lib/api/shared/src/main/scala/net/wiringbits/api/endpoints) to see real examples.
- `Option[T]` values are supported, sending a json without the key and value will be interpreted as `None`, otherwise, `Some(value)` will be sent.

## Creating an endpoint definition
We have to define our endpoints at the [endpoints](../lib/api/shared/src/main/scala/net/wiringbits/api/endpoints) package, for example:

```scala
val basicPostEndpoint = endpoint
  .post("basic") // points to POST http://localhost:9000/basic
  .tag("Misc") // tags the endpoint as "Misc" on swagger-ui
  .in(
    jsonBody[Basic.Request].example( // expects a JSON body of type BasicGet.Request with example values
      BasicGet.Request(
        name = "Alexis",
        email = "alexis@wiringbits.net"
      )
    )
  )
  .out(
    jsonBody[Basic.Response].example( // returns a JSON body of type BasicGet.Response with example values
      BasicGet.Response(
        message = "Hello Alexis!"
      )
    )
  )
```

Api models must have an `implicit Schema` defined, for example:

```scala
Schema
  .derived[Response]
  .name(Schema.SName("BasicResponse"))
  .description("Says hello to the user")
```

And then integrate the endpoint to the [ApiRouter](../server/src/main/scala/controllers/ApiRouter.scala) file:

```scala
object ApiRouter {
  private def routes(implicit ec: ExecutionContext): List[AnyEndpoint] = List(
    basicPostEndpoint
  )
}
```

## Endpoint user authentication details

We use Play Session cookie for user authentication, this is a cookie that's stored securely and is sent on every request, this cookie is used to identify the user and to check if the user is authenticated.

Any endpoint that requieres user authentication must include our implicit [userAuth](../lib/api/shared/src/main/scala/net/wiringbits/api/endpoints/package.scala) handler and convert the endpoint `val` to `def` that receives an implicit handler `implicit
authHandler: ServerRequest => Future[UUID]`, for example:

[//]: # (TODO: change Future[UUID] to Future[UserId] after mergin typo)
```scala
def basicEndpoint(implicit authHandler: ServerRequest => Future[UUID]) = endpoint.get
  .in(userAuth)
```

For more information about creating endpoints, please check the [tapir documentation](https://tapir.softwaremill.com/en/latest/).
