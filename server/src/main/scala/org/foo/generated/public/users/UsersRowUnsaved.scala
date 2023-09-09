/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package org.foo.generated.public.users

import org.foo.generated.customtypes.Defaulted
import org.foo.generated.customtypes.TypoOffsetDateTime
import org.foo.generated.customtypes.TypoUnknownCitext
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import scala.collection.immutable.ListMap
import scala.util.Try

/** This class corresponds to a row in table `public.users` which has not been persisted yet */
case class UsersRowUnsaved(
  userId: UsersId,
  name: String,
  lastName: Option[String],
  email: TypoUnknownCitext,
  password: String,
  verifiedOn: Option[TypoOffsetDateTime],
  /** Default: now() */
  createdAt: Defaulted[TypoOffsetDateTime] = Defaulted.UseDefault
) {
  def toRow(createdAtDefault: => TypoOffsetDateTime): UsersRow =
    UsersRow(
      userId = userId,
      name = name,
      lastName = lastName,
      email = email,
      password = password,
      verifiedOn = verifiedOn,
      createdAt = createdAt match {
                    case Defaulted.UseDefault => createdAtDefault
                    case Defaulted.Provided(value) => value
                  }
    )
}
object UsersRowUnsaved {
  implicit lazy val reads: Reads[UsersRowUnsaved] = Reads[UsersRowUnsaved](json => JsResult.fromTry(
      Try(
        UsersRowUnsaved(
          userId = json.\("user_id").as(UsersId.reads),
          name = json.\("name").as(Reads.StringReads),
          lastName = json.\("last_name").toOption.map(_.as(Reads.StringReads)),
          email = json.\("email").as(TypoUnknownCitext.reads),
          password = json.\("password").as(Reads.StringReads),
          verifiedOn = json.\("verified_on").toOption.map(_.as(TypoOffsetDateTime.reads)),
          createdAt = json.\("created_at").as(Defaulted.reads(TypoOffsetDateTime.reads))
        )
      )
    ),
  )
  implicit lazy val writes: OWrites[UsersRowUnsaved] = OWrites[UsersRowUnsaved](o =>
    new JsObject(ListMap[String, JsValue](
      "user_id" -> UsersId.writes.writes(o.userId),
      "name" -> Writes.StringWrites.writes(o.name),
      "last_name" -> Writes.OptionWrites(Writes.StringWrites).writes(o.lastName),
      "email" -> TypoUnknownCitext.writes.writes(o.email),
      "password" -> Writes.StringWrites.writes(o.password),
      "verified_on" -> Writes.OptionWrites(TypoOffsetDateTime.writes).writes(o.verifiedOn),
      "created_at" -> Defaulted.writes(TypoOffsetDateTime.writes).writes(o.createdAt)
    ))
  )
}
