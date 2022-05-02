package io.swagger.annotations

import scala.annotation.Annotation

// Dummy annotation to allow using swagger-annotations in our sjs compiled models
// Based on https://github.com/swagger-api/swagger-core
@SuppressWarnings(Array("org.wartremover.warts.ArrayEquals"))
final case class ApiModel(
    value: String = "",
    description: String = "",
    parent: Class[_] = classOf[Unit],
    discriminator: String = "",
    subTypes: Array[Class[_]] = Array.empty,
    reference: String = ""
) extends Annotation
