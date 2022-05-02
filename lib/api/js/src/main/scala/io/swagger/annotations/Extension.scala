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
//
// NOTE: Due to a compiler bug, we must define the values in the order we expect them to be used
//       otherwise, we end up with compile warnings - https://github.com/scala/bug/issues/7656
@SuppressWarnings(Array("org.wartremover.warts.ArrayEquals"))
case class Extension(name: String = "", properties: Array[ExtensionProperty]) extends scala.annotation.Annotation
