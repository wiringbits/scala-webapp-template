/** Copyright 2016 SmartBear Software <p> Licensed under the Apache License, Version 2.0 (the "License"); you may not
  * use this file except in compliance with the License. You may obtain a copy of the License at <p>
  * http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  * either express or implied. See the License for the specific language governing permissions and limitations under the
  * License.
  */

package io.swagger.annotations

// Dummy annotation to allow using swagger-annotations in our sjs compiled models
// Based on https://github.com/swagger-api/swagger-core
@SuppressWarnings(Array("org.wartremover.warts.ArrayEquals"))
case class ApiModelProperty(
    value: String = "",
    name: String = "",
    allowableValues: String = "",
    access: String = "",
    notes: String = "",
    dataType: String = "",
    required: Boolean = false,
    position: Int = 0,
    hidden: Boolean = false,
    example: String = "",
    readOnly: Boolean = false,
    accessMode: ApiModelProperty.AccessMode.AccessMode = ApiModelProperty.AccessMode.AUTO,
    reference: String = "",
    allowEmptyValue: Boolean = false,
    extensions: Array[Extension] = Array(
      Extension(properties = Array(ExtensionProperty(name = "", value = "")))
    )
) extends scala.annotation.Annotation

/** Adds and manipulates data of a model property.
  */
object ApiModelProperty {
  object AccessMode extends Enumeration {
    type AccessMode = Value
    val AUTO, READ_ONLY, READ_WRITE = Value
  }
}
