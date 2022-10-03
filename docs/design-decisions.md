# Design decisions
This document explains why we took certain design decisions on how the project is built/structured.

## 2022/Aug - Avoid default parameter in most cases
We commonly deal with models that are similar but belong to a different domain, [chimney](https://scalalandio.github.io/chimney) help us to transform those models from one domain to another, while this tool is handy, it does not play well with default values in arguments.

Take this snippet as an example:

```scala
case class CreateUserApiRequest(name: String, age: Option[Int])

case class CreateUserData(name: String, yearsOld: Option[Int] = None)

def transform(request: CreateUserApiRequest): CreateUserData = request.into[CreateUserData].transform
```

While the `transform` function would succeed, the `age` value will never become the `yearsOld` value, if there wasn't a default value, we'd get a compile error which would give us a chance to fix the problem (`request.into[CreateUserData].withFieldRenamed(_.age, _.yearsOld).transform`).

Still, there can be exceptions:
- The http API layer usually gets default values when adding a new parameter to an API method, this way, we keep backwards compatibility to support old API clients.


## 2022/Apr - Naming conventions for api/data models

The project follows some principles from DDD (Domain Driven Design), we use different models for different layers even if they look quite similar.

For example, when creating an endpoint that creates a user, we'd end up with models like `models.api.CreateUser` and `models.data.CreateUser`, in theory, we could be disciplined enough to follow the conventions and refer to the models with the package from the domain we are interested in, like:

```scala
import models._

def createUserApi(model: api.CreateUser)
def createUserData(model: data.CreateUser)
```

Unfortunately, IDE's automatically import the models from specific packages, which commonly causes conflicts because IDE imported the data model while we require the api one, there are pieces where we even need to deal with both.

Then, it seems more practical to just include the domain name at the model name instea, like `models.data.CreateUserData`, this way, IDE's won't have ambiguous choices.


## 2022/Jan/23 - Avoid creating postgres extensions in evolution scripts

While it is very handy to keep all the necessary sql operations at the evolution scripts, it is a good practice to limit the permissions for the database user, in fact, in AWS RDS, the default user won't be able to create some extensions. Solving such an issue can be annoying, hence, the pain is being shifted to the local environments instead.

In short, when creating a local database, you will see yourself creating extensions manually like `CREATE EXTENSION CITEXT;`

Ref: 0439d7b3159e01f886ceeb3f0ff0d2d471f5e304
