package io.swagger.annotations

import scala.annotation.Annotation

// Dummy annotation to allow using swagger-annotations in our sjs compiled models
// Based on https://github.com/swagger-api/swagger-core
//
// NOTE: Due to a compiler bug, we must define the values in the order we expect them to be used
//       otherwise, we end up with compile warnings - https://github.com/scala/bug/issues/7656
@SuppressWarnings(Array("org.wartremover.warts.ArrayEquals"))
final case class ApiModel(
    value: String = "",
    description: String = "",
    parent: Class[_] = classOf[Unit],
    discriminator: String = "",
    subTypes: Array[Class[_]] = Array.empty,
    reference: String = ""
) extends Annotation
