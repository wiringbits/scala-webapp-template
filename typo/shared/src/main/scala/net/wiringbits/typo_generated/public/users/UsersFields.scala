/**
 * File has been automatically generated by `typo`.
 *
 * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
 */
package net
package wiringbits
package typo_generated
package public
package users

import net.wiringbits.common.models.Email
import net.wiringbits.common.models.InstantCustom
import net.wiringbits.common.models.Name
import net.wiringbits.common.models.UUIDCustom
import typo.dsl.SqlExpr.Field
import typo.dsl.SqlExpr.IdField
import typo.dsl.SqlExpr.OptField

trait UsersFields[Row] {
  val userId: IdField[/* user-picked */ UUIDCustom, Row]
  val name: Field[/* user-picked */ Name, Row]
  val lastName: OptField[String, Row]
  val email: Field[/* user-picked */ Email, Row]
  val password: Field[String, Row]
  val createdAt: Field[/* user-picked */ InstantCustom, Row]
  val verifiedOn: OptField[/* user-picked */ InstantCustom, Row]
}
object UsersFields extends UsersStructure[UsersRow](None, identity, (_, x) => x)

